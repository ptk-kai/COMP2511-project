package dungeonmania.Entity;

import dungeonmania.Entity.MovingEntity.*;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dungeonmania.Config.*;
import dungeonmania.Entity.Collectable.*;
import dungeonmania.Entity.StaticEntities.*;
import dungeonmania.util.Position;

public class EntityFactory {
    public static Entity createEntity(String entityType, Position position, 
        JsonObject jsonObject, Config config) throws IllegalArgumentException {
        
        JsonElement tmp;
        switch(entityType) {
            case DungeonMacro.WALL:
                return new Wall(position);
            case DungeonMacro.PLAYER:
                return new Player(position, config);
            case DungeonMacro.EXIT:
                return new Exit(position);
            case DungeonMacro.BOULDER:
                return new Boulder(position);
            case DungeonMacro.SWITCH:
                tmp = jsonObject.get(DungeonMacro.LOGIC);
                if (tmp != null) {
                    return new FloorSwitch(position, tmp.getAsString());
                } else {
                    return new FloorSwitch(position, DungeonMacro.OR);
                }
            case DungeonMacro.DOOR:
                return new Door(position, jsonObject.get(DungeonMacro.KEY).getAsInt());
            case DungeonMacro.PORTAL:
                return new Portal(position, jsonObject.get("colour").getAsString());
            case DungeonMacro.ZOMBIE_SPAWNER:
                return new ZombieToastSpawner(position, config.getZombieSpawnRate(), 
                    config.getZombieHealth(), config.getZombieAttack());
            case DungeonMacro.TREASURE: 
                return new Treasure(position);
            case DungeonMacro.KEY:
                return new Key(position, jsonObject.get(DungeonMacro.KEY).getAsInt());
            case DungeonMacro.INVINCIBILITY: 
                return new InvincibilityPotion(position, config.getInvincibilityPotionDuration());
            case DungeonMacro.INVISIBILITY: 
                return new InvisibilityPotion(position, config.getInvisibilityPotionDuration());
            case DungeonMacro.WOOD: 
                return new Wood(position);
            case DungeonMacro.ARROW: 
                return new Arrow(position);
            case DungeonMacro.BOMB:
                tmp = jsonObject.get(DungeonMacro.LOGIC);
                if (tmp != null) {
                    return new Bomb(position, config.getBombRadius(), tmp.getAsString());
                } else {
                    return new Bomb(position, config.getBombRadius(), "");
                }
            case DungeonMacro.SWORD:
                return new Sword(position, config.getSwordAttack(), config.getSwordDurability());
            case DungeonMacro.SHIELD:
                return new Shield(position, config.getShieldDefence(), config.getShieldDurability());
            case DungeonMacro.BOW:
                return new Bow(position, config.getBowDurability());
            case DungeonMacro.SPIDER:
                return new Spider(position, config.getSpiderAttack(), config.getSpiderHealth());
            case DungeonMacro.ZOMBIE:
                return new Zombie(position, config.getZombieAttack(), config.getZombieHealth());
            case DungeonMacro.MERCENARY:
                return new Mercenary(position, config.getMercenaryAttack(), config.getMercenaryHealth(), config.getBribeAmount(),
                    config.getAllyAttack(), config.getAllyDefence());
            case DungeonMacro.ASSASSIN:
                return new Assassin(position, config.getAssassinAttack(), config.getAssassinHealth(), config.getAssassinBribeAmount(),
                    config.getAssassinAttack(), config.getAllyDefence(), config.getAssassinBribeFailRate(), config.getAssassinReconRadius());
            case DungeonMacro.HYDRA:
                return new Hydra(position, config.getHydraATK(), config.getHydraHP(), 
                    config.getHydraHPRecoverChance(), config.getHydraHPRecoverAmount());
            case DungeonMacro.SUNSTONE:
                return new Sunstone(position);
            case DungeonMacro.TIME_TURNER:
                return new TimeTurner(position);
            case DungeonMacro.TIME_PORTAL:
                return new TimeTravellingPortal(position);
            case DungeonMacro.SCEPTRE:
                return new Sceptre(position, config.getMindControlDuration());
            case DungeonMacro.SWAMP:
                return new SwampTile(position, jsonObject.get(DungeonMacro.MOVE_FACTOR).getAsInt());
            case DungeonMacro.WIRE:
                return new Wire(position, DungeonMacro.OR);
            case DungeonMacro.SWITCH_DOOR:
                tmp = jsonObject.get(DungeonMacro.LOGIC);
                if (tmp != null) {
                    return new SwitchDoor(position, jsonObject.get(DungeonMacro.KEY).getAsInt(), tmp.getAsString());
                } else {
                    return new SwitchDoor(position, jsonObject.get(DungeonMacro.KEY).getAsInt(), DungeonMacro.OR);
                }
            case DungeonMacro.BULB_OFF:
                tmp = jsonObject.get(DungeonMacro.LOGIC);
                if (tmp != null) {
                    return new LightBulb(position, DungeonMacro.BULB_OFF, tmp.getAsString());
                } else {
                    return new LightBulb(position, DungeonMacro.BULB_OFF, DungeonMacro.OR);
                }
            case DungeonMacro.BULB_ON:
                tmp = jsonObject.get(DungeonMacro.LOGIC);
                if (tmp != null) {
                    return new LightBulb(position, DungeonMacro.BULB_ON, tmp.getAsString());
                } else {
                    return new LightBulb(position, DungeonMacro.BULB_ON, DungeonMacro.OR);
                }  
            default:
				throw new IllegalArgumentException("Invalid type of entity");
        }
    }
}
