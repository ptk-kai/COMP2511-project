package dungeonmania;

import dungeonmania.util.Position;
import java.util.List;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import dungeonmania.Entity.*;


import java.util.Map;
import java.util.HashMap;


public class DungeonGenerator {
    
    public Map<Position,List<Entity>> entityMap = new HashMap<>();

    public Map<Position,List<Entity>> getEntityMap() {
        return this.entityMap;
    }

    public static String generateDungeon(int xStart, int yStart, int xEnd, int yEnd) {
        
        Position start = new Position(xStart, yStart);
        Position end = new Position(xEnd, yEnd);
        int width = xEnd - xStart + 1;
        int height = yEnd - yStart + 1; 

        JSONObject dungeonInJson = toJson(randomizedDungeon(start, end, width, height), width, height, start);
        
        String randomDungeonName = UUID.randomUUID().toString();
        Path savePath = Paths.get(DungeonManiaController.GENERATED_GAMES_DIR + randomDungeonName);
        try {
            Path absolutePath = savePath.toAbsolutePath();
            FileOutputStream fileOutStream = new FileOutputStream(absolutePath.toString());
            OutputStreamWriter out = new OutputStreamWriter(fileOutStream);
            
            out.write(dungeonInJson.toString(2));
            out.flush();
            out.close();

        } catch (Exception e) {
            System.out.println("Failed to save random generate dungeon");
            e.printStackTrace(); 
        }
        return randomDungeonName;
    }

    private static Boolean[][] randomizedDungeon(Position start, Position end, int width, int height) {
        //this algorithm is derived from the spec

        //let maze be a 2D array of booleans (of size width and height) default false
        // false representing a wall and true representing empty space

        Boolean[][] maze = new Boolean[height][width];
        
        // Arrays.fill only suitable for 1D array
        for (int i = 0 ; i < maze.length; i ++) {
            for (int j = 0 ; j < maze[0].length; j ++) {
                maze[i][j] = false;
            }
        }

        //maze[start] = empty
        maze[1][1] = true;

        // let options be a list of positions
        // add to options all neighbours of 'start' not on boundary that are of distance 2 away and are walls
        List<Position> options = new ArrayList<>(getNeighbours(new Position(1,1) , 2, maze, 
            width, height, false));
        
        Random rand = new Random();

        //  while options is not empty:
        while (!options.isEmpty()) {
            
            // let next = remove random from options
            Position next = options.remove(rand.nextInt(options.size()));

            // let neighbours = each neighbour of distance 2 from next not on boundary that are empty
            List<Position> nextsNeighbours = getNeighbours(next, 2, maze, 
                width, height, true);

            // if next's neighbours are not empty:
            if (nextsNeighbours.size() > 0) {

                // let neighbour = random from neighbours
                Position neighbour = nextsNeighbours.remove(rand.nextInt(nextsNeighbours.size()));

                // maze[ next ] = empty (i.e. true)
                maze[next.getX()][next.getY()] = true;
                
                // maze[ position inbetween next and neighbour ] = empty (i.e. true)
                Position inBetween = Position.getInBetween(next, neighbour);
                maze[inBetween.getX()][inBetween.getY()] = true;
                
                // maze[ neighbour ] = empty (i.e. true)
                maze[neighbour.getX()][neighbour.getY()] = true;
            }

            // add to options all neighbours of 'next' not on boundary that are of distance 2 away and are walls
            List <Position> nextPos1 = getNeighbours(next, 2, maze, 
                width, height, false);
            options.addAll(nextPos1);
        }
        
        // at the end there is still a case where our end position isn't connected to the map
        // we don't necessarily need this, you can just keep randomly generating maps (was original intention)
        // but this will make it consistently have a pathway between the two.
        // if maze[end] is a wall:
        // Ensure exit is accessable
        
        if (!maze[height - 2][width - 2]) {
            // maze[end] = empty
            maze[height - 2][width - 2] = true;
            
            // let neighbours = neighbours not on boundary of distance 1 from maze[end]
            List <Position> exitNeighbours = getAllNeighbours(new Position(height - 2, width - 2), 1, width, height);
            List <Position> emptyNeighbours = exitNeighbours
                .stream()
                .filter(pos -> maze[pos.getX()][pos.getY()] == true)
                .collect(Collectors.toList());

            // if there are no cells in neighbours that are empty:
            if (emptyNeighbours.size() == 0) {
                
                // connect exit to the grid
                // let neighbour = random from neighbours
                Position posChosenToFree = exitNeighbours.remove(rand.nextInt(exitNeighbours.size()));
                maze[posChosenToFree.getX()][posChosenToFree.getY()] = true;
            }
        }
        
        // generate the maze
        return maze;
    }

    //get neighbours(either wall or empty)
    private static List<Position> getNeighbours(Position position, int distance, 
            Boolean[][] maze, int width, int height, boolean requiredType ) {
        
        List<Position> neighbours = Position.getNeighbours(position, distance);

        return neighbours.stream()
                .filter(pos -> (pos.getX() > 0 && pos.getX() < height - 1)
                    && (pos.getY() > 0 && pos.getY() < width - 1))
                .filter(pos -> maze[pos.getX()][pos.getY()] == requiredType)
                .collect(Collectors.toList());
    }

    //get all neighbours(any kinds)
    private static List<Position> getAllNeighbours(Position position, int distance, int width, int height) {
        
        List<Position> neighbours = Position.getNeighbours(position, distance);
       
        return neighbours.stream()
                .filter(pos -> (pos.getX() > 0 && pos.getX() < height - 1)
                    &&(pos.getY() > 0 && pos.getY() < width - 1))
                .collect(Collectors.toList());
    }

    private static JSONObject toJson(Boolean[][] maze, int width, int height, Position start) {
        
        int xOffset = start.getX();
        int yOffset = start.getY();
        JSONObject randomMap = new JSONObject();
        JSONArray entityArray = new JSONArray();

        for (int i = 0 ; i < maze.length; i ++) {
            
            for (int j = 0 ; j < maze[0].length; j ++) {
                
                JSONObject eachEntity = new JSONObject();
                eachEntity.put("x", j + xOffset);
                eachEntity.put("y", i + yOffset);
                if (i == 1  && j == 1 ) {
                    eachEntity.put("type", "player");
                    entityArray.put(eachEntity);
                }
                if (i == height - 2 && j == width - 2) {
                    eachEntity.put("type", "exit");
                    entityArray.put(eachEntity);
                }
                else if (maze[i][j] == false) {
                    eachEntity.put("type", "wall");
                    entityArray.put(eachEntity);
                }
            }
        }
        randomMap.put("entities", entityArray);
        
        JSONObject exitGoal = new JSONObject();
        exitGoal.put("goal", "exit");
        
        randomMap.put("goal-condition", exitGoal);

        return randomMap;
    }
}