package io.lazysheeep.gifthunting.orbs;

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

    public void tick()
    {
        for(Orb orb : _orbs)
        {
            orb.tick();
        }
    }
}
