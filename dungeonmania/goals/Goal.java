package dungeonmania.goals;

import java.io.Serializable;

public interface Goal extends Serializable {
    public boolean checkGoal();
    public String getName();
    
    default public String incompleteGoals() {
        if (!checkGoal()) {
            return ":" + getName();
        }
        return "";
    }
}
