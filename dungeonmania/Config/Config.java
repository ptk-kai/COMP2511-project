package dungeonmania.Config;

import java.io.Serializable;

public class Config implements Serializable{
    private int ally_attack;
    private int ally_defence;
    private int bribe_radius;
    private int bribe_amount;
    private int bomb_radius;
    private int bow_durability;
    private int player_health;
    private int player_attack;
    private int enemy_goal;
    private int invincibility_potion_duration;
    private int invisibility_potion_duration;
    private int mercenary_attack;
    private int mercenary_health;
    private int spider_attack;
    private int spider_health;
    private int spider_spawn_rate;
    private int shield_durability;
    private int shield_defence;
    private int sword_attack;
    private int sword_durability;
    private int treasure_goal;
    private int zombie_attack;
    private int zombie_health;
    private int zombie_spawn_rate;
    private int assassin_attack;
    private int assassin_bribe_amount;
    private double assassin_bribe_fail_rate;
    private int assassin_health;
    private int assassin_recon_radius;
    private int hydra_attack;
    private int hydra_health;
    private double hydra_health_increase_rate;
    private int hydra_health_increase_amount;
    private int mind_control_duration;
    private int midnight_armour_attack;
    private int midnight_armour_defence;

    public int getAllyAttack() {
        return this.ally_attack;
    }

    public int getAllyDefence() {
        return this.ally_defence;
    }

    public int getBribeRadius() {
        return this.bribe_radius;
    }

    public int getBribeAmount() {
        return this.bribe_amount;
    }

    public int getBombRadius() {
        return this.bomb_radius;
    }

    public int getBowDurability() {
        return this.bow_durability;
    }

    public int getPlayerHealth() {
        return this.player_health;
    }

    public int getPlayerAttack() {
        return this.player_attack;
    }

    public int getEnemyGoal() {
        return this.enemy_goal;
    }

    public int getInvincibilityPotionDuration() {
        return this.invincibility_potion_duration;
    }

    public int getInvisibilityPotionDuration() {
        return this.invisibility_potion_duration;
    }

    public int getMercenaryAttack() {
        return this.mercenary_attack;
    }

    public int getMercenaryHealth() {
        return this.mercenary_health;
    }

    public int getSpiderAttack() {
        return this.spider_attack;
    }

    public int getSpiderHealth() {
        return this.spider_health;
    }

    public int getSpiderSpawnRate() {
        return this.spider_spawn_rate;
    }

    public int getShieldDurability() {
        return this.shield_durability;
    }

    public int getShieldDefence() {
        return this.shield_defence;
    }

    public int getSwordAttack() {
        return this.sword_attack;
    }

    public int getSwordDurability() {
        return this.sword_durability;
    }

    public int getTreasureGoal() {
        return this.treasure_goal;
    }

    public int getZombieAttack() {
        return this.zombie_attack;
    }

    public int getZombieHealth() {
        return this.zombie_health;
    }

    public int getZombieSpawnRate() {
        return this.zombie_spawn_rate;
    }
    
    public int getAssassinAttack() {
        return this.assassin_attack;
    } 

    public int getAssassinBribeAmount() {
        return this.assassin_bribe_amount;
    }
  
    public double getAssassinBribeFailRate() {
        return this.assassin_bribe_fail_rate;
    }

    public int getAssassinHealth() {
        return this.assassin_health;
    }
    
    public int getAssassinReconRadius() {
        return this.assassin_recon_radius;
    }
    
    public int getHydraATK() {
        return this.hydra_attack;
    }

    public int getHydraHP() {
        return this.hydra_health;
    }

    public double getHydraHPRecoverChance() {
        return this.hydra_health_increase_rate;
    }

    public int getHydraHPRecoverAmount() {
        return this.hydra_health_increase_amount;
    }

    public int getMindControlDuration() {
        return this.mind_control_duration;
    }

    public int getMidNightArmourtATK() {
        return this.midnight_armour_attack;
    }
    
    public int getgetMidNightArmoutDEF() {
        return this.midnight_armour_defence;
    }
    
}
