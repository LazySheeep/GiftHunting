package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;

public class ItemOrb extends Orb
{
    private ItemStack _item;

    public ItemOrb(ItemStack item, GHPlayer target, Location location)
    {
        super(location, target);
        _item = item;
    }

    @Override
    protected void onCollected()
    {
        if(_target != null)
        {
            MCUtil.GiveItem(_target.getPlayer(), _item);
        }
    }

    @Override
    protected void onTick()
    {
        _location.getWorld().spawnParticle(Particle.DUST, _location, 2, new Particle.DustOptions(Color.AQUA, 1.0f));
    }
}
