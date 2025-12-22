package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.gifthunting.utils.RandUtil;
import io.lazysheeep.lazuliui.LazuliUI;
import io.lazysheeep.lazuliui.Message;
import org.bukkit.*;
import org.bukkit.util.Vector;

public class ScoreOrb extends Orb
{
    private final int _scoreValue;
    private final GHPlayer _target;

    private Location _location;
    private Vector _velocity;
    private int _timer = -1;
    private boolean _collected = false;

    private final int _startDelay = 10;
    private final float _force = 24.0f;
    private final float _friction = 1.5f;

    public boolean isCollected()
    {
        return _collected;
    }

    public ScoreOrb(int scoreValue, GHPlayer target, Location location)
    {
        _scoreValue = scoreValue;
        _target = target;
        _location = location.clone();
        _velocity = new Vector(RandUtil.nextFloat(-3.0f, 3.0f), RandUtil.nextFloat(2.0f, 4.0f), RandUtil.nextFloat(-3.0f, 3.0f));
    }

    @Override
    public void tick()
    {
        if(!_target.isValid() || _target.getLocation().getWorld() != _location.getWorld())
            _collected = true;

        if(_collected)
            return;

        _timer ++;

        float deltaTime = MCUtil.GetServerTickDeltaTime();

        if(_timer >= _startDelay)
        {
            // collection check
            if(_target.getBodyLocation().distance(_location) <= 0.6f)
            {
                _collected = true;
                // send actionbar suffix list:
                // score: value +increment
                LazuliUI.flush(_target.getPlayer(), Message.Type.ACTIONBAR_SUFFIX);
                _target.getPlayer().playSound(_target.getPlayer(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER, 1.0f, 1.0f);
                _target.addScore(_scoreValue);
                return;
            }

            Vector direction = _target.getBodyLocation().toVector().subtract(_location.toVector());
            direction.normalize();
            _velocity.add(direction.multiply(_force * deltaTime));
        }

        // update location
        _location.add(_velocity.clone().multiply(deltaTime));

        // apply friction
        _velocity.multiply(Math.exp(-_friction * deltaTime));

        // particle effect
        _location.getWorld().spawnParticle(Particle.DUST, _location, 2, new Particle.DustOptions(Color.LIME, 1.0f));
    }
}
