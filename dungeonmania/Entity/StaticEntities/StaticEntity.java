package dungeonmania.Entity.StaticEntities;

import java.util.List;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public abstract class StaticEntity extends Entity {

    public StaticEntity(Position position, String type, boolean isInteractable) {
        super(position, type, isInteractable);
    }
    
    // Don't check entities is null
    // For the null case, entities should be empty list
    public boolean checkPosBlocked(List<Entity> entities) {
        return entities.stream()
            .anyMatch(entity -> 
            entity instanceof Wall
            || entity instanceof Boulder
            || entity instanceof Door
            || entity instanceof ZombieToastSpawner);
    }
}
