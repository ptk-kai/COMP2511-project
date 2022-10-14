package dungeonmania.Interfaces;

import java.util.List;
import java.util.Map;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.StaticEntities.*;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * Implemented by entities that can move
 */
public interface Moving {
    /**
     * Move entity into a new position on the map in relation to the direction
     * @param entityMap map to update entity position
     * @param direction direction to update the position to
     * @return @{Position} of the entity after the move. 
     * If the position is blocked from moving 
     * then the position will be the same before calling this function and after calling this function.
     */
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction, ActivitySubscriber subscriber);

    /**
     * Checks if this entity's able to move into a new position given the entities in the new position
     * @param entities all the entities in the new position to check if it blocks this entity's movement.
     * @return true if this entity is not blocked by any entities in the new position.
     */
    default public boolean movementBlocked(List<Entity> entities) {
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
            || entity instanceof Portal
            || entity instanceof ZombieToastSpawner
            ;
        });
        return blocked;
    
    }

    public void setPosition(Position position);
    public Position getPosition();
}
