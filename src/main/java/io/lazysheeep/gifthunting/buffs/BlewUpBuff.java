package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Particle;

public class BlewUpBuff extends Buff
{
    public BlewUpBuff()
    {
        super();
    }

    public BlewUpBuff(int duration)
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
        ghPlayer.getPlayer().getWorld().spawnParticle(Particle.DUST,ghPlayer.getPlayer().getLocation(),5, 0.2, 0.2, 0.2, new Particle.DustOptions(Color.WHITE, 2.0f));
    }

    @Override
    public TextComponent getDisplayName()
    {
        return Component.text("击飞", NamedTextColor.YELLOW);
    }

    @Override
    public String getDescription()
    {
        return "只是在脚底产生烟雾粒子";
    }
}
