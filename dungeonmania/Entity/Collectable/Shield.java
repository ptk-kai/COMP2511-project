package dungeonmania.Entity.Collectable;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Shield extends Weapon {
    public Shield(Position position, double defence, double durability) {
        super(position, DungeonMacro.SHIELD, false, 0, defence, durability);
    }
}
