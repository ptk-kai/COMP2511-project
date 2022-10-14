package dungeonmania.Entity.Collectable;

import dungeonmania.util.Position;

public abstract class Weapon extends Collectable {
    
    private double durability;
    private double atkModifier;
    private double defModifier;
    
    public Weapon(Position position, String type, boolean isInteractable, 
        double atkModifier, double defModifier, double durability) {
        
        super(position, type, false);
        this.atkModifier = atkModifier;
        this.defModifier = defModifier;
        this.durability = durability;
        //waiting battle part
    }

    public double getATKModifier() {
        return this.atkModifier;
    }

    public double getDEFModifier() {
        return this.defModifier;
    }

    public double getDurability() {
        return this.durability;
    }

    public void setDurability(double durability) {
        this.durability = durability;
    }

    public void useInBattle() {
        this.setDurability(this.getDurability() - 1);
        if (this.durability <= 0) {
            this.useItem();
        }
    }

    // Consumed means used for crafting
    @Override
    public void useItem() {
        this.getOwner().consumeItem(this);
    }
}
