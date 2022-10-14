package dungeonmania.goals;
import java.util.ArrayList;
import java.util.List;
import dungeonmania.map.DungeonMap;

public abstract class GoalComposite implements Goal {
    private DungeonMap currMap;
    private List<Goal> goalList;
    
    public GoalComposite(DungeonMap currMap) {
        this.currMap = currMap;
        this.goalList = new ArrayList<Goal>();
    }

    public void add(Goal goal) {
        goalList.add(goal);
    }

    public List<Goal> getAllgoals() {
        return goalList;
    }

}
