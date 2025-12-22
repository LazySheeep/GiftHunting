package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.gifthunting.utils.RandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public abstract class Orb
{
    protected GHPlayer _target;
    protected final @Nullable GHPlayer _source;
    protected Location _location;
    protected Vector _velocity;
    protected int _timer = -1;
    protected boolean _collected = false;

    protected final int _startDelay = 10;
    protected final float _force = 24.0f;
    protected final float _friction = 1.5f;
    protected final int _maxLifeTime = 1200;
    protected final float _captureDistance = 3.0f;
    protected final float _collectDistance = 0.6f;

    protected Orb(Location location, @Nullable GHPlayer source, GHPlayer target)
    {
        _location = location.clone();
        _target = target;
        _source = source;
        _velocity = new Vector(RandUtil.nextFloat(-3.0f, 3.0f), RandUtil.nextFloat(2.0f, 4.0f), RandUtil.nextFloat(-3.0f, 3.0f));
    }

    public boolean isCollected()
    {
        return _collected;
    }

    protected abstract void onCollected();

    protected abstract void onTick();

    protected boolean canCapture(GHPlayer gh)
    {
        if(_source != null && gh == _source) return false;
        return true;
    }

    public void tick(GameInstance gameInstance)
    {
        if(_target != null && (!_target.isValid() || _target.getLocation().getWorld() != _location.getWorld()))
            _collected = true;

        if(_collected)
            return;

        _timer ++;

        if(_timer >= _maxLifeTime)
        {
            _collected = true;
            return;
        }

        float deltaTime = MCUtil.GetServerTickDeltaTime();

        if(_timer >= _startDelay)
        {
            if(_target == null)
            {
                GHPlayer best = null;
                double bestDist2 = Double.MAX_VALUE;
                for(org.bukkit.entity.Player p : Bukkit.getOnlinePlayers())
                {
                    if(p.getWorld() != _location.getWorld()) continue;
                    double d2 = p.getLocation().toVector().distanceSquared(_location.toVector());
                    if(d2 <= _captureDistance * _captureDistance)
                    {
                        GHPlayer gh = gameInstance.getPlayerManager().getGHPlayer(p);
                        if(gh != null && gh.isValid() && canCapture(gh))
                        {
                            if(d2 < bestDist2)
                            {
                                best = gh;
                                bestDist2 = d2;
                            }
                        }
                    }
                }
                _target = best;
            }

            if(_target != null)
            {
                if(_target.getBodyLocation().distance(_location) <= _collectDistance)
                {
                    _collected = true;
                    onCollected();
                    return;
                }

                Vector direction = _target.getBodyLocation().toVector().subtract(_location.toVector());
                direction.normalize();
                _velocity.add(direction.multiply(_force * deltaTime));
            }
        }

        _location.add(_velocity.clone().multiply(deltaTime));
        _velocity.multiply(Math.exp(-_friction * deltaTime));

        onTick();
    }

    public void setTarget(io.lazysheeep.gifthunting.player.GHPlayer target)
    {
        this._target = target;
    }

    public org.bukkit.Location getLocation()
    {
        return _location;
    }
}
