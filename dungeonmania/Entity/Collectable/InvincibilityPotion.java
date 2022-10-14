package dungeonmania.Entity.Collectable;
import java.util.List;
import java.util.Map;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Interfaces.SpecialEffects;
import dungeonmania.util.Position;

public class InvincibilityPotion extends BuffItem implements SpecialEffects {
    public InvincibilityPotion(Position position, int duration) {
        super(position, DungeonMacro.INVINCIBILITY, false, duration);
    }


    @Override
    public void enactSpecialAction(Map<Position, List<Entity>> entityMap) {
        useItem();
        Player player = this.getOwner();
        player.queueEffects(this);
    }
}
