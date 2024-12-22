package io.lazysheeep.gifthunting.player;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.game.GameState;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.utils.MCUtil;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class GHPlayer
{
    private Player _hostPlayer;
    private int _score = 0;
    public int lastClickGiftTime = 0;
    public boolean isDisciple = false;
    public int silenceTimer = 0;
    public int reflectTimer = 0;

    public @NotNull Player getPlayer()
    {
        return _hostPlayer;
    }

    public void destroy()
    {
        _hostPlayer = null;
    }

    public boolean isValid()
    {
        return _hostPlayer != null;
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

    void tick()
    {
        if(GiftHunting.GetPlugin().getGameManager().getState() == GameState.IDLE)
        {
            return;
        }

        // disciple effect
        if(isDisciple && GiftHunting.GetPlugin().getGameManager().getMainTimer() % 4 == 0)
        {
            Gift specialGift = GiftHunting.GetPlugin().getGiftManager().getSpecialGift();
            if(specialGift != null)
            {
                MCUtil.SpawnDustLineParticle(_hostPlayer.getLocation().add(0.0, 1.3, 0.0), specialGift.getLocation().add(0.0, -0.3, 0.0), 0.5f, Color.ORANGE, 1.0f);
                _hostPlayer.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 40, 1));
            }
            else
            {
                isDisciple = false;
            }
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

        // set display score
        GiftHunting.GetPlugin().getScoreObjective().getScore(_hostPlayer).setScore(_score);
    }
}
