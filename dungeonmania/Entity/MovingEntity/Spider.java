package dungeonmania.Entity.MovingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.StaticEntities.Boulder;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Spider extends BattleOpponent {

    private List<Position> adjacentPositions;
    private int spiderCurrentPosition = 0;
    private Position spiderStartingPosition;
    private boolean clockwise = true;

    public Spider(Position position, double attack, double health) {
        super(position, DungeonMacro.SPIDER, false, attack, health);
        this.spiderStartingPosition = position;
        this.adjacentPositions = spiderStartingPosition.getAdjacentPositions();
    }

    @Override
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction, ActivitySubscriber subscriber) {
        Position newPosition = attemptToMove(entityMap, clockwise);
        if (Objects.isNull(newPosition)) {
            newPosition = attemptToMove(entityMap, clockwise);
        }
        if (Objects.isNull(newPosition)) {
            return this.getPosition();
        }
        MapHelper.mapAddEntity(entityMap, newPosition, this);
        MapHelper.mapRemoveEntity(entityMap, this.getPosition(), this);
        this.setPosition(newPosition);
        return newPosition;
    }

    public Position attemptToMove(Map<Position, List<Entity>> entityMap, boolean clockwise) {
        int potentialPos;
        if (clockwise) {
            potentialPos = spiderCurrentPosition + 1;
            if (potentialPos > 7) {
                potentialPos = 0;
            }
            
        } else {
            potentialPos = spiderCurrentPosition - 1;
            if (potentialPos < 0) {
                potentialPos = 7;
            }
        }
        Position potentialNewPosition = adjacentPositions.get(potentialPos);
        boolean blocked = movementBlocked(entityMap.getOrDefault(potentialNewPosition, new ArrayList<>()));
        
        if (blocked) {
            this.clockwise = !clockwise;
            return null;
        } else {
            spiderCurrentPosition = potentialPos;
            return potentialNewPosition;
        }
    }

    @Override
    public boolean movementBlocked(List<Entity> entities) {
        return entities.stream().anyMatch(entity -> entity instanceof Boulder);
    }

    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {
        move(entityMap, null, null);
    }

    public List<Position> getAdjacentPositions() {
        return this.adjacentPositions;
    }

    public void setAdjacentPositions(List<Position> adjacentPositions) {
        this.adjacentPositions = adjacentPositions;
    }

    public int getSpiderCurrentPosition() {
        return this.spiderCurrentPosition;
    }

    public void setSpiderCurrentPosition(int spiderCurrentPosition) {
        this.spiderCurrentPosition = spiderCurrentPosition;
    }

    public Position getSpiderStartingPosition() {
        return spiderStartingPosition;
    }

    public void setSpiderStartingPosition(Position spiderStartingPosition) {
        this.spiderStartingPosition = spiderStartingPosition;
    }
}
