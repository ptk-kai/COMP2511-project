package dungeonmania.Entity.StaticEntities;



import java.util.List;
import java.util.Map;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.LogicHelper;
import dungeonmania.Interfaces.Logic;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Position;

public class FloorSwitch extends StaticEntity implements Logic{

    private String logic;
    private boolean currSelfActive = false;
    private boolean prevActive = false;
    private boolean currActive = false;


    public FloorSwitch(Position position, String logic) {
        super(position, DungeonMacro.SWITCH, false);
        this.logic = logic;
        
    }

    public boolean checkActiveFloorSwitch(Map<Position,List<Entity>> entityMap) {
        
        boolean tmp = currActive;
        currSelfActive = MapHelper.getBoulders(entityMap)
            .stream()
            .anyMatch(boulder -> boulder.getPosition().equals(this.getPosition()));
        currActive = currSelfActive;
        if (currActive != tmp) {
            prevActive = tmp;
        }
        return currSelfActive;
    }

    @Override
    public String getLogic() {
        return this.logic;
    }


    @Override
    public boolean checkActiveCurrentTick() {
        if (!prevActive && currActive) {
            return true;
        }
        return false;
    }

    @Override
    public boolean getSingalStatus(Map<Position, List<Entity>> entityMap, List<Entity> checkedEntities, boolean updateCurr) {
        
        // If switch active by boulder, directly return true
        if (checkActiveFloorSwitch(entityMap)) {
            return true;
        }
        return LogicHelper.getSingalStatus(this, entityMap, checkedEntities, true);

    }

    @Override
    public void updatePrev(boolean status) {
        this.prevActive = status;
    }


    @Override
    public void updateCurr(boolean status) {
        this.currActive = status;
    }


    @Override
    public boolean getCurrStatus() {
        return this.currActive;
    }

}
