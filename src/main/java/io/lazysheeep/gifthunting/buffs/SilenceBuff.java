package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class SilenceBuff extends Buff
{
    public SilenceBuff(int duration)
    {
        super(duration);
    }

    @Override
    public void onApply(GHPlayer player)
    {

    }

    @Override
    public void onRemove(GHPlayer player)
    {

    }

    @Override
    protected void onTick(GHPlayer player)
    {

    }

    @Override
    public boolean tryMerge(Buff otherBuff)
    {
        if(otherBuff instanceof SilenceBuff)
        {
            this.remainingTime = Math.max(this.remainingTime, otherBuff.getRemainingTime());
            return true;
        }
        return false;
    }

    @Override
    public net.kyori.adventure.text.TextComponent getDisplayName()
    {
        return Component.text("沉默", NamedTextColor.DARK_GRAY);
    }
}
