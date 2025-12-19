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

    public abstract void onApply(GHPlayer ghPlayer);

    public abstract void onRemove(GHPlayer ghPlayer);

    protected abstract void onTick(GHPlayer ghPlayer);

    public void tick(GHPlayer ghPlayer)
    {
        if (remainingTime > 0)
        {
            onTick(ghPlayer);
            remainingTime--;
        }
    }
}
