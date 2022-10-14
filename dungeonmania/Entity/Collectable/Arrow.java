package dungeonmania.Entity.Collectable;
import dungeonmania.util.Position;
import dungeonmania.Config.*;

public class Arrow extends Collectable{
    public Arrow(Position position) {
        super(position, DungeonMacro.ARROW, false);
    }

    // useItem means used for craft bow

}
