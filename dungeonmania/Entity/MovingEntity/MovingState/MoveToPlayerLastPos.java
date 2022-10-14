package dungeonmania.Entity.MovingEntity.MovingState;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Interfaces.Moving;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Position;

public class MoveToPlayerLastPos extends MovementState {

    Player player;
    public MoveToPlayerLastPos(Moving moving, Map<Position, List<Entity>> entityMap, Player player) {
        super(moving, entityMap);
        this.player = player;
    }

    @Override
    public Position move() {
        
        // Ally mercenary can use portal. Move towards the closest pos with player's last position
        // which is before player tick
        Map<Position, Double> allPosAndDis = distanceMapForGivenPosMovingCardinally(player.getLastPos());
        if (allPosAndDis == null) {
            return null;
        }

        Double shortestDistance = Collections.min(allPosAndDis.values());
        List<Position> furthestPosList = allPosAndDis.entrySet()
            .stream()
            .filter(entry -> shortestDistance.equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        Random rand = new Random();
        Position chosedPos = furthestPosList.get(rand.nextInt(furthestPosList.size()));

        MapHelper.removeEntityById(getEntityMap(), getMoveableEntity().getPosition(), (Entity) getMoveableEntity());
        MapHelper.mapAddEntity(getEntityMap(), chosedPos, (Entity) getMoveableEntity());
        getMoveableEntity().setPosition(chosedPos);
        
        return chosedPos;
        
    }
    
}
