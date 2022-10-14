package dungeonmania.map;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.Entity.MovingEntity.Zombie;
import dungeonmania.Entity.StaticEntities.Door;
import dungeonmania.util.CopyUtil;
import dungeonmania.util.Position;

public class HistoryCaretaker implements Serializable {
    private Map<Integer, List<Entity>> historyMap = new HashMap<>();

    public int getHistorySize() {
        return historyMap.size();
    }

    public List<Entity> getHistoryAtTick(int tick) {
        return historyMap.get(tick);
    }

    public Map<Position, List<Entity>> getHistoryAsMapAtTick(int tick) {
        List<Entity> entitiesAtNthTick = getHistoryAtTick(tick);
        Map<Position, List<Entity>> entitiesMapAtNthTick = MapHelper.convertEntityListToMap(entitiesAtNthTick);
        return entitiesMapAtNthTick;
    }

    public void addHistoryAtTick(int tick, List<Entity> entitiesToSaveInHistory) {
        List<Entity> entityListDeepCopy = entitiesToSaveInHistory.stream()
            .map(ent -> CopyUtil.deepCopyObject(ent)).filter(Objects::nonNull)
            .collect(Collectors.toList());
        historyMap.put(tick, entityListDeepCopy);
    }

    public void updateHistoricalEntityPosition(Entity entity) {
        historyMap.values().stream().flatMap(List::stream).forEach(historicalEntity -> {
            String entId = entity.getId().substring(0, entity.getId().length() - 5);
            if (historicalEntity.getId().contains(entId)) {
                historicalEntity.setPosition(entity.getPosition());
            }
        });
    }

    public void updateHistoricalDoorStatus(Door entity) {
        historyMap.values().stream().flatMap(List::stream)
        .filter(historicalEntity -> historicalEntity instanceof Door)
        .map(historicalEntity -> (Door) historicalEntity)
        .forEach(historicalDoor -> {
            if (historicalDoor.getId().contains(entity.getId())) {
                historicalDoor.setDoorOpen(true);
            }
        });
    }

    public void updateHistoricalMercenaryStatus(Mercenary merc) {
        historyMap.values().stream().flatMap(List::stream)
        .filter(historicalEntity -> historicalEntity instanceof Mercenary)
        .map(historicalEntity -> (Mercenary) historicalEntity)
        .forEach(historicalMerc -> {
            if (historicalMerc.getId().contains(merc.getId())) {
                historicalMerc.setState(merc.getState());
            }
        });
    }

    public void removeEntitiesFromHistory(List<Entity> entitiesToRemove) {
        if (entitiesToRemove.isEmpty()) {
            return;
        }
        List<String> idsToRemove = entitiesToRemove.stream()
            .map(entity -> entity.getId()).collect(Collectors.toList());
        List<Entity> historicalToRemove = historyMap.values().stream().flatMap(List::stream)
            .filter(ent -> {
                boolean removeEntity = false;
                String entId = ent.getId().substring(0, ent.getId().length() - 5);
                if (ent instanceof Zombie) {
                    Zombie zombie = (Zombie) ent;
                    String spawnerId = zombie.getSpawnerId();
                    removeEntity = idsToRemove.stream().anyMatch(id -> id.contains(entId) || id.contains(spawnerId));
                } else {
                    removeEntity = idsToRemove.stream().anyMatch(id -> id.contains(entId));
                }
                
                return removeEntity;
            })
            .collect(Collectors.toList());
        historicalToRemove.forEach(entToRemove -> {
            historyMap.values().forEach(entList -> {entList.remove(entToRemove);});
        });
    } 
}
