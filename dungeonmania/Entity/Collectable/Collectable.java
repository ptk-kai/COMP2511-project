package dungeonmania.Entity.Collectable;

import dungeonmania.Entity.*;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.util.Position;

public abstract class Collectable extends Entity {
    private Player owner = null;

    public Collectable(Position position, String type, boolean isInteractable) {
        super(position, type, isInteractable);
    }

    public ItemResponse toItemResponse() {
        return new ItemResponse(this.getId(), this.getType());
    }

    public void setOwner(Player player) {
		this.owner = player;
	}

    public Player getOwner() {
        return this.owner;
    }

    public void actionPlayerSamePosition(Player player, ActivitySubscriber subscriber) {
        boolean pickupSuccess = player.collectItem(this);

        if (pickupSuccess) {
            subscriber.removeEntityFromMap(this);
        }

        //remove past items from history
        if (this.getId().contains("past")) {
            subscriber.removeHistoricalEntities(this);
        }
        return;
    }

    public void useItem() {
        this.getOwner().consumeItem(this);
    };

}
