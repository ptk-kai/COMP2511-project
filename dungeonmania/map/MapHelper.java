package dungeonmania.map;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.*;
import dungeonmania.Entity.Collectable.*;
import dungeonmania.Entity.MovingEntity.*;
import dungeonmania.Entity.StaticEntities.*;
import dungeonmania.util.Position;
import dungeonmania.Interfaces.Logic;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapHelper {

    public static List<Entity> getEntityList(Map<Position, List<Entity>> entityMap) {
        List<Entity> entityList = entityMap
            .values()
            .stream()
            .flatMap(List::stream)
            .collect(Collectors.toList());
        return entityList;
    }

    public static Entity getEntityById(Map<Position, List<Entity>> entityMap, String id) {
        return getEntityList(entityMap).stream()
            .filter(entity -> entity.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public static Collectable getEntityFromInventroy(Player player, String id) {
        return player.getInventoryItems().stream()
            .filter(item -> item.getId().equals(id))
            .findFirst()
            .orElse(null);
    }

    public static void rmGivenEntity(Map<Position, List<Entity>> entityMap, Position pos, Entity givenEntity) {
        entityMap.get(pos).remove(givenEntity);
    }

    public static void removeEntityById(Map<Position, List<Entity>> entityMap, Position pos, Entity givenEntity) {
        List<Entity> entitiesAtPos = entityMap.get(pos);
        List<Entity> matched = entitiesAtPos.stream().filter(entity -> entity.getId().equals(givenEntity.getId())).collect(Collectors.toList());
        entitiesAtPos.removeAll(matched);
    }

    // This function will remove all entities at given position except PLAYER!!
    public static void rmEntitiesAtPos(Map<Position, List<Entity>> entityMap, Position pos) {
        Entity currPlayer = MapHelper.getFirstEntityInType(MapHelper.getEntityList(entityMap), DungeonMacro.PLAYER);
        Position playerPos = currPlayer.getPosition();
        if (pos.equals(playerPos)) {
            List<Entity> newValue = new ArrayList<>();
            newValue.add(currPlayer);
            entityMap.replace(pos, newValue);
        } else {
            entityMap.remove(pos);
        }
    }

    public static List<ZombieToastSpawner> getZombieSpawner(Map<Position, List<Entity>> entityMap) {
        return getEntityList(entityMap)
            .stream()
            .filter(obj -> obj instanceof ZombieToastSpawner)
            .map(obj -> (ZombieToastSpawner) obj)
            .collect(Collectors.toList());
    }

    public static List<BattleOpponent> getEnemies(Map<Position, List<Entity>> entityMap) {
        return getEntityList(entityMap)
            .stream()
            .filter(obj -> obj instanceof BattleOpponent)
            .map(obj -> (BattleOpponent) obj)
            .collect(Collectors.toList());
    }

    public static Player getPlayer(Map<Position, List<Entity>> entityMap) {
        return getEntityList(entityMap)
            .stream()
            .filter(obj -> obj instanceof Player)
            .map(obj -> (Player) obj)
            .findFirst()
            .orElse(null);
    }

    public static List<Treasure> getTreasures(Map<Position, List<Entity>> entityMap) {
        return getEntityList(entityMap)
            .stream()
            .filter(obj -> obj instanceof Treasure)
            .map(obj -> (Treasure) obj)
            .collect(Collectors.toList());
    }

    // Based on assumption: Only one exit
    public static Exit getExit(Map<Position, List<Entity>> entityMap) {
        return getEntityList(entityMap)
            .stream()
            .filter(obj -> obj instanceof Exit)
            .map(obj -> (Exit) obj)
            .findFirst()
            .orElse(null);
    }

    public static List<Boulder> getBoulders(Map<Position, List<Entity>> entityMap) {
        return getEntityList(entityMap)
            .stream()
            .filter(obj -> obj instanceof Boulder)
            .map(obj -> (Boulder) obj)
            .collect(Collectors.toList());
    }

    public static List<FloorSwitch> getFloorSwitchs(Map<Position, List<Entity>> entityMap) {
        return getEntityList(entityMap)
            .stream()
            .filter(obj -> obj instanceof FloorSwitch)
            .map(obj -> (FloorSwitch) obj)
            .collect(Collectors.toList());
    }

    public static void mapAddEntity(Map<Position, List<Entity>> entityMap, Position pos, Entity entity) {
        List<Entity> entitiesAtPosition = entityMap.getOrDefault(pos, new ArrayList<Entity>());
        entitiesAtPosition.add(entity);
        entityMap.put(pos, entitiesAtPosition);
    }

    public static void mapRemoveEntity(Map<Position, List<Entity>> entityMap, Position pos, Entity entity) {
        List<Entity> entitiesAtPosition = entityMap.getOrDefault(pos, new ArrayList<Entity>());
        entitiesAtPosition.remove(entity);
    }

    public static Position getRandomPositionNoAdjacent(Map<Position, List<Entity>> entityMap, Position playerPosition) {
        Position randomPosition = MapHelper.getRandomPosition(playerPosition);
        List<Position> adjacentPositions = randomPosition.getAdjacentPositions();
        while (adjacentPositions.stream().anyMatch(pos -> entityMap.containsKey(pos))) {
            randomPosition = MapHelper.getRandomPosition(playerPosition);
            adjacentPositions = randomPosition.getAdjacentPositions();
        }
        return randomPosition;
    }

    public static Position getRandomPosition(Position playerPosition) {
        Random r = new Random();
        int randomX = r.ints(1, playerPosition.getX() - 10, playerPosition.getX() + 10).findFirst().getAsInt();
        int randomY = r.ints(1, playerPosition.getY() - 10, playerPosition.getY() + 10).findFirst().getAsInt();
        Position position = new Position(randomX, randomY);
        return position;
    }

    public static boolean checkListHasTypeEntity(List<Entity> entities, String type) {
        Stream<Entity> stream = entities.stream();
        if (type.equals("zombie_toast")) {
            stream.filter(it -> !it.getType().startsWith("zombie_toast_spawner"));
        }
        return stream.anyMatch(entity -> entity.getType().startsWith(type));
    }

    public static List<Portal> getPortals(List<Entity> entities) {
        return entities.stream()
            .filter(entity -> entity instanceof Portal)
            .map(obj -> (Portal) obj)
            .collect(Collectors.toList());
    }

    // Used when you already have entityList for a specifc postion
    public static Entity getFirstEntityInType(List<Entity> entities, String type) {
        Stream<Entity> stream = entities.stream();
        if (type.equals("zombie_toast")) {
            stream.filter(it -> !it.getType().startsWith("zombie_toast_spawner"));
        }

        return stream.filter(entity -> entity.getType().startsWith(type))
            .findFirst()
            .orElse(null);
    }

    public static Map<Position, List<Entity>> convertEntityListToMap(List<Entity> entities) {
        return entities.stream()
            .collect(Collectors.toMap(Entity::getPosition, 
                                        entity->new ArrayList<Entity>(Arrays.asList(entity)), 
                                        (existing, clash) -> {
                                            existing.addAll(clash); 
                                            return existing;
                                        }, 
                                        HashMap::new));
    }
    public static List<Entity> getLogicTransmitEntityList(List<Entity> entities) {

        return entities.stream()
            .filter(e -> e instanceof FloorSwitch || e instanceof Wire)
            .filter(e -> !((Logic) e).getLogic().equals(DungeonMacro.XOR))
            .collect(Collectors.toList());
    }

    public static boolean checkEntityTrappedInSwamp(Map<Position, List<Entity>> entityMap, Entity currEntity) {
        
        List<Entity> currPosEntities = entityMap.getOrDefault(currEntity.getPosition(), new ArrayList<>());
        SwampTile currPosSwamp = (SwampTile) getFirstEntityInType(currPosEntities, DungeonMacro.SWAMP);
        if (currPosSwamp != null) {
            return currPosSwamp.checkEntityTrapped(currEntity);
        }
        return false;
    }

}
