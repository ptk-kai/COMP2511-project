package dungeonmania.goals;

import dungeonmania.map.DungeonMap;

public class GoalBoulders implements Goal {

    private DungeonMap currMap;
    
    public GoalBoulders(DungeonMap currMap) {
        this.currMap = currMap;
    }

    @Override
    public boolean checkGoal() {
        return currMap.checkBoulderGoal();
    }

    @Override
    public String getName() {
        return "boulders";
    }
}
