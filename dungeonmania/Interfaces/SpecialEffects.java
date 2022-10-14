package dungeonmania.Interfaces;

import java.util.List;
import java.util.Map;

import dungeonmania.Entity.Entity;
import dungeonmania.util.Position;

public interface SpecialEffects {
    public void enactSpecialAction(Map<Position, List<Entity>> entityMap);
}
