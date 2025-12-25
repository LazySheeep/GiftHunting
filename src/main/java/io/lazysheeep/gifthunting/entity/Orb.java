package io.lazysheeep.gifthunting.entity;

import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.gifthunting.utils.RandUtil;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public abstract class Orb extends GHEntity
{
    protected GHPlayer _target;
    protected final @Nullable GHPlayer _source;
    protected Vector _velocity;
    protected int _timer = -1;

    protected final int _startDelay = 10;
    protected final float _force = 24.0f;
    protected final float _friction = 1.5f;
    protected final int _maxLifeTime = 1200;
    protected final float _captureDistance = 4.0f;
    protected final float _collectDistance = 0.6f;

    protected Orb(Location location, @Nullable GHPlayer source, GHPlayer target)
    {
        super(location);
        _target = target;
        _source = source;
        _velocity = new Vector(RandUtil.nextFloat(-3.0f, 3.0f), RandUtil.nextFloat(2.0f, 4.0f), RandUtil.nextFloat(-3.0f, 3.0f));
    }

    @Override
    public void onTick(GameInstance gameInstance)
    {
        super.onTick(gameInstance);

        if(_target != null && (!_target.isValid() || _target.getLocation().getWorld() != _location.getWorld()))
            _isDestroyed = true;

        if(_isDestroyed)
            return;

        _timer ++;

        if(_timer >= _maxLifeTime)
        {
            _isDestroyed = true;
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
                setTarget(best);
            }

            if(_target != null)
            {
                if(_target.getBodyLocation().distance(_location) <= _collectDistance)
                {
                    onCollected();
                    _isDestroyed = true;
                    return;
                }

                Vector targetDirection = _target.getBodyLocation().toVector().subtract(_location.toVector()).normalize();
                Vector desiredVelocity = targetDirection.clone().multiply(_force / _friction);
                Vector forceDirection = desiredVelocity.subtract(_velocity).normalize();
                _velocity.add(forceDirection.multiply(_force * deltaTime));
            }
        }

        _location.add(_velocity.clone().multiply(deltaTime));
        _velocity.multiply(Math.exp(-_friction * deltaTime));
    }

    public void setTarget(GHPlayer newTarget)
    {
        if(_target == newTarget) return;
        if(_target != null && newTarget != null)
        {
            LazuliUI.sendMessage(_target.getPlayer(), MessageFactory.getOrbCapturedByOthersMsg(newTarget));
        }
        this._target = newTarget;
    }

    protected boolean canCapture(GHPlayer gh)
    {
        return _source == null || gh != _source;
    }

    protected abstract void onCollected();
}
