package dungeonmania.Entity.MovingEntity;

import java.util.List;
import java.util.Map;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.MovingEntity.MovingState.MovementState;
import dungeonmania.Entity.MovingEntity.MovingState.RandomState;
import dungeonmania.Entity.MovingEntity.MovingState.RunningAwayState;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

/**
 * From assumptions thread:
 * Whether zombies/the player can traverse through zombie spawners is undefined
 * Whether zombies and mercs can push boulders is undefined
 * Whether zombies are blocked by portals or can they move on top of them without any effect is undefined
 * 
 * Assumption:
 * 1. zombie blocked by spawner
 * 2. zombie blocked by boulder
 * 3. zombie blocked by portal
 */
public class Zombie extends BattleOpponent {
    private String spawnerId = "";

    private MovementState movementState;

    public Zombie(Position position, double attack, double health) {
        super(position, DungeonMacro.ZOMBIE, false, attack, health);
    }

    public void changeMovementState(MovementState movementState) {
        this.movementState = movementState;
    }

    public String getSpawnerId() {
        return this.spawnerId;
    }

    public void setSpawnerId(String spawnerId) {
        this.spawnerId = spawnerId;
    }

    @Override
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction, ActivitySubscriber subscriber) {
        Position newPosition = movementState.move();
        return newPosition;
    }

    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {
        if (player.isInvincible()) {
            movementState = new RunningAwayState(this, entityMap, player);
        } else {
            movementState = new RandomState(this, entityMap);
        }
        move(entityMap, null, null);
    }
}
