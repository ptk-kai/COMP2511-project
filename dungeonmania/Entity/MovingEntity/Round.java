package dungeonmania.Entity.MovingEntity;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Entity.Collectable.Collectable;
import dungeonmania.response.models.ItemResponse;
import dungeonmania.response.models.RoundResponse;

public class Round implements Serializable {
    private double deltaPlayerHealth;
    private double deltaEnemyHealth;
    List<Collectable> weaponryUsed;

    public Round(double deltaPlayerHealth, double deltaEnemyHealth, List<Collectable> weaponryUsed) {
        this.deltaPlayerHealth = deltaPlayerHealth;
        this.deltaEnemyHealth = deltaEnemyHealth;
        this.weaponryUsed = weaponryUsed;
    }

    public double getDeltaPlayerHealth() {
        return this.deltaPlayerHealth;
    }

    public void setDeltaPlayerHealth(double deltaPlayerHealth) {
        this.deltaPlayerHealth = deltaPlayerHealth;
    }

    public double getDeltaEnemyHealth() {
        return this.deltaEnemyHealth;
    }

    public void setDeltaEnemyHealth(double deltaEnemyHealth) {
        this.deltaEnemyHealth = deltaEnemyHealth;
    }

    public List<Collectable> getWeaponryUsed() {
        return this.weaponryUsed;
    }

    public void setWeaponryUsed(List<Collectable> weaponryUsed) {
        this.weaponryUsed = weaponryUsed;
    }

    public RoundResponse toRoundResponse() {
        List<ItemResponse> ir = weaponryUsed.stream().map(item -> item.toItemResponse()).collect(Collectors.toList());
        RoundResponse rr = new RoundResponse(getDeltaPlayerHealth(), getDeltaEnemyHealth(), ir);
        return rr;
    }

}
