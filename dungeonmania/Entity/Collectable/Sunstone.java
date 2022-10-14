package dungeonmania.Entity.Collectable;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

// On specs it says extend Treasure, not choose here
// Since Sunstone can only do a subset of Treasure
// Still need type check
public class Sunstone extends Collectable{

    public Sunstone(Position position) {
        super(position, DungeonMacro.SUNSTONE, false);
    }

    // useItem should be called only when use Sunstone for craft

}
