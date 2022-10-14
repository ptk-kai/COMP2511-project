package dungeonmania.Interfaces;

import java.util.List;
import java.util.Map;
import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public interface Logic {
    
    public String getLogic();

    public void updatePrev(boolean status);

    public boolean getCurrStatus();
    
    public void updateCurr(boolean status);

    public boolean getSingalStatus(Map<Position,List<Entity>> entityMap, List<Entity> checkedEntities, boolean updateCurr);

    // default return false since only Wire/FloorSwitch override this
    default public boolean checkActiveCurrentTick() {return false;}

}
