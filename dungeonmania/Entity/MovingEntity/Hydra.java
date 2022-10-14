package dungeonmania.Entity.MovingEntity;

import java.util.List;
import java.util.Map;
import java.util.Random;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.MovingEntity.MovingState.MovementState;
import dungeonmania.Entity.MovingEntity.MovingState.RandomState;
import dungeonmania.Entity.MovingEntity.MovingState.RunningAwayState;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Hydra extends BattleOpponent{

    private MovementState movementState;
    private double HPRecoverChance;
    private double HPRecoverAmount;

    public Hydra(Position position, double attack, double health, double HPRecoverChance, double HPRecoverAmount) {
        super(position, DungeonMacro.HYDRA, false, attack, health);
        this.HPRecoverChance = HPRecoverChance;
        this.HPRecoverAmount = HPRecoverAmount;
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

    public double getHydraHPRecoverAmount() {
        return this.HPRecoverAmount;
    }
    
    public boolean checkHaveHPRecoverThisTurn() {
        Random rand = new Random();
        return (rand.nextDouble() <= this.HPRecoverChance);
    }
}
