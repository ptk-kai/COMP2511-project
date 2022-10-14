package dungeonmania.goals;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.map.DungeonMap;

public class GoalTreasure implements Goal {

    private DungeonMap currMap;


    public GoalTreasure(DungeonMap currMap) {
        this.currMap = currMap;
    }

    @Override
    public boolean checkGoal() {
        return currMap.checkTreasureGoal();
    }

    @Override
    public String getName() {
        return DungeonMacro.TREASURE;
    }
}
