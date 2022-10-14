package dungeonmania.Entity.MovingEntity.MovingState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;


import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.StaticEntities.Portal;
import dungeonmania.Entity.StaticEntities.SwampTile;
import dungeonmania.Interfaces.Moving;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Position;

public class MoveTowardsPlayer extends MovementState {
    private Map<Position, Position> dijkstraMap = new HashMap<>();
    private Map<Position, Double> portalTmp = new HashMap<>();
    Player player;

    public MoveTowardsPlayer(Moving moving, Map<Position, List<Entity>> entityMap, Player player) {
        super(moving, entityMap);
        this.player = player;
    }

    @Override
    public Position move() {
        portalTmp.clear();
        updateDijkstraMap();
        Position chosedPos = findchosedPosition(player.getPosition());
        if (!portalTmp.isEmpty()) {
            chosedPos = findchosedPosition(Collections.min(
                portalTmp.entrySet(), Entry.comparingByValue()).getKey());
        }
        
        List<Entity> entities = entityMap.getOrDefault(chosedPos, new ArrayList<>());
        if (MapHelper.checkListHasTypeEntity(entities, DungeonMacro.PORTAL)) {
            // Assumption : No two portal as same pos
            Portal portal = (Portal) MapHelper.getFirstEntityInType(entities, DungeonMacro.PORTAL);
            Position teleportPos = portal.getPossiblePos(entityMap);
            if (teleportPos == null) {
                return this.moveableEntity.getPosition();
            }
            chosedPos = teleportPos;
        }

        MapHelper.mapAddEntity(getEntityMap(), chosedPos, (Entity) getMoveableEntity());
        MapHelper.mapRemoveEntity(getEntityMap(), getMoveableEntity().getPosition(), (Entity) getMoveableEntity());
        getMoveableEntity().setPosition(chosedPos);
        
        return chosedPos;
    }

    private Position findchosedPosition(Position p) {
        if (dijkstraMap.get(p) != null) {
            if (dijkstraMap.get(p).equals(this.moveableEntity.getPosition())) {
                return p;
            } else {
                return findchosedPosition(dijkstraMap.get(p));
            }
        } else {
            return this.moveableEntity.getPosition();
        }
    }

    private void updateDijkstraMap() {
        Map<Position, Double> dist = new HashMap<>();
        Map<Position, Position> prev = new HashMap<>();
        Map<Position, Double> queue = new HashMap<>();
        for (Position p : allpossiblePositions()) {
            dist.put(p, Double.POSITIVE_INFINITY);
            prev.put(p, null);
            queue.put(p, Double.POSITIVE_INFINITY);
        }
        dist.put(this.moveableEntity.getPosition(), (double) 0);
        queue.put(this.moveableEntity.getPosition(), (double) 0);

        while (queue.size() != 0) {
            Position u = Collections.min(queue.entrySet(), Map.Entry.comparingByValue()).getKey();
            queue.remove(u);
            for (Position v: u.getCardinallyAdjacentPositions()) {
                if (dist.containsKey(v)) {
                    Double cost = dist.get(u) + cost(u, v);
                    if (cost < dist.get(v)) {
                        dist.put(v, cost);
                        queue.put(v, cost);
                        prev.put(v, u);
                    }
                }
            }
        }

        this.dijkstraMap = prev;
    }

    private ArrayList<Position> allpossiblePositions() {
        int x = 0;
        int y = 0;
        for (Position p : this.entityMap.keySet()) {
            if (Math.abs(p.getX()) > Math.abs(x)) {
                x = p.getX();
            }
            if (Math.abs(p.getY()) > Math.abs(y)) {
                y = p.getY();
            }
        }
        int xTmp;
        int yTmp;
        if (x < 0) {
            xTmp = x - 1;
        } else {
            xTmp = -1;
        }
        if (y < 0) {
            yTmp = y - 1;
        } else {
            yTmp = -1;
        }
        
        ArrayList<Position> result = new ArrayList<Position>();
        while (xTmp <= Math.abs(x) + 1) {
            for (int i = yTmp; i <= Math.abs(y) + 1; i++) {
                result.add(new Position(xTmp, i));
            }
            xTmp++;
        }
        return result;
    }

    private Double cost(Position u, Position v) {
        List<Entity> vEntities = this.entityMap.getOrDefault(v, new ArrayList<>());
        if (vEntities.size() == 0) {
            return (double) 1;
        } else if (this.moveableEntity.movementBlocked(vEntities)) {
            return Double.POSITIVE_INFINITY;
        } else if (vEntities.stream().anyMatch(e -> {return e instanceof SwampTile;})) {
            SwampTile swampTile = (SwampTile) vEntities.stream().filter(e -> {return e instanceof SwampTile;}).findFirst().orElse(null);
            return (double) swampTile.getTrappedTime();
        } else if (vEntities.stream().anyMatch(e -> {return e instanceof Portal;})) {
            Portal portal = (Portal) vEntities.stream().filter(e -> {return e instanceof Portal;}).findFirst().orElse(null);
            Double newDistance = Position.calculateDistanceBetween(portal.getPossiblePos(entityMap), this.player.getPosition());
            Double oldDistance = Position.calculateDistanceBetween(v, this.player.getPosition());
            if (newDistance - oldDistance < 0) {
                this.portalTmp.put(v, newDistance - oldDistance);
            }
            return (double) 1;
        } else {
            return (double) 1;
        }
    }
    
}
