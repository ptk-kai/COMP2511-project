package dungeonmania.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.StaticEntities.FloorSwitch;
import dungeonmania.Interfaces.Logic;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Position;

public class LogicHelper {
    
    public static Boolean logicProcess(String logicString, List<Entity> cardinallyEntities, List<Boolean> results) {
        
        switch (logicString) {

            case DungeonMacro.AND:

                long switchCount = cardinallyEntities.stream()
                    .filter(e -> e instanceof FloorSwitch)
                    .count();
                if (switchCount >= 3) {
                    Map<Entity,Boolean> map = new HashMap<>();
                    for (int i = 0; i < results.size(); i++) {
                        map.put(cardinallyEntities.get(i), results.get(i));
                    }
                    return map.entrySet()
                        .stream()
                        .filter(entry -> entry.getKey() instanceof FloorSwitch)
                        .allMatch(entry -> entry.getValue() == true);
                }
            
                return activeSignalHelper(results) >= 2;
                
            case DungeonMacro.OR:
                
                return activeSignalHelper(results) >= 1;

            case DungeonMacro.XOR:
                
                return activeSignalHelper(results) == 1;

            case DungeonMacro.CO_AND:
                
                List<Boolean> isCurrTickActive = cardinallyEntities.stream()
                    .map(e -> (Logic) e)
                    .map(e -> e.checkActiveCurrentTick())
                    .collect(Collectors.toList());

                return activeSignalHelper(isCurrTickActive) >= 2;

            default:
                return false;
        }
    }
    

    public static long activeSignalHelper(List<Boolean> results) {
        
        return results.stream()
            .filter(result -> result == true)
            .count();
    }


    public static List<Entity> getCardinallySingalDeliverer(Entity input, Map<Position,List<Entity>> entityMap) {
        List<Position> cardinallyPos = input.getPosition().getCardinallyAdjacentPositions();
        List<Entity> cardinallyEntities = cardinallyPos.stream()
            .map(pos -> entityMap.getOrDefault(pos,new ArrayList<Entity>()))
            .flatMap(List::stream)
            .collect(Collectors.toList());
        
        return MapHelper.getLogicTransmitEntityList(cardinallyEntities);
    }


    public static boolean getSingalStatus(Entity input, Map<Position,List<Entity>> entityMap, List<Entity> checkedEntities, 
        boolean updateCurr) {
        
        List<Entity> checkTargets = LogicHelper.getCardinallySingalDeliverer(input, entityMap);
       
        // checkedEntities add this
        // Avoid stackOverFlow
        // Reason : what happen if two AND logic entity adjacent to each other?
        if (!checkedEntities.contains(input)) {
            checkedEntities.add(input);
        }
        
        checkTargets = checkTargets.stream()
            .filter(target -> !checkedEntities.contains(target))
            .collect(Collectors.toList());
        //checkedEntities.addAll(checkTargets);

        List<Boolean> results = checkTargets.stream()
            .map(target -> (Logic) target)
            .map(target -> target.getSingalStatus(entityMap, checkedEntities, false))
            .collect(Collectors.toList());
        
        
       
        Logic logicEntity = (Logic) input;
        if (updateCurr) {
            logicEntity.updatePrev(logicEntity.getCurrStatus());
            logicEntity.updateCurr(LogicHelper.logicProcess(logicEntity.getLogic(), checkTargets, results));
            return logicEntity.getCurrStatus();
        }


        return LogicHelper.logicProcess(logicEntity.getLogic(), checkTargets, results);
    }

}
