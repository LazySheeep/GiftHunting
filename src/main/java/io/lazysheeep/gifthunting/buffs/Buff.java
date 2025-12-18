package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;

public abstract class Buff
{
    protected int remainingTime;

    public int getRemainingTime()
    {
        return remainingTime;
    }

    protected Buff(int duration)
    {
        this.remainingTime = duration;
    }

    public abstract void onApply(GHPlayer player);

    public abstract void onRemove(GHPlayer player);

    protected abstract void onTick(GHPlayer player);

    public void tick(GHPlayer player)
    {
        if (remainingTime > 0)
        {
            onTick(player);
            remainingTime--;
        }
    }
}
