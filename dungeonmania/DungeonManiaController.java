package dungeonmania;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.*;
import dungeonmania.Entity.Collectable.Collectable;
import dungeonmania.Entity.MovingEntity.Zombie;
import dungeonmania.Interfaces.Interact;
import dungeonmania.Interfaces.SpecialEffects;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.map.DungeonMap;
import dungeonmania.map.MapHelper;
import dungeonmania.response.models.DungeonResponse;
import dungeonmania.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;
import dungeonmania.util.Position;
import spark.utils.IOUtils;

import org.json.JSONException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class DungeonManiaController {
    public static final String SAVED_GAMES_DIR = "/tmp/saved_games/";
    public static final String GENERATED_GAMES_DIR = "/tmp/generated_dungeons/";
    private DungeonMap dungeon;
    
    public Map<Position,List<Entity>> entityMap = new HashMap<>();

    public Map<Position,List<Entity>> getEntityMap() {
        return this.entityMap;
    }

    public String getSkin() {
        return "default";
    }

    public String getLocalisation() {
        return "en_US";
    }

    /**
     * /dungeons
     */
    public static List<String> dungeons() {
        return FileLoader.listFileNamesInResourceDirectory("dungeons");
    }

    /**
     * /configs
     */
    public static List<String> configs() {
        return FileLoader.listFileNamesInResourceDirectory("configs");
    }

    /**
     * Creates a new game
     * dungeonName is the name of the dungeon map (corresponding to a JSON file
     * stored in the model)
     * configName is the name of the configuration file.
     * 
     * @throws FileNotFoundException
     * @throws JSONException
     */
    public DungeonResponse newGame(String dungeonName, String configName) throws IllegalArgumentException, JSONException {
        if (Objects.isNull(dungeonName) || Objects.isNull(configName)) {
            throw new IllegalArgumentException("Invalid dungeonName or configName - file doesn't exist");
        }
        dungeon = new DungeonMap(dungeonName, configName);
        return dungeon.toDungeonResponse();
    }

    /**
     * /game/dungeonResponseModel
     */
    public DungeonResponse getDungeonResponseModel() {
        return dungeon.toDungeonResponse();
    }

    /**
     * /game/tick/item
     */
    public DungeonResponse tick(String itemUsedId) throws IllegalArgumentException, InvalidActionException {
        Collectable chosenItem = MapHelper.getEntityFromInventroy(dungeon.getPlayer(), itemUsedId);

        if (chosenItem == null ) {
            dungeon.notifyEntityOfTick();
            throw new InvalidActionException("Not a valid item Id player owned");
        }

        if (chosenItem instanceof SpecialEffects) {
            SpecialEffects specialEffectsItem = (SpecialEffects) chosenItem;
            specialEffectsItem.enactSpecialAction(dungeon.getEntityMap());
            dungeon.notifyEntityOfTick();
        } else {
            dungeon.notifyEntityOfTick();
            throw new IllegalArgumentException("The item has no special actions");
        }

        return dungeon.toDungeonResponse();
    }

    /**
     * /game/tick/movement
     */
    public DungeonResponse tick(Direction movementDirection) {
        boolean initiatedTimeTravel = dungeon.attemptToMovePlayer(movementDirection);

        if (!initiatedTimeTravel) {
            dungeon.notifyEntityOfTick();
        }
        return dungeon.toDungeonResponse();
    }

    /**
     * /game/build
     */
    public DungeonResponse build(String buildable) throws IllegalArgumentException, InvalidActionException {
        // Check given string is valid first
        if (!currCraftRecipes().contains(buildable)) {
            throw new IllegalArgumentException("Input string is not buildable");
        }

        Player player = dungeon.getPlayer();
        boolean canCraft = true;
        
        // Special Case for craft where we need check if Zombie exists
        if (buildable.equals(DungeonMacro.MIDNIGHT)) {
            canCraft = canCraftMidNightArmour();
        }
        if (canCraft) {
            canCraft = player.craftItem(buildable);
        }
        if (!canCraft) {
            throw new InvalidActionException("Craft failed. Insufficient items or Condition not fulfilled");
        }
        return dungeon.toDungeonResponse();
    }

    public List<String> currCraftRecipes() {
        return Arrays.asList(DungeonMacro.BOW, DungeonMacro.SHIELD, DungeonMacro.SCEPTRE, DungeonMacro.MIDNIGHT);
    }

    private boolean canCraftMidNightArmour() {
        return !MapHelper.getEnemies(this.dungeon.getEntityMap())
            .stream()
            .anyMatch(enemy -> enemy instanceof Zombie);
    }

    /**
     * /game/interact
     */
    public DungeonResponse interact(String entityId) throws IllegalArgumentException, InvalidActionException {
        Entity chosenEntity = MapHelper.getEntityById(dungeon.getEntityMap(), entityId);
        if (chosenEntity == null) {
            throw new IllegalArgumentException("Not a valid entity Id");
        }

        if (chosenEntity instanceof Interact) {
            Interact interactEntity = (Interact) chosenEntity;
            interactEntity.interact(dungeon.getPlayer(), dungeon.getEntityMap(), this.dungeon);
            this.dungeon.updateMapAfterAction();
        } else {
            throw new IllegalArgumentException("Can't interact with given entity");
        }

        return dungeon.toDungeonResponse();
    }

    /**
     * /game/save
     */
    public DungeonResponse saveGame(String name) throws IllegalArgumentException {
        if (Objects.isNull(name)) {
            throw new IllegalArgumentException("Not a valid file name");
        }
        try {
            Files.createDirectories(Paths.get(SAVED_GAMES_DIR));
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        try {
            FileOutputStream fileOut = new FileOutputStream(SAVED_GAMES_DIR + name);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(dungeon);
            out.close();
            fileOut.close();
        } catch (Exception e) {
            throw new IllegalArgumentException("can't save the game: " + name + " " + e);
        }

        return dungeon.toDungeonResponse();
    }

    /**
     * /game/load
     */
    public DungeonResponse loadGame(String name) throws IllegalArgumentException {
        try {
            FileInputStream fileIn = new FileInputStream(SAVED_GAMES_DIR + name);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            dungeon = (DungeonMap) in.readObject();
            in.close();
            fileIn.close();
        } catch (Exception e) {
            throw new IllegalArgumentException("can't load the game: " + name + " " + e);
        }
        return dungeon.toDungeonResponse();
    }

    /**
     * /games/all
     */
    public List<String> allGames() {
        File saveDirectory = new File(SAVED_GAMES_DIR);
        File[] filesSaved = saveDirectory.listFiles();
        if (Objects.isNull(filesSaved) || filesSaved.length == 0) {
            return new ArrayList<>();
        }
        List<File> files = Arrays.asList(filesSaved);
        return files.stream().map(file -> file.getName()).collect(Collectors.toList());
    }

    /**
     * /api/game/rewind
     */
    public DungeonResponse rewind(int ticks) throws IllegalArgumentException {
        if (ticks <= 0 || dungeon.getNumTicks() < ticks) {
            throw new IllegalArgumentException("ticks <=0 or number of ticks have not occurred yet");
        }
        dungeon.initiateTimeTravel(ticks);
        return dungeon.toDungeonResponse();
    }


    /**
     * /game/dungeonBuilder
     */
    public DungeonResponse generateDungeon(int xStart, int yStart, int xEnd, int yEnd, String configName) throws IllegalArgumentException {
        if (Objects.isNull(xStart) || Objects.isNull(yStart) || Objects.isNull(xEnd) || Objects.isNull(yEnd) || Objects.isNull(configName)) {
            throw new IllegalArgumentException("Invalid arguments");
        }

        try {
            Files.createDirectories(Paths.get(GENERATED_GAMES_DIR));
            String generatedDungeonName = DungeonGenerator.generateDungeon(xStart - 1, yStart -1, xEnd + 1, yEnd + 1);
            FileInputStream fileIn = new FileInputStream(GENERATED_GAMES_DIR + generatedDungeonName);
            String jsonTxt = IOUtils.toString(fileIn);
            JsonObject mapFile =  new Gson().fromJson(jsonTxt, JsonObject.class);
            dungeon = new DungeonMap(generatedDungeonName, configName, mapFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Can't find generated file");
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Can't read generated file");
        }

        return dungeon.toDungeonResponse();
    }
}
