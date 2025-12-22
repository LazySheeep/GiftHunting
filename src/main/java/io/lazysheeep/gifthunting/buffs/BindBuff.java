package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import java.util.List;

public class BindBuff extends Buff
{
    private Location lockedLocation;
    private double _t;

    public BindBuff(int duration)
    {
        super(duration);
    }

    @Override
    public void onApply(GHPlayer player)
    {
        Player p = player.getPlayer();
        lockedLocation = p.getLocation();
        _t = 0.0;
    }

    @Override
    public void onRemove(GHPlayer player)
    {
    }

    @Override
    protected void onTick(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        Location currentLocation = player.getLocation();
        World world = currentLocation.getWorld();
        Location targetLocation = lockedLocation.clone();
        targetLocation.setYaw(currentLocation.getYaw());
        targetLocation.setPitch(currentLocation.getPitch());
        player.teleport(targetLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
        player.setVelocity(player.getVelocity().zero());
        // particles
        Vector center = ghPlayer.getBodyLocation().toVector();
        Vector fwd = currentLocation.getDirection();
        fwd.setY(0.0).normalize();
        if(fwd.lengthSquared() == 0.0) fwd = new Vector(0.0, 0.0, 1.0);
        Vector right = new Vector(fwd.getZ(), 0.0, -fwd.getX()).normalize();
        double alpha = Math.PI / 6;
        Vector a1 = right.clone().rotateAroundAxis(fwd, alpha).normalize().multiply(0.8);
        Vector a2 = right.clone().rotateAroundAxis(fwd, -alpha).normalize().multiply(0.8);
        Vector b = fwd.clone().multiply(0.5);
        List<Vector> particlePositions = List.of(ellipse(center, a1, b, _t), ellipse(center, a1, b, _t + Math.PI), ellipse(center, a2, b, _t), ellipse(center, a2, b, _t + Math.PI));
        Particle.DustOptions dustOption = new Particle.DustOptions(Color.fromRGB(255, 32, 32), 1.0f);
        for (Vector p : particlePositions)
        {
            world.spawnParticle(Particle.DUST, new Location(world, p.getX(), p.getY(), p.getZ()), 1, 0, 0, 0, 0, dustOption);
        }
        _t += Math.PI * 0.15;
    }

    @Override
    public net.kyori.adventure.text.TextComponent getDisplayName()
    {
        return Component.text("束缚", NamedTextColor.RED);
    }

    private static Vector ellipse(Vector o, Vector a, Vector b, double t)
    {
        return o.clone().add(a.clone().multiply(Math.cos(t))).add(b.clone().multiply(Math.sin(t)));
    }
}
