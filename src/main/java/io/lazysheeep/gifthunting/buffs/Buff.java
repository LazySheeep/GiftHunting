package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;

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

    public TextComponent getDisplayName()
    {
        return Component.text(this.getClass().getSimpleName());
    }

    public abstract void onApply(GHPlayer ghPlayer);

    public abstract void onRemove(GHPlayer ghPlayer);

    protected abstract void onTick(GHPlayer ghPlayer);

    public abstract boolean tryMerge(Buff otherBuff);

    public void tick(GHPlayer ghPlayer)
    {
        if (remainingTime > 0)
        {
            remainingTime--;
            onTick(ghPlayer);
        }
    }
}
