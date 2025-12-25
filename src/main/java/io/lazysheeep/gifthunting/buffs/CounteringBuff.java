package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class CounteringBuff extends Buff
{
    public CounteringBuff()
    {
        super();
    }

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
    public net.kyori.adventure.text.TextComponent getDisplayName()
    {
        return Component.text("识破", NamedTextColor.GREEN);
    }

    @Override
    public String getDescription()
    {
        return "识破一次技能, 成功后移除该Buff";
    }
}
