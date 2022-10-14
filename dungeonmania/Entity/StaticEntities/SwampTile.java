package dungeonmania.Entity.StaticEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.MovingEntity.BattleOpponent;
import dungeonmania.util.Position;

public class SwampTile extends StaticEntity {

    private int trapTime;
    private Map<BattleOpponent, Integer> trappedMap = new HashMap<>();
    
    public SwampTile(Position position, int trapCounter) {
        super(position, DungeonMacro.SWAMP, false);
        this.trapTime = trapCounter;
    }
    
    public void swampTickOperation(Map<Position, List<Entity>> entityMap) {
        List<Entity> entityOnSwamp = entityMap.getOrDefault(this.getPosition(), new ArrayList<>());
        // We put +1 for movement factor here
        // Since the tickLeft do -1 for all key in the map
        // which means enmey trapped in this tick also get -1 in trap time
        entityOnSwamp.stream()
            .filter(e -> e instanceof BattleOpponent)
            .map(e -> (BattleOpponent) e)
            .filter(e -> !e.checkAlly() && !trappedMap.containsKey(e))
            .forEach(e -> trappedMap.put(e, trapTime + 1));
    
        trappedMap.replaceAll((key,value) -> (value - 1));
        trappedMap.values().removeIf(value -> value == 0); 
    }


    public boolean checkEntityTrapped(Entity input) {
        return trappedMap.containsKey(input);
    }

    public int getTrappedTime() {
        return this.trapTime;
    }
}
