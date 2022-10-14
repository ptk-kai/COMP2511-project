package dungeonmania.Entity.StaticEntities;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Player;
import dungeonmania.util.Position;

public class Exit extends StaticEntity {

    public Exit(Position position) {
        super(position, DungeonMacro.EXIT, false);
    }

    public boolean checkExit(Player curPlayer) {
        return this.getPosition().equals(curPlayer.getPosition());
    }

}
