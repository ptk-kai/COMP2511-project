package dungeonmania.Interfaces;

import dungeonmania.Entity.Entity;
import dungeonmania.Entity.MovingEntity.Battle;
import dungeonmania.Entity.MovingEntity.Mercenary;
import dungeonmania.Entity.StaticEntities.Door;

public interface ActivitySubscriber {
    public void notifySubscriberOfBattle(Battle battle);
    public void removeHistoricalEntities(Entity entity);
    public void removeEntityFromMap(Entity collectable);
    public void updateHistoricalEntityPosition(Entity entity);
    public void updateHistoricalMercenary(Mercenary entity);
    public void notifySubscriberOfTimeTravel(int numTicks);
    public void updateHistoricalDoorStatus(Door entity);
}
