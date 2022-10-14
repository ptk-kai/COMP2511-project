package dungeonmania.Entity.MovingEntity.MercenaryState;

import java.util.List;
import java.util.Map;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.Entity.MovingEntity.MovingState.MoveToPlayerLastPos;
import dungeonmania.Entity.MovingEntity.MovingState.MovementState;
import dungeonmania.Entity.StaticEntities.Wall;
import dungeonmania.Entity.StaticEntities.ZombieToastSpawner;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.Entity.StaticEntities.*;
import dungeonmania.util.*;

import dungeonmania.Interfaces.MercenaryState;

public class AllyMercenary implements MercenaryState {
    
    Mercenary mercenary;
    private MovementState movementState = null;
    private boolean mindControlled = false;
    private int mindControlDuration = 0;

    public AllyMercenary(Mercenary mercenary) {
        this.mercenary = mercenary;
    }

    
    @Override
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction) {
        Position newPosition = movementState.move();
        return newPosition;
    }

    @Override
    public boolean movementBlocked(List<Entity> entities) {
        boolean doorBlocked = entities.stream()
            .filter(entity -> entity instanceof Door)
            .anyMatch(entity -> {
                Door door = (Door) entity;
                return !door.checkDoorOpen();
            });
        if (doorBlocked) {
            return true;
        }

        boolean blocked = entities.stream().anyMatch(entity -> {
            return entity instanceof Boulder
            || entity instanceof Wall
            || entity instanceof ZombieToastSpawner
            ;
        });
        return blocked;
    }

    @Override
    public void interact(Mercenary mercenary, Player player, Map<Position, List<Entity>> entityMap) throws InvalidActionException {
        throw new InvalidActionException("You can't bribe or mind control an allyMercenary again");
    }

    @Override
    public void setGetControlled(int duration) {
        this.mindControlled = true;
        this.mindControlDuration = duration;
    }

    private boolean checkGetMindControlled() {
        return this.mindControlled;
    }



    public boolean checkMoveStateSetUp() {
        return this.movementState != null;
    }

    @Override
    public void setMoveState(Map<Position, List<Entity>> entityMap, Player player) {
        movementState = new MoveToPlayerLastPos(this.mercenary, entityMap, player);
    }


    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {
        this.setMoveState(entityMap, player);
        this.move(entityMap, null);

        if (checkGetMindControlled()) {
            this.mindControlDuration -= 1;
        
            if (this.mindControlDuration == 0) {
                this.mindControlled = false;
                mercenary.setState(mercenary.getEnemyState());
                player.rmControlledAlly(mercenary);
            }
        }
    }
    
}
