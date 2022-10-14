package dungeonmania.Entity.StaticEntities;

import java.util.List;
import java.util.Map;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.LogicHelper;
import dungeonmania.Interfaces.Logic;
import dungeonmania.util.Position;

public class LightBulb extends StaticEntity implements Logic{

    private String logic;
    private boolean prevActive = false;
    private boolean currActive = false;

    public LightBulb(Position position, String type, String logicString) {
        super(position, type, false);
        this.logic = logicString;
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
        
        LogicHelper.getSingalStatus(this, entityMap, checkedEntities, updateCurr);
        if (currActive) {
            this.changeType(DungeonMacro.BULB_ON);
        }
        else {
            this.changeType(DungeonMacro.BULB_OFF);
        }
        return this.currActive;
    }
    
}
