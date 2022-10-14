package dungeonmania.goals;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.map.DungeonMap;

public class GoalExit implements Goal {
    
    private DungeonMap currMap;

    public GoalExit(DungeonMap currMap) {
        this.currMap = currMap;
    }

    @Override
    public boolean checkGoal() {
        return currMap.checkExitGoal();    
    }

    @Override
    public String getName() {
        return DungeonMacro.EXIT;
    }
    
}
