package dungeonmania.Entity.StaticEntities;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class TimeTravellingPortal extends StaticEntity {

    public TimeTravellingPortal(Position position) {
        super(position, DungeonMacro.TIME_PORTAL, false);
    }
}
