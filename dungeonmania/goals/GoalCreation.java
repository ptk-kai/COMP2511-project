package dungeonmania.goals;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dungeonmania.map.DungeonMap;

public class GoalCreation {

    public static Goal createGoalFromJson(JsonObject goalInJson, DungeonMap currMap) {
        
        String baseGoal = goalInJson.get("goal").getAsString();

        if (goalInJson.get("subgoals") == null) {
            return GoalFactory.createGoal(baseGoal, currMap);
        } else {
            GoalComposite goalEntity = (GoalComposite) GoalFactory.createGoal(baseGoal, currMap);
            for (JsonElement singleElement : goalInJson.getAsJsonArray("subgoals")) {
                goalEntity.add(createGoalFromJson(singleElement.getAsJsonObject(), currMap));
            }
            return goalEntity;
        }
    }
}
