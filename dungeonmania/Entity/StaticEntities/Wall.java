package dungeonmania.Entity.StaticEntities;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Wall extends StaticEntity{

    public Wall(Position position) {
        super(position, DungeonMacro.WALL, false);
    }
}
