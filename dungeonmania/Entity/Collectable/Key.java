package dungeonmania.Entity.Collectable;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.util.Position;

public class Key extends Collectable{
    private int keyId;

    public Key(Position position, int id) {
        super(position, DungeonMacro.KEY, false);
        this.keyId = id;
    }
    
    // useItem means used for openning door or crafting shield


    public int getKeyId() {
        return this.keyId;
    }
}
