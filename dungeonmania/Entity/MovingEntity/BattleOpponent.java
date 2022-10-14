package dungeonmania.Entity.MovingEntity;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Interfaces.ActivitySubscriber;
import dungeonmania.Interfaces.Moving;
import dungeonmania.Interfaces.Tick;
import dungeonmania.util.Position;

public abstract class BattleOpponent extends Entity implements Moving, Tick {

    private double health;
    private double attack;

    public BattleOpponent(Position position, String type, boolean isInteractable, double attack, double health) {
        super(position, type, isInteractable);
        this.health = health;
        this.attack = attack;
    }

    @Override
    public void actionPlayerSamePosition(Player player, ActivitySubscriber subscriber) {
        if (player.isInvisible()) {
            return;
        }
        if (this.getHealth() > 0) {
            Battle battle = new Battle(player, this);
            battle.action();
            subscriber.notifySubscriberOfBattle(battle);
        }
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getAttack() {
        return attack;
    }

    public void setAttack(double attack) {
        this.attack = attack;
    }

    public boolean checkAlly() {
        return false;
    }
}
