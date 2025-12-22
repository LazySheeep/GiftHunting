package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.player.GHPlayer;

import java.util.HashSet;

public class OrbManager
{
    private final HashSet<Orb> _orbs = new HashSet<>();

    public void addOrb(Orb orb)
    {
        _orbs.add(orb);
    }

    public void removeOrb(Orb orb)
    {
        _orbs.remove(orb);
    }

    public void tick(GameInstance gameInstance)
    {
        _orbs.removeIf(Orb::isCollected);

        for(Orb orb : _orbs)
        {
            orb.tick(gameInstance);
        }
    }

    public void retargetOrbsNear(GHPlayer player, double radius)
    {
        var loc = player.getBodyLocation();
        double r2 = radius * radius;
        for(Orb orb : _orbs)
        {
            if(orb.isCollected()) continue;
            if(orb._location.getWorld() != loc.getWorld()) continue;
            if(orb._location.distanceSquared(loc) <= r2)
            {
                orb.setTarget(player);
            }
        }
    }

    public java.util.List<Orb> getOrbsSnapshot()
    {
        return new java.util.ArrayList<>(_orbs);
    }
}
