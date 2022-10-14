package dungeonmania.Entity.StaticEntities;

import java.util.List;
import java.util.Map;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.LogicHelper;
import dungeonmania.Interfaces.Logic;
import dungeonmania.util.Position;

public class SwitchDoor extends Door implements Logic{

    private boolean prevActive;
    private boolean currActive;
    private String logic;

    public SwitchDoor(Position position, int requiredKeyId, String inputLogic) {
        super(position, requiredKeyId);
        this.logic = inputLogic;
    }

    @Override
    public boolean checkDoorOpen() {
        return super.checkDoorOpen() || currActive;
    }

    @Override
    public String getLogic() {
        return this.logic;
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

    @Override
    public boolean getSingalStatus(Map<Position, List<Entity>> entityMap, List<Entity> checkedEntities, boolean updateCurr) {
        
        return LogicHelper.getSingalStatus(this, entityMap, checkedEntities, updateCurr);
    }

    
}
