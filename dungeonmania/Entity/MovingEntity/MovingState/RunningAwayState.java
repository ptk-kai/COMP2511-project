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

public class RunningAwayState extends MovementState {
    Player player;

    public RunningAwayState(Moving moving, Map<Position, List<Entity>> entityMap, Player player) {
        super(moving, entityMap);
        this.player = player;
    }

    @Override
    public Position move() {

        Map<Position, Double> allPosAndDis = distanceMapForGivenPos(player.getPosition());

        if (allPosAndDis == null) {
            return null;
        }

        Double maxDistance = Collections.max(allPosAndDis.values());
        List<Position> furthestPosList = allPosAndDis.entrySet()
            .stream()
            .filter(entry -> maxDistance.equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        Random rand = new Random();
        Position chosedPos = furthestPosList.get(rand.nextInt(furthestPosList.size()));

        MapHelper.mapAddEntity(getEntityMap(), chosedPos, (Entity) getMoveableEntity());
        MapHelper.mapRemoveEntity(getEntityMap(), getMoveableEntity().getPosition(), (Entity) getMoveableEntity());
        getMoveableEntity().setPosition(chosedPos);
        
        return chosedPos;
    }
}
