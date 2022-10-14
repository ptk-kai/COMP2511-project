package dungeonmania.map;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;

import dungeonmania.Config.Config;
import dungeonmania.Entity.*;
import dungeonmania.Entity.MovingEntity.Battle;
import dungeonmania.Entity.MovingEntity.BattleOpponent;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.Entity.StaticEntities.*;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.Interfaces.Logic;
import dungeonmania.Interfaces.Tick;
import dungeonmania.goals.*;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class DungeonMap implements ActivitySubscriber, Serializable {

    private String dungeonId;
    private String dungeonName;
    private Goal goal;
    private Map<Position, List<Entity>> entityMap = new HashMap<>();
    private Player player;
    private Config config;
    private int enemyKillCount = 0;
    private int numTicks = 0;
    private List<Battle> battles = new ArrayList<>();
    private boolean inThePast = false;
    private int numTicksToRewind = 0;
    private int ticksRewinded = 0;
    private MapCache mapCache = new MapCache();
    private HistoryCaretaker historyCaretaker = new HistoryCaretaker();
    private boolean timeTravelled = false;

    public DungeonMap(String dungeonName, String configName) {
        this.dungeonId = UUID.randomUUID().toString();
        this.dungeonName = dungeonName;
        this.config = MapCreation.loadConfigFile(configName);
        MapCreation.loadMapFile(dungeonName, this);
        historyCaretaker.addHistoryAtTick(numTicks, MapHelper.getEntityList(entityMap));;
    }

    public DungeonMap(String dungeonName, String configName, JsonObject jsonObject) {
        this.dungeonId = dungeonName;
        this.dungeonName = dungeonName;
        this.config = MapCreation.loadConfigFile(configName);
        MapCreation.createMapEntityFromJsonObject(this, jsonObject);
        historyCaretaker.addHistoryAtTick(numTicks, MapHelper.getEntityList(entityMap));;
    }

    public DungeonResponse toDungeonResponse() {
        List<EntityResponse> entities = MapHelper.getEntityList(this.entityMap).stream()
            .map(entity -> entity.toEntityResponse()).collect(Collectors.toList());
        List<ItemResponse> inventory = player.getInventoryItems().stream()
            .map(item -> item.toItemResponse()).collect(Collectors.toList());
        List<BattleResponse> battles = this.battles.stream()
            .map(battle -> battle.toBattleResponse()).collect(Collectors.toList());
        List<String> buildables = player.buildables();
        String goal = this.goal.incompleteGoals();
        DungeonResponse dungeonResponse = new DungeonResponse(dungeonId, dungeonName, entities, inventory, battles, buildables, goal);
        return dungeonResponse;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    public Goal getGoal() {
        return this.goal;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

    public Map<Position,List<Entity>> getEntityMap() {
        return this.entityMap;
    }

    public void setEntityMap(Map<Position,List<Entity>> entityMap) {
        this.entityMap = entityMap;
    }

    public int getNumTicks() {
        return numTicks;
    }

    public void setNumTicks(int ticks) {
        this.numTicks = ticks;
    }

    public boolean checkExitGoal() {
        Player curPlayer = this.getPlayer();
        Exit curExit = MapHelper.getExit(this.entityMap);
        if (curExit != null) {
            return curExit.checkExit(curPlayer);
        }
        return false;
    }

    public boolean checkTreasureGoal() {
        return this.getPlayer().getNumTreasurePicked() >= this.config.getTreasureGoal();
    }

    public boolean checkEnemyGoal() {
        return this.enemyKillCount >= this.config.getEnemyGoal();
    }

    public boolean checkNoSpawner() {
        return MapHelper.getZombieSpawner(this.entityMap).isEmpty();
    }

    public boolean checkBoulderGoal() {
        return MapHelper.getFloorSwitchs(this.entityMap)
            .stream()
            .allMatch(floorSwitch -> floorSwitch.checkActiveFloorSwitch(this.entityMap));
    }
    /**
     * Attempts to move the player into a new position.
     * @param movementDirection
     * @return true if the move caused the player to time travel
     */
    public boolean attemptToMovePlayer(Direction movementDirection) {
        boolean beforeMoveInPast = inThePast;
        Position playerNewPosition = this.player.move(getEntityMap(), movementDirection, this);
        List<Entity> entitiesAtNewPosition = entityMap.getOrDefault(playerNewPosition, new ArrayList<>());
        entitiesAtNewPosition.forEach(entity -> entity.actionPlayerSamePosition(player, this));
        boolean afterMoveInPast = inThePast;

        updateMapAfterAction();
        if (beforeMoveInPast == false && afterMoveInPast == true) {
            return true;
        }

        return false;
    }

    public void notifyEntityOfTick() {
        MapHelper.getEntityList(entityMap).stream()
            .filter(entity -> entity instanceof Tick)
            .filter(entity -> !MapHelper.checkEntityTrappedInSwamp(entityMap, entity))
            .forEach(entity -> ((Tick) entity).tick(getEntityMap(), this.player));
        
        MapHelper.getEntityList(entityMap).stream()
            .filter(entity -> entity instanceof Logic)
            .forEach(entity -> ((Logic) entity).getSingalStatus(this.entityMap, new ArrayList<Entity>(), true));
        
        List<Entity> entitiesAtPlayerPosition = entityMap.getOrDefault(player.getPosition(), new ArrayList<>());
        entitiesAtPlayerPosition.forEach(entity -> {
            entity.actionPlayerSamePosition(player, this);
        });

        updateMapAfterAction();

        setNumTicks(getNumTicks() + 1);
        if (config.getSpiderSpawnRate() != 0 && numTicks % config.getSpiderSpawnRate() == 0) {
            MapUpdater.spawnSpider(this.entityMap, this.player, this.config);
        }

        MapHelper.getEntityList(entityMap).stream()
            .filter(entity -> entity instanceof SwampTile)
            .forEach(entity -> ((SwampTile) entity).swampTickOperation(entityMap));

        if (inThePast) {
            //update historical mercenary position in case it was bribed by player
            MapHelper.getEnemies(entityMap).stream()
                .filter(enemy -> enemy instanceof Mercenary)
                .map(merc -> (Mercenary) merc)
                .forEach(merc -> updateHistoricalEntityPosition(merc));

            enactHistoricalTick();
        }

        historyCaretaker.addHistoryAtTick(numTicks, MapHelper.getEntityList(entityMap));;
    }

    public void updateMapAfterAction() {
        //update map
        MapUpdater.removeEntitiesFromMap(entityMap, mapCache.getEntitiesToDelete());

        //update battle
        updateBattleDetails(mapCache.getBattlesCacheForOneTick());

        //update history
        historyCaretaker.removeEntitiesFromHistory(mapCache.getHistoricalEntitiesToDelete());

        mapCache.clearAll();
    }

    private void updateBattleDetails(List<Battle> battleCache) {
        battleCache.stream().forEach(battle -> {
            BattleOpponent enemy = battle.getEnemy();
            if (enemy.getHealth() <= 0) {
                MapHelper.mapRemoveEntity(entityMap, enemy.getPosition(), enemy);
                this.enemyKillCount = enemyKillCount + 1;
            }

            if (player.getHealth() <= 0) {
                MapHelper.mapRemoveEntity(entityMap, player.getPosition(), player);
            }

            if (enemy.getId().contains("past")) {
                mapCache.addHistoricalEntityToDelete(enemy);
            }
        });
        this.battles.addAll(mapCache.getBattlesCacheForOneTick());
    }

    /**
     * Travels back in time
     * Assumes everything in the old map can be collected again
     * Assumes old player moves into portal it would disappear from map
     * Assumes old player will replay any build actions and new player can build
     * Assumes old player's goal achievements carry on to the new player's achievements
     * Assumes old player cant interact with enemies anymore - enemies would only attack the new player
     * Assumes old player's invalid moves are ignored - when they fail to build etc
     * Assumes obstructions to old player movements will not have any effect on old player
     * Assumes time travel is not possible when player is in the past
     * Assumes battling with old player does not take into account any of the old player's combat items (swords, bows, shields etc)
     * Assumes enemy will track old player until it disappears from the map - then it would track the new player
     * Assumes new player invincibility still kills enemies - but enemies would not run away from new player in the past since it tracks the older_player
     * Assumes while in the past, new player invisibility doesn't affect enemies - because enemies track older_player in the past
     * @param ticks number of ticks to travel back in time to
     */
    public void initiateTimeTravel(int ticksToRewind) {
        if (timeTravelled) {
            throw new IllegalArgumentException("Cannot time travel again");
        }

        this.inThePast = true;
        this.numTicksToRewind = ticksToRewind;
        this.ticksRewinded = 0;

        // get history at num ticks
        int key = historyCaretaker.getHistorySize() - this.numTicksToRewind - 1;
        Map<Position, List<Entity>> historicalMapAtNthTick = historyCaretaker.getHistoryAsMapAtTick(key);
        this.setEntityMap(historicalMapAtNthTick);
        MapHelper.mapAddEntity(entityMap, this.player.getPosition(), this.player);

        //save history and set it as when time travel started
        this.numTicks = numTicks + 1;
        historyCaretaker.addHistoryAtTick(numTicks, MapHelper.getEntityList(entityMap));
        timeTravelled = true;
    }

    /**
     * Every time the player ticks, call this function to move the old player if inThePast = true;
     */
    public void enactHistoricalTick() {
        if (this.ticksRewinded == this.numTicksToRewind) {
            this.inThePast = false;
            // when player gets to its time travel tick it would disappear
            Entity oldPlayer = MapHelper.getEntityList(entityMap).stream()
                .filter(entity -> entity instanceof Player && entity.getId().contains("past"))
                .findFirst()
                .orElse(null);
            if (Objects.nonNull(oldPlayer)) {
                MapHelper.rmGivenEntity(entityMap, oldPlayer.getPosition(), oldPlayer);
                historyCaretaker.removeEntitiesFromHistory(Arrays.asList(oldPlayer));
            }
            return;
        }

        // replace entityMap with latest historical tick entities
        int key = historyCaretaker.getHistorySize() - this.numTicksToRewind - 1;
        Map<Position, List<Entity>> historicalMapAtNthTick = historyCaretaker.getHistoryAsMapAtTick(key);
        this.setEntityMap(historicalMapAtNthTick);
        MapHelper.mapAddEntity(entityMap, this.player.getPosition(), this.player);

        this.ticksRewinded = ticksRewinded + 1;
    }

    @Override
    public void notifySubscriberOfBattle(Battle battle) {
        mapCache.addBattle(battle);
    }

    @Override
    public void removeHistoricalEntities(Entity entity) {
        if (inThePast) {
            mapCache.addHistoricalEntityToDelete(entity);
        }
    }

    @Override
    public void removeEntityFromMap(Entity entity) {
        mapCache.addEntityToDeleteFromMap(entity);
        
    }

    @Override
    public void updateHistoricalEntityPosition(Entity entity) {
        historyCaretaker.updateHistoricalEntityPosition(entity);
    }

    @Override
    public void updateHistoricalDoorStatus(Door entity) {
        historyCaretaker.updateHistoricalDoorStatus(entity);
    }

    @Override
    public void notifySubscriberOfTimeTravel(int ticksToRewind) {
        if (ticksToRewind > this.getNumTicks()) {
            ticksToRewind = this.getNumTicks() + 1;
        }
        initiateTimeTravel(ticksToRewind - 1);
    }

    @Override
    public void updateHistoricalMercenary(Mercenary merc) {
        historyCaretaker.updateHistoricalMercenaryStatus(merc);
    }
}
