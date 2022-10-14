package dungeonmania.Entity;

import java.util.UUID;

import java.io.Serializable;

import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.response.models.EntityResponse;
import dungeonmania.util.Position;

public abstract class Entity implements Serializable {
    protected String id;
    protected Position position;
    protected String type;
    protected boolean isInteractable;

    public Entity(Position position, String type, boolean isInteractable) {
        this.id = type.toString() + UUID.randomUUID();
        this.position = position;
        this.type = type;
        this.isInteractable = isInteractable;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Position getPosition() {
        return this.position;
    }

    public void setPosition(Position pos) {
        this.position = pos;
    }

    public int getX() {
        return this.position.getX();
    }

    public int getY() {
        return this.position.getY();
    }

    public boolean getIsInteractable() {
        return this.isInteractable;
    }

    public void setIsInteractable(boolean isInteractable) {
        this.isInteractable = isInteractable;
    }


    public EntityResponse toEntityResponse() {
        EntityResponse entityResponse = new EntityResponse(id, type.toString(), position, isInteractable);
        return entityResponse;
    }

    public void actionPlayerSamePosition(Player player, ActivitySubscriber subscriber) {
        return;
    }

    public void changeType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "{" +
                " id='" + getId() + "'" +
                ", position='" + getPosition() + "'" +
                ", type='" + getType() + "'" +
                "}";
    }
}
