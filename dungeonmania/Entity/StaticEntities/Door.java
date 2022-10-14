package dungeonmania.Entity.StaticEntities;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Collectable.Key;
import dungeonmania.Entity.Collectable.Sunstone;
import dungeonmania.util.Position;

public class Door extends StaticEntity {
    private int requiredKeyId;
    private boolean opened = false;
    public Door(Position position,int requiredKeyId) {
        super(position, DungeonMacro.DOOR, false);
        this.requiredKeyId = requiredKeyId;
    }

    public boolean checkMatchKey(Key inputKey) {
        if (inputKey == null) {
            return false;
        }
        boolean result = this.requiredKeyId == inputKey.getKeyId();
        if (result) {
            this.opened = true;
            this.setType(DungeonMacro.DOOR_OPEN);
            inputKey.useItem();
        }
        return result;
    }
    
    public boolean checkUsingSunStone(Sunstone inpuSunstone) {
        if (inpuSunstone == null) {
            return false;
        }
        else {
            this.opened = true;
        }
        return this.opened;
    }

    public boolean checkDoorOpen() {
        return this.opened;
    }

    public void setDoorOpen(boolean opened) {
        this.opened = opened;
    }
}
