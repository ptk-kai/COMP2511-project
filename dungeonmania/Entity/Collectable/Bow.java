package dungeonmania.Entity.Collectable;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Bow extends Weapon {
    public Bow(Position position, double durability) {
        super(position, DungeonMacro.BOW, false, 0, 0, durability);
    }
}
