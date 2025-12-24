package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.game.GameInstance;
import org.bukkit.Location;

public abstract class GHEntity
{
    protected boolean _isDestroyed = false;
    protected Location _location;

    protected GHEntity(Location location)
    {
        _location = location.clone();
    }

    public boolean isDestroyed()
    {
        return _isDestroyed;
    }

    public Location getLocation()
    {
        return _location;
    }

    public void onTick(GameInstance gameInstance)
    {

    }

    public void onDestroyed()
    {

    }
}
