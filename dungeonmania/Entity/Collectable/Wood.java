package dungeonmania.Entity.Collectable;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Wood extends Collectable{
    public Wood(Position position) {
        super(position, DungeonMacro.WOOD, false);
    }

    // useItem means used for craft bow

}
