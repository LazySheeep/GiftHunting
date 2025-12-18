package io.lazysheeep.gifthunting.player;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.buffs.Buff;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.utils.MCUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.UUID;

public class GHPlayer
{
    private Player _hostPlayer;
    private final GameInstance _gameInstance;
    private int _score = 0;
    private final HashSet<Buff> _buffs = new HashSet<>();
    public int lastClickGiftTime = 0;

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

    public Location getLocation()
    {
        return _hostPlayer.getLocation();
    }

    public Location getBodyLocation()
    {
        return _hostPlayer.getLocation().add(_hostPlayer.getEyeLocation()).multiply(0.5f);
    }

    public GameInstance getGameInstance()
    {
        return _gameInstance;
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

    public boolean isValid()
    {
        return isConnected() && !isDestroyed();
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

    GHPlayer(@NotNull Player player, @NotNull GameInstance gameInstance)
    {
        _hostPlayer = player;
        _gameInstance = gameInstance;
    }

    public void addBuff(@NotNull Buff buff)
    {
        _buffs.add(buff);
        buff.onApply(this);
    }

    public boolean hasBuff(@NotNull Class<? extends Buff> buffClass)
    {
        for(Buff buff : _buffs)
        {
            if(buff.getClass() == buffClass)
            {
                return true;
            }
        }
        return false;
    }

    public void removeBuff(@NotNull Class<? extends Buff> buffClass)
    {
        for(Buff buff : _buffs)
        {
            if(buff.getClass() == buffClass)
            {
                buff.onRemove(this);
                _buffs.remove(buff);
                return;
            }
        }
    }

    public void reset()
    {
        _score = 0;
        lastClickGiftTime = 0;
    }

    void tick()
    {
        if(_hostPlayer == null || !_hostPlayer.isConnected())
        {
            return;
        }

        _hostPlayer.setSaturation(20.0f);
        _hostPlayer.setFoodLevel(20);

        // update buffs
        for(Buff buff : _buffs.stream().toList())
        {
            if(buff.getRemainingTime() <= 0)
            {
                buff.onRemove(this);
                _buffs.remove(buff);
            }
            else
            {
                buff.tick(this);
            }
        }

        // set display score
        GiftHunting.GetPlugin().getScoreObjective().getScore(_hostPlayer).setScore(_score);
    }
}
