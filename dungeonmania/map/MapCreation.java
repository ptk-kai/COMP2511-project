package dungeonmania.map;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dungeonmania.Config.Config;
import dungeonmania.Entity.*;
import dungeonmania.goals.GoalCreation;
import dungeonmania.util.FileLoader;
import dungeonmania.util.Position;

public class MapCreation {
    
    public static void loadMapFile(String fileName, DungeonMap currDungeon) {
        try {
            String fileContent = FileLoader.loadResourceFile("/dungeons/" + fileName + ".json");
            JsonObject mapFile =  new Gson().fromJson(fileContent, JsonObject.class);

            createMapEntityFromJsonObject(currDungeon, mapFile);

        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("File not found.");
        }
    }

    public static void createMapEntityFromJsonObject(DungeonMap currDungeon, JsonObject mapFile) {
        for (JsonElement singElement : mapFile.getAsJsonArray("entities")) {
            JsonObject jsonObj = singElement.getAsJsonObject();
            String type = jsonObj.get("type").getAsString();
            int x = jsonObj.get("x").getAsInt();
            int y = jsonObj.get("y").getAsInt();
            
            Position position = new Position(x, y);
            Entity entity = EntityFactory.createEntity(type, position, jsonObj, currDungeon.getConfig());
            MapHelper.mapAddEntity(currDungeon.getEntityMap(), position, entity);
            if (entity instanceof Player) {
                currDungeon.setPlayer((Player) entity);
            }
        }
        currDungeon.setGoal(GoalCreation.createGoalFromJson(
            mapFile.getAsJsonObject("goal-condition"), currDungeon));
    }

    public static Config loadConfigFile(String configName) {
        try {
            String fileContent = FileLoader.loadResourceFile("configs/" + configName + ".json");
            return new Gson().fromJson(fileContent, Config.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("File not found.");
        }
    }


}
