package dungeonmania.Entity.Collectable;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Treasure extends Collectable{

    public Treasure(Position position) {
        super(position, DungeonMacro.TREASURE, false);
    }

    // UseItem means used for bribing or crafting shield

}
