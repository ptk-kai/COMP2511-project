package dungeonmania.Entity.Collectable;

import dungeonmania.util.Position;

public abstract class BuffItem extends Collectable {
    private int duration = 0;
    public BuffItem(Position position, String type, boolean isInteractable, int duration) {
        super(position, type, isInteractable);
        this.duration = duration;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
    
}
