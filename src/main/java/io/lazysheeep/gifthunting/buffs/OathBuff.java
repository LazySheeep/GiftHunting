package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class OathBuff extends Buff
{
    public OathBuff(int duration)
    {
        super(duration);
    }

    @Override
    public void onApply(GHPlayer ghPlayer)
    {
    }

    @Override
    public void onRemove(GHPlayer ghPlayer)
    {
    }

    @Override
    protected void onTick(GHPlayer ghPlayer)
    {
    }

    @Override
    public TextComponent getDisplayName()
    {
        return Component.text("誓约", NamedTextColor.LIGHT_PURPLE);
    }
}
