package dungeonmania.Entity.Collectable;



import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Sceptre extends Weapon {

    private int controlDuration;
    public Sceptre(Position position, int controlDuration) {
        super(position, DungeonMacro.SCEPTRE, false, 0, 0, 1000);
        this.controlDuration = controlDuration;
    }


    public int getControlDuration() {
        return this.controlDuration;
    }

    public void setcontrolDuration(int controlDuration) {
        this.controlDuration = controlDuration;
    }
}
