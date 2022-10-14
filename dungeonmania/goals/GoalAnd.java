package dungeonmania.goals;

import dungeonmania.map.DungeonMap;

public class GoalAnd extends GoalComposite {

    public GoalAnd(DungeonMap currMap) {
        super(currMap);
    }

    @Override
    public boolean checkGoal() {
        return getAllgoals().stream()
            .map(goal -> goal.checkGoal())
            .allMatch(result -> result.equals(true));
    }

    @Override
    public String getName() {
        return " AND ";
    }

    @Override
    public String incompleteGoals() {
        StringBuilder incompleteGoals = new StringBuilder();
        getAllgoals().stream().forEach(goal -> {
            
            boolean goalCompleted = goal.checkGoal();
            if (!goalCompleted) {
                if (incompleteGoals.length() > 0) {
                    incompleteGoals.append(this.getName());
                }
                incompleteGoals.append(goal.incompleteGoals());
            }
        });
        return incompleteGoals.toString();
    }
}
