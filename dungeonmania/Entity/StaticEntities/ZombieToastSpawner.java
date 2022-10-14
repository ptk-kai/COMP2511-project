package dungeonmania.Entity.StaticEntities;

import java.util.*;
import java.util.stream.Collectors;
import dungeonmania.Config.DungeonMacro;
import dungeonmania.Entity.Entity;
import dungeonmania.Entity.Player;
import dungeonmania.Entity.MovingEntity.Zombie;
import dungeonmania.Interfaces.*;
import dungeonmania.exceptions.InvalidActionException;
import dungeonmania.map.MapHelper;
import dungeonmania.util.Position;

public class ZombieToastSpawner extends StaticEntity implements Tick, Interact{
    private int spanwRate;
    private int tickCount = 0;
    private double zombieHealth;
    private double zombieAttack;
    List<Entity> spawnedZombies = new ArrayList<>();

    public ZombieToastSpawner(Position position, int spanwRate, int health, int attack) {
        super(position, DungeonMacro.ZOMBIE_SPAWNER, true);
        this.spanwRate = spanwRate;
        this.zombieHealth = health;
        this.zombieAttack = attack;
    }

    @Override
    public boolean checkPosBlocked(List<Entity> entities) {
        return entities.stream()
            .anyMatch(entity -> 
            entity instanceof Wall
            || entity instanceof Boulder
            || entity instanceof Door
            || entity instanceof Portal
            || entity instanceof ZombieToastSpawner);
    }

    @Override
    public void tick(Map<Position, List<Entity>> entityMap, Player player) {
        this.tickCount += 1;
        if (spanwRate != 0 && tickCount % spanwRate == 0) {
            spawnZombie(entityMap);
        }
    }

    private void spawnZombie(Map<Position, List<Entity>> entityMap) {

        List<Position> possiblePos = this.getPosition().getCardinallyAdjacentPositions();
        List<Position> ableToSpawnPos = possiblePos.stream()
            .filter(pos -> !checkPosBlocked(entityMap.getOrDefault(pos, new ArrayList<>())))
            .collect(Collectors.toList());

        if (ableToSpawnPos.isEmpty()) {
            return;
        }
        Random rand = new Random();
        Position randomPos = ableToSpawnPos.get(rand.nextInt(ableToSpawnPos.size()));
        Zombie zombie = new Zombie(randomPos, this.zombieHealth, this.zombieAttack);
        zombie.setSpawnerId(this.getId());
        MapHelper.mapAddEntity(entityMap, randomPos, zombie);
        spawnedZombies.add(zombie);
    }

    // Player can only be same pos as Zombie Spawner (destory spawner) if they have weapon (sword or bow)
    // destory do not count as battle nor reduce weapon durability
    public void destroy(Map<Position, List<Entity>> entityMap) {
		MapHelper.rmGivenEntity(entityMap, this.position, this);
	}

    @Override
    public void interact(Player player, Map<Position, List<Entity>> entityMap, ActivitySubscriber subscriber) throws InvalidActionException {
        if (!Position.isAdjacent(this.getPosition(), player.getPosition()) ||
            !player.checkDestroySpawner()) {
            throw new InvalidActionException("You can't destroy ZombieSpawner currently");
        }
        this.destroy(entityMap);

        if (this.getId().contains("past")) {
            subscriber.removeHistoricalEntities(this);
            subscriber.removeEntityFromMap(this);
            spawnedZombies.forEach(zombie -> subscriber.removeEntityFromMap(zombie));
        }
    }
}
