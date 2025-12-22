package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.lazuliui.LazuliUI;
import io.lazysheeep.lazuliui.Message;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.jetbrains.annotations.Nullable;

public class ScoreOrb extends Orb
{
    private final int _scoreValue;

    public ScoreOrb(Location location, @Nullable GHPlayer source, GHPlayer target, int scoreValue)
    {
        super(location, source, target);
        _scoreValue = scoreValue;
    }

    @Override
    protected void onCollected()
    {
        if(_target != null)
        {
            LazuliUI.flush(_target.getPlayer(), Message.Type.ACTIONBAR_SUFFIX);
            _target.getPlayer().playSound(_target.getPlayer(), Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER, 1.0f, 1.0f);
            _target.addScore(_scoreValue);
            if(_source != null && _source != _target)
            {
                var name = _source.getPlayer().getName();
                LazuliUI.sendMessage(_target.getPlayer(), MessageFactory.getCapturedScoreFromOtherMsg(name, _scoreValue));
            }
        }
    }

    @Override
    protected void onTick()
    {
        _location.getWorld().spawnParticle(Particle.DUST, _location, 2, new Particle.DustOptions(Color.LIME, 1.0f));
    }
}
