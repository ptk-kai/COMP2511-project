package dungeonmania.Entity.MovingEntity;

import java.util.Random;

import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Player;
import dungeonmania.util.Position;

public class Assassin extends Mercenary {
    private double bribeFailRate;
    private int reconRadius;
    public Assassin(Position position, double attack, double health,int bribeAmount, int attackbuff, int defbuff, double bribeFailRate, int reconRadius) {
        super(position, attack, health,bribeAmount, attackbuff, defbuff);
        this.bribeFailRate = bribeFailRate;
        this.reconRadius = reconRadius;
        this.setType(DungeonMacro.ASSASSIN);
    }

    @Override
    public boolean bribeSuccess() {
        return (new Random()).nextDouble() > bribeFailRate;
    }

    @Override
    public boolean canNotSeePlayer(Player player) {
        return player.isInvisible() && !this.getPosition().inRange(reconRadius, player.getPosition());
    }
    
}
