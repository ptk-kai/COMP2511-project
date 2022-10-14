package dungeonmania.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


import dungeonmania.Inventory;
import dungeonmania.Config.Config;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Collectable.*;
import dungeonmania.Entity.MovingEntity.BattleOpponent;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.Entity.StaticEntities.*;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Player extends BattleOpponent {
    private double health;
    private double attack;
    private Inventory inventory;
    private boolean invisible = false;
    private boolean invincible = false;
    private BuffItem currentBuff;
    private Position lastPos = null;
    private List<BuffItem> buffItems = new ArrayList<>();
    private List<BattleOpponent> allies = new ArrayList<>();
    

    public Player(Position position, Config config) {
        super(position, DungeonMacro.PLAYER, false, config.getPlayerAttack(), config.getPlayerAttack());
        this.health = config.getPlayerHealth();
        this.attack = config.getPlayerAttack();
        this.inventory = new Inventory(this, config);
    }

    public int getBribeRadius() {
        Config config = inventory.getConfig();
        return config.getBribeRadius();
    }

    public void useTreasure(int number) {
        inventory.useTreasure(number);
    }

    public Collectable getHasSword() {
        return this.inventory.getFirstItemInType(DungeonMacro.SWORD);
    }

    public Collectable getHasShield() {
        return this.inventory.getFirstItemInType(DungeonMacro.SHIELD);
    }

    public Collectable getHasBow() {
        return this.inventory.getFirstItemInType(DungeonMacro.BOW);
    }

    public Collectable getHasMidNightArmour() {
        return this.inventory.getFirstItemInType(DungeonMacro.MIDNIGHT);
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public List<Collectable> getInventoryItems() {
        return this.inventory.getInventory();
    }

    public boolean isInvisible() {
        return this.invisible;
    }

    public void setInvisible(boolean invisible) {
        this.invisible = invisible;
    }

    public boolean isInvincible() {
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
    }


    public BuffItem getCurrentBuff() {
        return currentBuff;
    }

    public void setCurrentBuff(BuffItem currentBuff) {
        this.currentBuff = currentBuff;
    }


    public void setLastPos(Position pos) {
        this.lastPos = pos;
    }

    public Position getLastPos() {
        return this.lastPos == null ? this.getPosition() : this.lastPos ;
    }

    public List<BattleOpponent> getAllies() {
        return this.allies;
    }

    public void addAlly(BattleOpponent enemyEntity) {
        this.allies.add(enemyEntity);
    }

    @Override
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction, ActivitySubscriber subscriber) {
        Position potentialNewPosition = getPosition().translateBy(direction.getOffset());
        List<Entity> entitiesAtNewPosition = entityMap.getOrDefault(potentialNewPosition, new ArrayList<>());

        if (movementBlocked(entitiesAtNewPosition)) {
            return this.getPosition();
        }

        boolean newPositionIsBoulder = MapHelper.checkListHasTypeEntity(entitiesAtNewPosition, DungeonMacro.BOULDER);
        boolean newPositionIsDoor = MapHelper.checkListHasTypeEntity(entitiesAtNewPosition, DungeonMacro.DOOR) ||
            MapHelper.checkListHasTypeEntity(entitiesAtNewPosition, DungeonMacro.SWITCH_DOOR);
        boolean newPositionIsPortal = MapHelper.checkListHasTypeEntity(entitiesAtNewPosition, DungeonMacro.PORTAL);
        boolean newPositionIsZombieSpawner = MapHelper.checkListHasTypeEntity(entitiesAtNewPosition,DungeonMacro.ZOMBIE_SPAWNER);
        boolean newPositionIsBomb = MapHelper.checkListHasTypeEntity(entitiesAtNewPosition, DungeonMacro.BOMB);
        boolean newPositionTimePortal = MapHelper.checkListHasTypeEntity(entitiesAtNewPosition, DungeonMacro.TIME_PORTAL);

        if (newPositionIsBomb) {
            Bomb bomb = (Bomb) MapHelper.getFirstEntityInType(entitiesAtNewPosition, DungeonMacro.BOMB);
            if (bomb.checkUsed()) {
                return this.getPosition(); // bomb is used then it will block player's movement and can't pick up
            }
        }

        if (newPositionIsBoulder) {
            // No need to think about two or more boulder at same position
            // This is prevented by boulder move.
            Boulder boulder = (Boulder) MapHelper.getFirstEntityInType(entitiesAtNewPosition, DungeonMacro.BOULDER);
            Position originalPosition = boulder.getPosition();
            Position newPosition = boulder.move(entityMap, direction, subscriber);

            if (originalPosition.equals(newPosition)) {
                // boulder move failed. player can't move to boulder's location
                return this.getPosition();
            }
        }

        if (newPositionIsDoor) {
            Door door = (Door) MapHelper.getFirstEntityInType(entitiesAtNewPosition, DungeonMacro.DOOR);
            Sunstone sunstone = (Sunstone) this.inventory.getFirstItemInType(DungeonMacro.SUNSTONE);
            if (!door.checkDoorOpen()) {
                if (!door.checkUsingSunStone(sunstone)) {
                    Key key = (Key) this.inventory.getFirstItemInType(DungeonMacro.KEY);
                    if (!door.checkMatchKey(key)) {
                        return this.getPosition();
                    }
                    subscriber.updateHistoricalDoorStatus(door);
                }
            }
        }

        if (newPositionIsPortal) {
            // Assumption : No two portal as same pos
            Portal portal = (Portal) MapHelper.getFirstEntityInType(entitiesAtNewPosition, DungeonMacro.PORTAL);
            Position teleportPos = portal.getPossiblePos(entityMap);
            if (teleportPos == null) {
                return this.getPosition();
            }
            potentialNewPosition = teleportPos;
        }

        if (newPositionIsZombieSpawner) {
            if (!checkDestroySpawner()) {
                return this.getPosition();
            }
        }

        if (newPositionTimePortal) {
            subscriber.notifySubscriberOfTimeTravel(30);
        }

        MapHelper.mapRemoveEntity(entityMap, getPosition(), this);
        this.setLastPos(getPosition());
        this.setPosition(potentialNewPosition);
        MapHelper.mapAddEntity(entityMap, potentialNewPosition, this);
        updateBuff();
        return potentialNewPosition;
    }

    private void updateBuff() {
        if (buffItems.isEmpty()) {
            this.setInvisible(false);
            this.setInvincible(false);
            return;
        }

        if (buffItems.get(0).getDuration() == 0) {
            buffItems.remove(0);
            if (buffItems.isEmpty()) {
                this.setInvisible(false);
                this.setInvincible(false);
                return;
            }
        }

        BuffItem firstBuffItem = buffItems.get(0);
        int itemDuration = firstBuffItem.getDuration();
        if (DungeonMacro.INVISIBILITY.equals(firstBuffItem.getType())) {
            this.setInvisible(true);
            this.setInvincible(false);
            
        } else if (DungeonMacro.INVINCIBILITY.equals(firstBuffItem.getType())){
            this.setInvincible(true);
            this.setInvisible(false);
        }

        this.currentBuff = firstBuffItem;
        firstBuffItem.setDuration(itemDuration - 1);
    }

    public int getMindControlDuration() {
        Config config = inventory.getConfig();
        return config.getMindControlDuration();
    }

    public void rmControlledAlly(Mercenary ally) {
        this.allies.remove(ally);
    }

    public boolean checkHasSceptre() {
        return inventory.checkHasSceptre();
    }

    @Override
    public boolean movementBlocked(List<Entity> entities) {
        return entities.stream().anyMatch(entity -> entity instanceof Wall);
    }

    public boolean collectItem(Collectable item) {
        return this.inventory.pickUpItem(item);
    }

    public void consumeItem(Collectable item) {
        this.inventory.removeItem(item);
    }

    public boolean craftItem(String item) {
        return this.inventory.tryCraftItem(item);
    }

    public List<String> buildables() {
        List<String> buildableItemTypes = new ArrayList<>();

        if (getInventory().checkCraftBow()) {
            buildableItemTypes.add(DungeonMacro.BOW);
        }

        if (getInventory().checkCraftShield()) {
            buildableItemTypes.add(DungeonMacro.SHIELD);
        }

        if (getInventory().checkCraftSceptre()) {
            buildableItemTypes.add(DungeonMacro.SCEPTRE);
        }

        if (getInventory().checkCraftMidNightArmour()) {
            buildableItemTypes.add(DungeonMacro.MIDNIGHT);
        }

        return buildableItemTypes;
    }

    public int getNumTreasurePicked() {
        return this.inventory.getNumTreasurePicked();
    }

    public void queueEffects(BuffItem potion) {
        buffItems.add(potion);
        updateBuff();
    }

    public boolean checkDestroySpawner() {
        return (!Objects.isNull(getHasSword()) || !Objects.isNull(getHasBow()));
    }

    /**
     * Return calculated ally attack or defence buffs
     * @return array containing ally buffs, first item is ally attack buff, second item is ally defence buff
     */
    public int[] calculateAllyBuff() {
        int atkBuff = 0;
        int defBuff = 0;
        
        List<Mercenary> allyMercenaryInRange = this.allies.stream()
            .filter(entity -> this.getPosition().inRange(this.getBribeRadius(), entity.getPosition()))
            .map(entity -> (Mercenary) entity)
            .collect(Collectors.toList());

        atkBuff = allyMercenaryInRange
            .stream()
            .map(ally -> ally.getAttackBuff())
            .reduce(0, (a,b) -> a+b);
        
        defBuff = allyMercenaryInRange
            .stream()
            .map(ally -> ally.getDefenceBuff())
            .reduce(0, (a,b) -> a+b);
        
        return new int[]{atkBuff, defBuff};   
    }

    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {
        return;
    }

    @Override
    public void actionPlayerSamePosition(Player player, ActivitySubscriber subscriber) {
        if (this.equals(player)) {
            return;
        }
        if (this.invisible || player.invisible) {
            return;
        }
        if (this.inventory.checkHasItem(DungeonMacro.SUNSTONE) || player.inventory.checkHasItem(DungeonMacro.SUNSTONE)) {
            return;
        }
        if (this.inventory.checkHasItem(DungeonMacro.MIDNIGHT) || player.inventory.checkHasItem(DungeonMacro.MIDNIGHT)) {
            return;
        }
        
        super.actionPlayerSamePosition(player, subscriber);
	}
}