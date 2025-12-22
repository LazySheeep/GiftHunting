package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.gifthunting.utils.RandUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public abstract class Orb
{
    protected GHPlayer _target;
    protected Location _location;
    protected Vector _velocity;
    protected int _timer = -1;
    protected boolean _collected = false;
    protected GHPlayer _forbidden;

    protected final int _startDelay = 10;
    protected final float _force = 24.0f;
    protected final float _friction = 1.5f;
    protected final int _maxLifeTime = 1200;
    protected final float _captureDistance = 3.0f;
    protected final float _collectDistance = 0.6f;

    protected Orb(Location location, GHPlayer target)
    {
        _target = target;
        _location = location.clone();
        _velocity = new Vector(RandUtil.nextFloat(-3.0f, 3.0f), RandUtil.nextFloat(2.0f, 4.0f), RandUtil.nextFloat(-3.0f, 3.0f));
    }

    public boolean isCollected()
    {
        return _collected;
    }

    public Orb forbid(GHPlayer gh)
    {
        _forbidden = gh;
        return this;
    }

    protected abstract void onCollected();

    protected abstract void onTick();

    protected boolean canCapture(GHPlayer gh)
    {
        if(_forbidden != null && gh == _forbidden) return false;
        return true;
    }

    public void tick()
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
                        GHPlayer gh = GiftHunting.GetPlugin().getGameInstance().getPlayerManager().getGHPlayer(p);
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
                if(canCapture(_target) && _target.getBodyLocation().distance(_location) <= _collectDistance)
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
}
