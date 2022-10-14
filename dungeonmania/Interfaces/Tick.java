package dungeonmania.Interfaces;

import java.util.List;
import java.util.Map;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.util.Position;

public interface Tick {
    /**
     * Notification function to notify subscribers to perform tick actions such as move or spawn.
     * @param entityMap
     */
    public void tick(Map<Position, List<Entity>> entityMap, Player player);
}
