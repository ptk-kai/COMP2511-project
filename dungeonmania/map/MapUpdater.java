package dungeonmania.map;

import java.util.List;
import java.util.Map;

import dungeonmania.Config.Config;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.MovingEntity.Spider;
import dungeonmania.util.Position;

public class MapUpdater {

    public static void spawnSpider(Map<Position, List<Entity>> entityMap, Player player, Config config) {
        Position randomPosition = MapHelper.getRandomPositionNoAdjacent(entityMap, player.getPosition());
        Spider spider = new Spider(randomPosition, config.getSpiderHealth(), config.getSpiderAttack());
        MapHelper.mapAddEntity(entityMap, randomPosition, spider);
    }

    public static void removeEntitiesFromMap(Map<Position, List<Entity>> entityMap, List<Entity> entitiesToRemove) {
        entitiesToRemove.stream().forEach(entityToDelete -> {
            entityMap.values().forEach(entList -> {
                Entity toDelete = entList.stream().filter(entity -> {
                    String id = entityToDelete.getId();
                    return entity.getId().contains(id);
                })
                    .findFirst().orElse(null);
                entList.remove(toDelete);
            });
        });
    }
}
