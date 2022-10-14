package dungeonmania.Entity.MovingEntity.MovingState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import dungeonmania.Entity.Entity;
import dungeonmania.Interfaces.Moving;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Position;

public class RandomState extends MovementState {

    public RandomState(Moving moving, Map<Position, List<Entity>> entityMap) {
        super(moving, entityMap);
    }

    @Override
    public Position move() {
        List<Position> possiblePositions = getMoveableEntity().getPosition().getAdjacentPositions();
        List<Position> unblockedPossiblePositions = possiblePositions.stream()
            .filter(pos -> !getMoveableEntity().movementBlocked(getEntityMap().getOrDefault(pos, new ArrayList<>())))
            .collect(Collectors.toList());
        
        if (unblockedPossiblePositions.isEmpty()) {
            return null;
        }

        Random r = new Random();
        int random = r.ints(1, 0, unblockedPossiblePositions.size()).findFirst().getAsInt();
        Position randomPosition = unblockedPossiblePositions.get(random);

        MapHelper.mapAddEntity(getEntityMap(), randomPosition, (Entity) getMoveableEntity());
        MapHelper.mapRemoveEntity(getEntityMap(), getMoveableEntity().getPosition(), (Entity) getMoveableEntity());
        getMoveableEntity().setPosition(randomPosition);

        return randomPosition;
        
    }
}
