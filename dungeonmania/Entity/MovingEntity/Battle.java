package dungeonmania.Entity.MovingEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.Collectable.Bow;
import dungeonmania.Entity.Collectable.Collectable;
import dungeonmania.Entity.Collectable.MidnightArmour;
import dungeonmania.Entity.Collectable.Shield;
import dungeonmania.Entity.Collectable.Sword;
import dungeonmania.response.models.BattleResponse;
import dungeonmania.response.models.RoundResponse;

public class Battle implements Serializable {
    private List<Round> rounds = new ArrayList<>();
    private Player player;
    private BattleOpponent enemy;
    private double initialPlayerHealth;
    private double initialEnemyHealth;

    public Battle(Player player, BattleOpponent enemy) {
        this.player = player;
        this.initialPlayerHealth = player.getHealth();
        this.enemy = enemy;
        this.initialEnemyHealth = enemy.getHealth();
    }

    public List<Round> getRounds() {
        return this.rounds;
    }

    public void setRounds(List<Round> rounds) {
        this.rounds = rounds;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public BattleOpponent getEnemy() {
        return this.enemy;
    }

    public void setEnemy(BattleOpponent enemy) {
        this.enemy = enemy;
    }

    public void setInitialEnemyHealth(double initialEnemyHealth) {
        this.initialEnemyHealth = initialEnemyHealth;
    }

    public void action() {
        // invincible state can one-hit kill Hydra
        if (player.isInvincible()) {
            //assumption, can just store potion as weaponsUsed
            //waiting for tutor to confirm deltahealth for opponent
            Round round = new Round(0, -1 * this.getEnemy().getHealth(), Arrays.asList(player.getCurrentBuff()));
            this.rounds.add(round);
            enemy.setHealth(0);
            return;
        }

        List<Collectable> weaponsUsed = new ArrayList<>();
        double[] damageArray = playerBattleHelper(player, enemy.getAttack(), weaponsUsed);
        double dealDamage = damageArray[0];
        double recvDamage = damageArray[1];

        boolean isHydra = enemy instanceof Hydra;
        boolean checkHydraGetHPRecover = false;

        // Assumed ally's attack buff will multiply by bow again
        while (player.getHealth() > 0 && enemy.getHealth() > 0) {
            
            double newPlayerHealth = player.getHealth() - recvDamage;
            double newEnemyHealth = enemy.getHealth() - dealDamage;
            
            // Hydra has special battle-in calculation. 
            // Assume Hydra can have more HP than original (through HP regen)
            if (isHydra) {
                Hydra currEnemy = (Hydra) enemy;
                checkHydraGetHPRecover = ((Hydra) enemy).checkHaveHPRecoverThisTurn();
                if (checkHydraGetHPRecover) {
                    newEnemyHealth = currEnemy.getHealth() + currEnemy.getHydraHPRecoverAmount();
                }
            }

            double deltaPlayerHealth = newPlayerHealth - player.getHealth();
            double deltaEnemyHealth = newEnemyHealth - enemy.getHealth();
            
            Round round = new Round(deltaPlayerHealth, deltaEnemyHealth, weaponsUsed);
            this.rounds.add(round);

            player.setHealth(newPlayerHealth);
            enemy.setHealth(newEnemyHealth);
        }
    }


    public double getInitialPlayerHealth() {
        return this.initialPlayerHealth;
    }

    public double getInitialEnemyHealth() {
        return this.initialEnemyHealth;
    }


    public BattleResponse toBattleResponse() {
        List<RoundResponse> roundResponse = rounds.stream().map(round -> round.toRoundResponse()).collect(Collectors.toList());
        BattleResponse br = new BattleResponse(enemy.getType(), roundResponse, getInitialPlayerHealth(), getInitialEnemyHealth());
        return br;
    }


    private double[] playerBattleHelper(Player player, double enemyATK, List<Collectable> weaponsUsed) {
        
        // buffArray in format [attack bonus, defence bouns]
        int[] buffArray = player.calculateAllyBuff();
        
        double swordModifier = 0;
        double shieldModifier = 0;
        double bowModifier = 1;
        double midNightArmourATK = 0;
        double midNightArmourDEF = 0;
        
        Sword sword = (Sword) player.getHasSword();
        Shield shield = (Shield) player.getHasShield();
        Bow bow = (Bow) player.getHasBow();
        MidnightArmour midNightArmour = (MidnightArmour) player.getHasMidNightArmour();
        
        if (!Objects.isNull(sword)) {
            sword.useInBattle();
            swordModifier = sword.getATKModifier();
            weaponsUsed.add(sword);
        }

        if (!Objects.isNull(shield)) {
            shield.useInBattle();
            shieldModifier = shield.getDEFModifier();
            weaponsUsed.add(shield);
        }

        if (!Objects.isNull(bow)) {
            bow.useInBattle();
            bowModifier = 2;
            weaponsUsed.add(bow);
        }

        if (!Objects.isNull(midNightArmour)) {
            midNightArmour.useInBattle();
            midNightArmourATK = midNightArmour.getATKModifier();
            midNightArmourDEF = midNightArmour.getDEFModifier();
            weaponsUsed.add(midNightArmour);
        }

        double finalDealDamage = (bowModifier * (player.getAttack() + swordModifier 
            + midNightArmourATK + buffArray[0])) / 5.0;

        double enemyAttackAfterDef = enemy.getAttack() - shieldModifier - midNightArmourDEF - buffArray[1];
        double finalRecvDamage = (enemyAttackAfterDef < 0 ? 0 : enemyAttackAfterDef) / 10.0;

        return new double[]{finalDealDamage, finalRecvDamage};
    }
}
