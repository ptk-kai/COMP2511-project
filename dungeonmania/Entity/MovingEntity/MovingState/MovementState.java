package dungeonmania.Entity.MovingEntity.MovingState;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.stream.Collectors;

import dungeonmania.Entity.Entity;
import dungeonmania.Interfaces.Moving;
import dungeonmania.util.Position;

public abstract class MovementState implements Serializable {
    protected Moving moveableEntity;
    protected Map<Position, List<Entity>> entityMap;

    public MovementState(Moving moving, Map<Position, List<Entity>> entityMap) {
        this.moveableEntity = moving;
        this.entityMap = entityMap;
    }

    public Moving getMoveableEntity() {
        return moveableEntity;
    }

    public void setMoveableEntity(Moving moveableEntity) {
        this.moveableEntity = moveableEntity;
    }

    public Map<Position, List<Entity>> getEntityMap() {
        return entityMap;
    }

    public void setEntityMap(Map<Position, List<Entity>> entityMap) {
        this.entityMap = entityMap;
    }

    public abstract Position move();

    // TAKE CARE !! This functions used when entity can move diagonally
    public Map<Position, Double> distanceMapForGivenPos(Position inputPos) {
        List<Position> possiblePositions;
       
        possiblePositions = getMoveableEntity().getPosition().getAdjacentPositions();
        
      
        List<Position> unblockedPossiblePositions = possiblePositions.stream()
            .filter(pos -> !getMoveableEntity().movementBlocked(getEntityMap().getOrDefault(pos, new ArrayList<>())))
            .collect(Collectors.toList());
        
        if (unblockedPossiblePositions.isEmpty()) {
            return null;
        }
        Map<Position, Double> allPosWithDis = unblockedPossiblePositions.stream()
            .map(pos -> {
                double distance = Position.calculateDistanceBetween(pos, inputPos);
                return new AbstractMap.SimpleEntry<>(pos, distance);
            })
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
        return allPosWithDis;
    }


    public Map<Position, Double> distanceMapForGivenPosMovingCardinally(Position inputPos) {
        List<Position> possiblePositions;
        
        possiblePositions = getMoveableEntity().getPosition().getCardinallyAdjacentPositions();
        possiblePositions.add(this.getMoveableEntity().getPosition());
        
        List<Position> unblockedPossiblePositions = possiblePositions.stream()
            .filter(pos -> !getMoveableEntity().movementBlocked(getEntityMap().getOrDefault(pos, new ArrayList<>())))
            .collect(Collectors.toList());
        
        if (unblockedPossiblePositions.isEmpty()) {
            return null;
        }
        
        Map<Position, Double> allPosWithDis = unblockedPossiblePositions.stream()
            .map(pos -> {
                double distance = Position.cardinallyDistanceBetween(pos, inputPos);
                return new AbstractMap.SimpleEntry<>(pos, distance);
            })
            .collect(Collectors.toMap(SimpleEntry::getKey, SimpleEntry::getValue));
        return allPosWithDis;
    }
}
