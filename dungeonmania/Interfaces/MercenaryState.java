package dungeonmania.Interfaces;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public interface MercenaryState extends Serializable {
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction);
    public boolean movementBlocked(List<Entity> entities);
    public void interact(Mercenary mercenary, Player player, Map<Position, List<Entity>> entityMap) throws InvalidActionException;
    public void setMoveState(Map<Position, List<Entity>> entityMap, Player player);
    public void setGetControlled(int duration);
    public void tick(Map<Position, List<Entity>> entityMap, Player player);
}
