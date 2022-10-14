package dungeonmania.Entity.Collectable;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class TimeTurner extends Collectable {

    public TimeTurner(Position position) {
        super(position, DungeonMacro.TIME_TURNER, false);
    }

    @Override
    public void useItem() {
        this.getOwner().consumeItem(this);
    }
}
