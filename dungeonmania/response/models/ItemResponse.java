package dungeonmania.response.models;

import java.util.Objects;

public final class ItemResponse {
    private final String id;
    private final String type;

    public ItemResponse(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public final String getType() {
        return type;
    }

    public final String getId() {
        return id;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof ItemResponse)) {
            return false;
        }
        ItemResponse itemResponse = (ItemResponse) o;
        return Objects.equals(id, itemResponse.id) && Objects.equals(type, itemResponse.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type);
    }

}
