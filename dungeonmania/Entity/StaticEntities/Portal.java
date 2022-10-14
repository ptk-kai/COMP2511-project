package dungeonmania.Entity.StaticEntities;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.map.MapHelper;
import dungeonmania.util.*;

public class Portal extends StaticEntity {
    String colour;

    public Portal(Position position, String colour) {
        super(position, DungeonMacro.PORTAL, false);
        this.colour = colour;
    }
    
    public String getColour() {
        return this.colour;
    }

    public Position getPossiblePos(Map<Position, List<Entity>> entityMap) {
        
        List<Entity> entityList = MapHelper.getEntityList(entityMap);

        Portal pairedPortal = this.getPairedPortal(entityList);
        if (pairedPortal == null) {
            return null;
        }
        
        List<Position> possiblePos = pairedPortal.getPosition().getCardinallyAdjacentPositions();
        List<Position> ableToTeleportPos = possiblePos.stream()
            .filter(pos -> !checkPosBlocked(entityMap.getOrDefault(pos, new ArrayList<>())))
            .collect(Collectors.toList());

        if (ableToTeleportPos.size() == 0) {
            return null;
        }
        Random rand = new Random();
        Position teleportPossiblePos = ableToTeleportPos.get(rand.nextInt(ableToTeleportPos.size()));
        // Check chanined portal
        List<Entity> entitiesAtPossiblePos = entityMap.getOrDefault(teleportPossiblePos, new ArrayList<>());
        if (MapHelper.checkListHasTypeEntity(entitiesAtPossiblePos, DungeonMacro.PORTAL)) {
            Portal portal = (Portal) MapHelper.getFirstEntityInType(entitiesAtPossiblePos, DungeonMacro.PORTAL);
            return portal.getPossiblePos(entityMap);
        }
        return teleportPossiblePos;
    }

    private boolean matchColour(Portal inputPortal) {
        return inputPortal.getColour().equals(this.colour);
    }

    // Assumption: We'll only given two portals in same colour.
    private Portal getPairedPortal(List<Entity> entityList) {
        List<Portal> curPortals = MapHelper.getPortals(entityList);
        return curPortals.stream()
            .filter(portal -> this.matchColour(portal) && !portal.equals(this))
            .findFirst()
            .orElse(null);
    }

}
