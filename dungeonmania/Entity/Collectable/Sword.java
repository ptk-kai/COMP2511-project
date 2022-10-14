package dungeonmania.Entity.Collectable;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Sword extends Weapon {
    
    public Sword(Position position, double attack, double durability) {
        super(position, DungeonMacro.SWORD, false, attack, 0, durability);
    }
}
