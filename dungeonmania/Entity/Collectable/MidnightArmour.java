package dungeonmania.Entity.Collectable;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class MidnightArmour extends Weapon{

    public MidnightArmour(Position position, double atkModifier,double defModifier) {
        super(position, DungeonMacro.MIDNIGHT, false, atkModifier, defModifier, 1000);

    }
    @Override
    public void useInBattle() {
        // Do Nothing here since MidnightArmout has infi-durability
    }
}
