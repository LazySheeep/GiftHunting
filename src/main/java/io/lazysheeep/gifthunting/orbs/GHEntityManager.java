package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.game.GameInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class GHEntityManager
{
    private final HashSet<GHEntity> _entities = new HashSet<>();

    public void addEntity(GHEntity entity)
    {
        _entities.add(entity);
    }

    public void removeEntity(GHEntity entity)
    {
        _entities.remove(entity);
    }

    public void tick(GameInstance gameInstance)
    {
        List<GHEntity> snapshot = new ArrayList<>(_entities);
        for(GHEntity entity : snapshot)
        {
            if(!entity.isDestroyed())
            {
                entity.onTick(gameInstance);
            }
            if(entity.isDestroyed())
            {
                entity.onDestroyed();
            }
        }

        _entities.removeIf(GHEntity::isDestroyed);
    }

    public List<Orb> getOrbs()
    {
        List<Orb> result = new ArrayList<>();
        for(GHEntity entity : _entities)
        {
            if(entity instanceof Orb orb)
            {
                result.add(orb);
            }
        }
        return result;
    }
}
