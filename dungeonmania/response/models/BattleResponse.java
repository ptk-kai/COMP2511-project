package dungeonmania.response.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class BattleResponse {
    private final String enemy;
    private final double initialPlayerHealth;
    private final double initialEnemyHealth;
    private final List<RoundResponse> rounds;
    
    public BattleResponse(){
        this.initialPlayerHealth = 0;
        this.initialEnemyHealth = 0;
        this.enemy = "";
        this.rounds = new ArrayList<RoundResponse>();
    }

    public BattleResponse(String enemy, List<RoundResponse> rounds, double initialPlayerHealth, double initialEnemyHealth) {
        this.initialPlayerHealth = initialPlayerHealth;
        this.initialEnemyHealth = initialEnemyHealth;
        this.enemy = enemy;
        this.rounds = rounds;
    }

    public final String getEnemy(){
        return enemy;
    }

    public final double getInitialPlayerHealth(){
        return initialPlayerHealth;
    }

    public final double getInitialEnemyHealth(){
        return initialEnemyHealth;
    }

    public final List<RoundResponse> getRounds(){
        return rounds;
    }


    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof BattleResponse)) {
            return false;
        }
        BattleResponse battleResponse = (BattleResponse) o;
        return Objects.equals(enemy, battleResponse.enemy) && initialPlayerHealth == battleResponse.initialPlayerHealth && initialEnemyHealth == battleResponse.initialEnemyHealth && Objects.equals(rounds, battleResponse.rounds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(enemy, initialPlayerHealth, initialEnemyHealth, rounds);
    }

}
