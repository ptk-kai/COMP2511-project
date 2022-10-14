package dungeonmania;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import dungeonmania.Config.*;
import dungeonmania.Entity.*;
import dungeonmania.Entity.Collectable.*;

public class Inventory implements Serializable {
    private Player player;
    private List<Collectable> items = new ArrayList<>();
    private Config config;
    private int treasurePicked = 0;

    public Inventory(Player currPlayer, Config config) {
        this.player = currPlayer;
        this.config = config;
    }

    public List<Collectable> getInventory() {
        return this.items;
    }

    public Config getConfig() {
        return this.config;
    }

    public boolean pickUpItem(Collectable item) {
        // Player can't hold two keys at same time
        if (item instanceof Key && this.checkHasKey()) {
            return false;
        }
        if (item instanceof Treasure || item instanceof Sunstone) {
            this.treasurePicked += 1;
        }
        item.setOwner(player);
        this.items.add(item);
        return true;
    }

    public void useTreasure(int amount) {
        for (int i = 0; i < amount; i++) {
            getFirstItemInType(DungeonMacro.TREASURE).useItem();
        }
    }
    
    public boolean checkHasSceptre() {
        return this.items.stream()
            .anyMatch(item -> item instanceof Sceptre);
    }

    private boolean checkHasKey() {
        return this.items.stream()
            .anyMatch(item -> item instanceof Key);
    }

    
    public boolean checkHasItem(String itemType) {
        return this.items.stream()
            .anyMatch(item -> item.getType().equals(itemType));
    }

    // Don't use this for calculate current treasure number!!
    public int getNumTreasurePicked() {
        return this.treasurePicked;
    }

    public void removeItem(Collectable item) {
        this.items.remove(item);
    }

    public int checkNumberOfItem(String itemType) {
        return this.items
            .stream()
            .filter(item -> item.getType().equals(itemType))
            .collect(Collectors.toList())
            .size();
    }

    public Collectable getFirstItemInType(String itemType) {
        return this.items
            .stream()
            .filter(item -> item.getType().equals(itemType))
            .findFirst()
            .orElse(null);
    }

    private void useItemForCraft(String itemType, int amount) {
        for (int i = 0; i < amount; i++) {
            getFirstItemInType(itemType).useItem();
        }
    }

    public boolean tryCraftItem(String itemType) {
        switch (itemType) {
            case DungeonMacro.BOW:
                if (!checkCraftBow()) {
                    return false;
                }

                useItemForCraft(DungeonMacro.WOOD, 1);
                useItemForCraft(DungeonMacro.ARROW, 3);
                pickUpItem(new Bow(null, config.getBowDurability()));
                return true;
            
            case DungeonMacro.SHIELD:
                if (!checkCraftShield()) {
                    return false;
                }

                useItemForCraft(DungeonMacro.WOOD, 2);
                if (checkNumberOfItem(DungeonMacro.TREASURE) >= 1) {
                    useItemForCraft(DungeonMacro.TREASURE, 1);
                } else {
                    useItemForCraft(DungeonMacro.KEY, 1);
                }
                pickUpItem(new Shield(null, config.getShieldDefence(), config.getShieldDurability()));
                return true;
            
            case DungeonMacro.SCEPTRE:
                if (!checkCraftSceptre()) {
                    return false;
                }
                
                useItemForCraft(DungeonMacro.SUNSTONE, 1);
                
                if (checkNumberOfItem(DungeonMacro.WOOD) >= 1) {
                    useItemForCraft(DungeonMacro.WOOD, 1);
                } else {
                    useItemForCraft(DungeonMacro.ARROW, 2);
                }

                if (checkNumberOfItem(DungeonMacro.TREASURE) >= 1) {
                    useItemForCraft(DungeonMacro.TREASURE, 1);
                } else if (checkNumberOfItem(DungeonMacro.KEY) >= 1) {
                    useItemForCraft(DungeonMacro.KEY, 1);
                }
                pickUpItem(new Sceptre(null, config.getMindControlDuration()));
                return true;

            case DungeonMacro.MIDNIGHT:
                if (!checkCraftMidNightArmour()) {
                    return false;
                }

                useItemForCraft(DungeonMacro.SUNSTONE, 1);
                useItemForCraft(DungeonMacro.SWORD, 1);
                pickUpItem(new MidnightArmour(null, config.getMidNightArmourtATK(), 
                    config.getgetMidNightArmoutDEF()));
                return true;
            
            default:
                return true;
        }
    }

    public boolean checkCraftBow() {
        return (checkNumberOfItem(DungeonMacro.WOOD) >= 1 && checkNumberOfItem(DungeonMacro.ARROW) >= 3);
    }

    public boolean checkCraftShield() {
        return (checkNumberOfItem(DungeonMacro.WOOD) >= 2 &&
                    (checkNumberOfItem(DungeonMacro.TREASURE) >= 1 || checkNumberOfItem(DungeonMacro.KEY) >= 1));
    }

    public boolean checkCraftSceptre() {
        return ((checkNumberOfItem(DungeonMacro.WOOD) >= 1 || checkNumberOfItem(DungeonMacro.ARROW) >= 2)
            && (checkNumberOfItem(DungeonMacro.KEY) >= 1 || checkNumberOfItem(DungeonMacro.TREASURE) >= 1
            || checkNumberOfItem(DungeonMacro.SUNSTONE) >= 2)
            && (checkNumberOfItem(DungeonMacro.SUNSTONE) >= 1));
    }

    public boolean checkCraftMidNightArmour() {
        return (checkNumberOfItem(DungeonMacro.SWORD) >= 1 && checkNumberOfItem(DungeonMacro.SUNSTONE) >= 1);
    }


}
