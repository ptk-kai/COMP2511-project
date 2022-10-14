package dungeonmania.Entity.Collectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.LogicHelper;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.StaticEntities.FloorSwitch;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.Interfaces.Logic;
import dungeonmania.Interfaces.SpecialEffects;
import dungeonmania.Interfaces.Tick;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Position;

public class Bomb extends Collectable implements Tick, SpecialEffects, Logic {
    
    private boolean usedByPlayer;
    private int radius;
    private String logic;
    private boolean prevActive = false;
    private boolean currActive = false;

    public Bomb(Position position, int radius, String logicString) {
        super(position, DungeonMacro.BOMB, false);
        this.radius = radius;
        this.usedByPlayer = false;
        this.logic = logicString;
    }

    public boolean checkUsed() {
        return this.usedByPlayer;
    }

    public void detonate(Map<Position, List<Entity>> entityMap) {

        Position startPos = new Position(getX() - radius, getY() - radius);
        List<Position> posInRange = startPos.getSquaredRangePos(2 * radius);

        posInRange.stream()
            .forEach(pos -> MapHelper.rmEntitiesAtPos(entityMap, pos));

    }

    public void useBomb(Map<Position, List<Entity>> entityMap) {
        this.useItem();
        MapHelper.mapAddEntity(entityMap, this.getPosition(), this);
    }

    @Override
    public void useItem() {
        Player owner = this.getOwner();
        this.usedByPlayer = true;
        this.setPosition(owner.getPosition());
        owner.consumeItem(this);
    }

    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {

        if (this.usedByPlayer) {

            boolean checkActive;
            if (this.logic.equals("")) {
                List<Position> adjacentPos = this.getPosition().getCardinallyAdjacentPositions();
                List<FloorSwitch> adjacentFloorSwitch = MapHelper.getFloorSwitchs(entityMap)
                .stream()
                .filter(fs -> adjacentPos.contains(fs.getPosition()))
                .collect(Collectors.toList());
    
                checkActive = adjacentFloorSwitch.stream()
                    .anyMatch(fs -> fs.checkActiveFloorSwitch(entityMap));
            }
            else {
                checkActive = LogicHelper.getSingalStatus(this, entityMap, new ArrayList<Entity>(), true);
            }
            
            if (checkActive) {
                this.detonate(entityMap);
            }
        }
    }

    @Override
    public void enactSpecialAction(Map<Position, List<Entity>> entityMap) {
        useBomb(entityMap);
        tick(entityMap, null);
    }

    @Override
    public void actionPlayerSamePosition(Player player, ActivitySubscriber subscriber) {
        if (!this.usedByPlayer) {
            super.actionPlayerSamePosition(player, subscriber);
        }
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
