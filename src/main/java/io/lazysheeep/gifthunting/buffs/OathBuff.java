package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MathUtils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

public class OathBuff extends Buff
{
    private double _t;

    public OathBuff(int duration)
    {
        super(duration);
    }

    @Override
    public void onApply(GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().getWorld().playSound(ghPlayer.getPlayer(), Sound.BLOCK_BEACON_POWER_SELECT, SoundCategory.MASTER, 1.0f, 1.0f);
    }

    @Override
    public void onRemove(GHPlayer ghPlayer)
    {
    }

    @Override
    protected void onTick(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        World world = player.getWorld();
        Vector center = ghPlayer.getBodyLocation().toVector();
        double radius = 1.0;
        Vector a = new Vector(radius, 0, 0);
        Vector b = new Vector(0, 0, radius);
        List<Vector> particlePositions = List.of(MathUtils.Ellipse(center, a, b, _t), MathUtils.Ellipse(center, a, b, _t + Math.PI));
        for (Vector p : particlePositions)
        {
            world.spawnParticle(Particle.DUST, new Location(world, p.getX(), p.getY(), p.getZ()), 2, 0, 0, 0, 0, new Particle.DustOptions(Color.AQUA, 1.0f));
        }
        _t += Math.PI * 0.15;
    }

    @Override
    public TextComponent getDisplayName()
    {
        return Component.text("誓约", NamedTextColor.LIGHT_PURPLE);
    }
}
