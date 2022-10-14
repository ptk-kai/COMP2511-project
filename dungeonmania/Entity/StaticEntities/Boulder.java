package dungeonmania.Entity.StaticEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.Interfaces.Moving;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * Specs: Boulders can be pushed onto collectable entities.
 * 
 * Assumptions:
 * Mon 4 Jul 1pm - Movement of boulders onto collectables is undefined
 * Mon 4 Jul 11pm - Behaviour for what happens when a boulder is pushed onto an enemy or portal is undefined
 * Thu 7 Jul 6pm - There will never be two or more of any static or collectable entity at the same tile 
 * with the exception of floor switches, of which there could be a boulder on top
 * 
 * Assumptions: boulder can only be pushed onto empty spaces, floor switches or collectables.
 * In the case of collectables, both entities will exist on the same space.
 */
public class Boulder extends StaticEntity implements Moving {

    public Boulder(Position position) {
        super(position, DungeonMacro.BOULDER, false);
    }

    @Override
    public boolean checkPosBlocked(List<Entity> entities) {
        boolean blocked = entities.stream()
            .anyMatch(entity -> 
            entity instanceof Moving
            || entity instanceof SwampTile
            || entity instanceof Wall
            || entity instanceof Exit
            || entity instanceof Boulder
            || entity instanceof Door
            || entity instanceof Portal
            || entity instanceof ZombieToastSpawner);
        return blocked;
    }

    @Override
    public boolean movementBlocked(List<Entity> entities) {
        return this.checkPosBlocked(entities);
    }

    @Override
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction, ActivitySubscriber subscriber) {
        Position potentialBoulderNewPos = getPosition().translateBy(direction);
        List<Entity> boulderNewPositionEntities = entityMap.getOrDefault(potentialBoulderNewPos, new ArrayList<>());
        boolean boulderMovementBlocked = movementBlocked(boulderNewPositionEntities);
        if (boulderMovementBlocked) {
            return getPosition();
        }
        
        MapHelper.rmGivenEntity(entityMap, getPosition(), this);
        this.setPosition(potentialBoulderNewPos);
        MapHelper.mapAddEntity(entityMap, potentialBoulderNewPos, this);

        if (this.getId().contains("past")) {
            subscriber.updateHistoricalEntityPosition(this);
        }
        return potentialBoulderNewPos;
    }
}
