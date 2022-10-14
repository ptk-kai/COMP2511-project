package dungeonmania.Entity.StaticEntities;

import java.util.List;
import java.util.Map;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.LogicHelper;
import dungeonmania.Interfaces.Logic;
import dungeonmania.util.Position;


public class Wire extends Entity implements Logic {

    private String logic;
    private boolean prevActive = false;
    private boolean currActive = false;

    public Wire(Position position, String inputLogic) {
        super(position, DungeonMacro.WIRE, false);
        this.logic = inputLogic;
    }
    

    @Override
    public String getLogic() {
        return this.logic;
    }


    @Override
    public boolean getSingalStatus(Map<Position,List<Entity>> entityMap, List<Entity> checkedEntities, boolean updateCurr) {
        
        return LogicHelper.getSingalStatus(this, entityMap, checkedEntities, updateCurr);
    }


    @Override
    public boolean checkActiveCurrentTick() {
        if (!prevActive && currActive) {
            return true;
        }
        return false;
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
