package io.lazysheeep.gifthunting.player;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GHPlayer
{
    private Player _hostPlayer;

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

    private int _score = 0;

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

    }
}
