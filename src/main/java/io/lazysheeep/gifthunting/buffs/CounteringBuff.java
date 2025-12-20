package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CounteringBuff extends Buff
{
    public CounteringBuff(int duration)
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
        if(otherBuff instanceof CounteringBuff)
        {
            this.remainingTime = Math.max(this.remainingTime, otherBuff.getRemainingTime());
            return true;
        }
        return false;
    }

    @Override
    public net.kyori.adventure.text.TextComponent getDisplayName()
    {
        return Component.text("识破", NamedTextColor.AQUA);
    }
}
