package dungeonmania.response.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DungeonResponse {
    private final String dungeonId;
    private final String dungeonName;
    private final List<EntityResponse> entities;
    private final List<ItemResponse> inventory;
    private final List<BattleResponse> battles;
    private final List<String> buildables;
    private final String goals;
    private final List<AnimationQueue> animations;

    public DungeonResponse(String dungeonId, String dungeonName, List<EntityResponse> entities,
            List<ItemResponse> inventory, List<BattleResponse> battles, List<String> buildables, String goals) {
        this(dungeonId, dungeonName, entities, inventory, battles, buildables, goals, new ArrayList<>());
    }

    public DungeonResponse(String dungeonId, String dungeonName, List<EntityResponse> entities,
            List<ItemResponse> inventory, List<BattleResponse> battles, List<String> buildables, String goals,
            List<AnimationQueue> animations) {
        this.dungeonId = dungeonId;
        this.dungeonName = dungeonName;
        this.entities = entities;
        this.inventory = inventory;
        this.battles = battles;
        this.buildables = buildables;
        this.goals = goals;
        this.animations = animations;
    }

    public List<AnimationQueue> getAnimations() {
        return animations;
    }

    public final String getDungeonName() {
        return dungeonName;
    }

    public final List<ItemResponse> getInventory() {
        return inventory;
    }

    public final List<BattleResponse> getBattles(){
        return battles;
    }

    public final List<String> getBuildables() {
        return buildables;
    }

    public final String getGoals() {
        return goals;
    }

    public final String getDungeonId() {
        return dungeonId;
    }

    public final List<EntityResponse> getEntities() {
        return entities;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof DungeonResponse)) {
            return false;
        }
        DungeonResponse dungeonResponse = (DungeonResponse) o;
        return Objects.equals(dungeonId, dungeonResponse.dungeonId) && Objects.equals(dungeonName, dungeonResponse.dungeonName) && Objects.equals(entities, dungeonResponse.entities) && Objects.equals(inventory, dungeonResponse.inventory) && Objects.equals(battles, dungeonResponse.battles) && Objects.equals(buildables, dungeonResponse.buildables) && Objects.equals(goals, dungeonResponse.goals) && Objects.equals(animations, dungeonResponse.animations);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dungeonId, dungeonName, entities, inventory, battles, buildables, goals, animations);
    }

}
