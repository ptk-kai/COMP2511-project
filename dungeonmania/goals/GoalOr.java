package dungeonmania.goals;

import dungeonmania.map.DungeonMap;

public class GoalOr extends GoalComposite {
    
    public GoalOr(DungeonMap currMap) {
        super(currMap);
    }

    @Override
    public boolean checkGoal() {
        return getAllgoals().stream()
            .map(goal -> goal.checkGoal())
            .anyMatch(result -> result.equals(true));
    }

    @Override
    public String getName() {
        return " OR ";
    }

    @Override
    public String incompleteGoals() {
        StringBuilder incompleteGoals = new StringBuilder();
        if (!this.checkGoal()) {
            getAllgoals().stream().forEach(goal -> {
                if (incompleteGoals.length() > 0) {
                    incompleteGoals.append(this.getName());
                }
                incompleteGoals.append(goal.incompleteGoals()); 
            });
            incompleteGoals.insert(0, "(").append(")");
            return incompleteGoals.toString();
        }
        return "";
    }

}
