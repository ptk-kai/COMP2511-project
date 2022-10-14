package dungeonmania.goals;

import dungeonmania.map.DungeonMap;

public class GoalEnemies implements Goal {
    
    private DungeonMap currMap;


    public GoalEnemies(DungeonMap currMap) {
        this.currMap = currMap;
    }
    
    @Override
    public boolean checkGoal() {
        return currMap.checkEnemyGoal() && currMap.checkNoSpawner();
    }

    @Override
    public String getName() {
        return "enemies";
    }
}
