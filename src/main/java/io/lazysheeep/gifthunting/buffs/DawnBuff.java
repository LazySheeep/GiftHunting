package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class DawnBuff extends Buff
{
    public DawnBuff(int duration)
    {
        super(duration);
    }

    @Override
    public void onApply(GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().setGlowing(true);
    }

    @Override
    public void onRemove(GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().setGlowing(false);
    }

    @Override
    protected void onTick(GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().setGlowing(true);
    }

    @Override
    public TextComponent getDisplayName()
    {
        return Component.text("决胜", NamedTextColor.LIGHT_PURPLE);
    }
}
