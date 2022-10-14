package dungeonmania.map;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.MovingEntity.Battle;

public class MapCache implements Serializable {
    private List<Battle> battlesCacheForOneTick = new ArrayList<>();
    private List<Entity> historicalEntitiesToDelete = new ArrayList<>();
    private List<Entity> entitiesToDelete = new ArrayList<>();

    public void clearAll() {
        battlesCacheForOneTick.clear();
        historicalEntitiesToDelete.clear();
        entitiesToDelete.clear();
    }


    public List<Battle> getBattlesCacheForOneTick() {
        return this.battlesCacheForOneTick;
    }

    public void addBattle(Battle battle) {
        battlesCacheForOneTick.add(battle);
    }

    public List<Entity> getHistoricalEntitiesToDelete() {
        return this.historicalEntitiesToDelete;
    }

    public void addHistoricalEntityToDelete(Entity entity) {
        historicalEntitiesToDelete.add(entity);
    }

    public List<Entity> getEntitiesToDelete() {
        return this.entitiesToDelete;
    }

    public void addEntityToDeleteFromMap(Entity entity) {
        entitiesToDelete.add(entity);
    }

}
