package dungeonmania.goals;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.map.DungeonMap;

public class GoalFactory {
    public static Goal createGoal(String type, DungeonMap currMap) throws IllegalArgumentException {
        switch (type) {
            case "enemies":
                return new GoalEnemies(currMap);
            case DungeonMacro.TREASURE:
                return new GoalTreasure(currMap);
			case DungeonMacro.EXIT:
				return new GoalExit(currMap);
			case "boulders":
				return new GoalBoulders(currMap);
			case "AND":
				return new GoalAnd(currMap);
			case "OR":
				return new GoalOr(currMap);
			default:
				throw new IllegalArgumentException("Invalid type of goal");
		}
    }


}
