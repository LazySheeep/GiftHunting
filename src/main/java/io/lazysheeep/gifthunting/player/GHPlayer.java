package io.lazysheeep.gifthunting.player;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.utils.MCUtil;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class GHPlayer
{
    private Player _hostPlayer;
    private int _score = 0;
    public int lastClickGiftTime = 0;
    public boolean isDisciple = false;
    public int silenceTimer = 0;
    public int reflectTimer = 0;
    public int revolutionTimer = 0;
    public @Nullable GHPlayer revolutionTarget = null;
    public int speedUpTimer = 0;

    public @NotNull Player getPlayer()
    {
        return _hostPlayer;
    }

    public void reconnect(@NotNull Player player)
    {
        _hostPlayer = player;
    }

    public UUID getUUID()
    {
        return _hostPlayer.getUniqueId();
    }

    public void destroy()
    {
        _hostPlayer = null;
    }

    public boolean isConnected()
    {
        return _hostPlayer != null && _hostPlayer.isConnected();
    }

    public boolean isDestroyed()
    {
        return _hostPlayer == null;
    }

    public int getScore()
    {
        return _score;
    }

    public void addScore(int score)
    {
        this._score += score;
    }

    public void setScore(int score)
    {
        this._score = score;
    }

    GHPlayer(@NotNull Player player)
    {
        _hostPlayer = player;
    }

    public void reset()
    {
        _score = 0;
        lastClickGiftTime = 0;
        isDisciple = false;
        silenceTimer = 0;
        reflectTimer = 0;
        revolutionTimer = 0;
        revolutionTarget = null;
        speedUpTimer = 0;
    }

    void tick()
    {
        if(_hostPlayer == null || !_hostPlayer.isConnected())
        {
            return;
        }

        _hostPlayer.setSaturation(20.0f);

        // disciple effect
        if(isDisciple)
        {
            Gift specialGift = GiftHunting.GetPlugin().getGameInstance().getGiftManager().getSpecialGift();
            if(specialGift != null)
            {
                MCUtil.SpawnDustLineParticle(_hostPlayer.getLocation().add(0.0, 1.3, 0.0), specialGift.getLocation().add(0.0, -0.3, 0.0), 0.5f, Color.GREEN, 1.0f);
                _hostPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0));
            }
            else
            {
                isDisciple = false;
            }
        }

        // revolution effect
        if(revolutionTarget != null && revolutionTimer > 0 && revolutionTimer % 4 == 0)
        {
            revolutionTarget.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 10, 0));
            revolutionTarget.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 10, 0));
            MCUtil.SpawnDustLineParticle(_hostPlayer.getLocation().add(0.0, 1.3, 0.0), revolutionTarget.getPlayer().getLocation().add(0.0, 1.3, 0.0), 0.5f, Color.RED, 1.0f);
        }

        // speed up effect
        if(speedUpTimer > 0 && speedUpTimer % 4 == 0)
        {
            _hostPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 0));
        }

        // update timer
        if(silenceTimer > 0)
        {
            silenceTimer--;
        }
        if(reflectTimer > 0)
        {
            reflectTimer--;
        }
        if(revolutionTimer > 0)
        {
            revolutionTimer--;
            if(revolutionTimer == 0)
            {
                revolutionTarget = null;
            }
        }
        if(speedUpTimer > 0)
        {
            speedUpTimer--;
        }

        // set display score
        GiftHunting.GetPlugin().getScoreObjective().getScore(_hostPlayer).setScore(_score);
    }
}
