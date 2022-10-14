package dungeonmania.Entity.MovingEntity.MercenaryState;

import java.util.List;
import java.util.Map;
import dungeonmania.Inventory;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.*;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.Entity.MovingEntity.MovingState.MoveTowardsPlayer;
import dungeonmania.Entity.MovingEntity.MovingState.MovementState;
import dungeonmania.Entity.MovingEntity.MovingState.RandomState;
import dungeonmania.Entity.MovingEntity.MovingState.RunningAwayState;
import dungeonmania.Entity.StaticEntities.Wall;
import dungeonmania.Entity.StaticEntities.ZombieToastSpawner;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.Entity.StaticEntities.*;
import dungeonmania.util.*;
import dungeonmania.Interfaces.MercenaryState;


public class EnemyMercenary implements MercenaryState {
    Mercenary mercenary;
    protected MovementState movementState = null;

    public EnemyMercenary(Mercenary mercenary) {
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
            //|| entity instanceof Portal
            || entity instanceof ZombieToastSpawner
            ;
        });
        return blocked;
    }

    @Override
    public void interact(Mercenary mercenary, Player player, Map<Position, List<Entity>> entityMap) throws InvalidActionException {
        Position playerPosition = player.getPosition();
        Inventory inventory = player.getInventory();
        
        if (player.checkHasSceptre()) {

            mercenary.setState(mercenary.getAllyState());
            mercenary.getAllyState().setGetControlled(player.getMindControlDuration());
            player.addAlly(this.mercenary);
        
        } else {
            
            if (! playerPosition.inRange(player.getBribeRadius(), mercenary.getPosition())) {
                throw new InvalidActionException("Mercenary not in range");
            } else if (mercenary.getBribeAmount() > inventory.checkNumberOfItem(DungeonMacro.TREASURE)) {
                throw new InvalidActionException("Do not have enough treasure");
            } else {
                player.useTreasure(mercenary.getBribeAmount());
                if (this.mercenary.bribeSuccess()) {
                    mercenary.setState(mercenary.getAllyState());
                    player.addAlly(this.mercenary);
                }
            }
        }
        
    }


    @Override
    public void setMoveState(Map<Position, List<Entity>> entityMap, Player player) {
        movementState = new MoveTowardsPlayer(this.mercenary, entityMap, player);
        
    }

    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {
        if (player.isInvincible()) {
            movementState = new RunningAwayState(this.mercenary, entityMap, player);
        } else if (this.mercenary.canNotSeePlayer(player)) {
            movementState = new RandomState(this.mercenary, entityMap);
        } else {
            this.setMoveState(entityMap, player);
        }
        this.move(entityMap, null);
    }

    @Override
    public void setGetControlled(int duration) {}
}
