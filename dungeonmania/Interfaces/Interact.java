package dungeonmania.Interfaces;

import java.util.List;
import java.util.Map;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Position;

public interface Interact {
    public void interact(Player player, Map<Position, List<Entity>> entityMap, ActivitySubscriber subscriber) throws InvalidActionException;
}
