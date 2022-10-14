package dungeonmania.Entity.MovingEntity;

import java.util.List;
import java.util.Map;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.MovingEntity.MercenaryState.AllyMercenary;
import dungeonmania.Entity.MovingEntity.MercenaryState.EnemyMercenary;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.Interfaces.Interact;
import dungeonmania.Interfaces.MercenaryState;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public class Mercenary extends BattleOpponent implements Interact {
    MercenaryState emenymercenary;
    MercenaryState allymercenary;
    MercenaryState state;
    private int attackBuff;
    private int defBuff;
    private int bribeAmount;
    
    public Mercenary(Position position, double attack, double health, int bribeAmount, int attackbuff, int defbuff) {
        super(position, DungeonMacro.MERCENARY,true, attack, health);
        this.emenymercenary = new EnemyMercenary(this);
        this.allymercenary = new AllyMercenary(this);
        this.state = this.emenymercenary;
        this.attackBuff = attackbuff;
        this.defBuff = defbuff;
        this.bribeAmount = bribeAmount;
    }

    @Override
    public Position move(Map<Position, List<Entity>> entityMap, Direction direction, ActivitySubscriber subscriber) {
        return state.move(entityMap, direction);
    }

    @Override
    public boolean movementBlocked(List<Entity> entities) {
        return state.movementBlocked(entities);
    }

    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {
        state.tick(entityMap, player);
    }

    @Override
    public void actionPlayerSamePosition(Player player, ActivitySubscriber battleSubscriber) {
        if (this.checkAlly()) {
            return;
        } else {
            super.actionPlayerSamePosition(player, battleSubscriber);
        }
	}

    @Override
    public void interact(Player player, Map<Position, List<Entity>> entityMap, ActivitySubscriber subscriber) throws InvalidActionException {
        state.interact(this, player, entityMap);
        subscriber.updateHistoricalMercenary(this);
    }

    public boolean bribeSuccess() {
        return true;
    }

    public boolean canNotSeePlayer(Player player) {
        return player.isInvisible();
    }

    public int getBribeAmount() {
        return this.bribeAmount;
    }

    public void setState(MercenaryState state) {
        this.state = state;
    }

    public MercenaryState getState() {
        return this.state;
    }

    public MercenaryState getAllyState() {
        return this.allymercenary;
    }

    public MercenaryState getEnemyState() {
        return this.emenymercenary;
    }

    @Override
    public boolean checkAlly() {
        return this.state instanceof AllyMercenary;
    }

    public int getAttackBuff() {
        return this.attackBuff;
    }

    public int getDefenceBuff() {
        return this.defBuff;
    }

}
