package io.lazysheeep.gifthunting.orbs;

import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.lazuliui.LazuliUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ItemOrb extends Orb
{
    private final ItemStack _item;

    public ItemOrb(Location location, @Nullable GHPlayer source, GHPlayer target, ItemStack item)
    {
        super(location, source, target);
        _item = item;
    }

    @Override
    protected void onCollected()
    {
        if(_target != null)
        {
            MCUtil.GiveItem(_target.getPlayer(), _item);
            if(_source != null && _source != _target)
            {
                var name = _source.getPlayer().getName();
                var itemName = _item.getItemMeta() != null && _item.getItemMeta().hasDisplayName() ? _item.getItemMeta().displayName() : Component.text(_item.getType().name());
                LazuliUI.sendMessage(_target.getPlayer(), MessageFactory.getCapturedItemFromOtherMsg(name, itemName));
            }
        }
    }

    @Override
    public void onTick(GameInstance gameInstance)
    {
        super.onTick(gameInstance);
        _location.getWorld().spawnParticle(Particle.DUST, _location, 2, new Particle.DustOptions(Color.AQUA, 1.0f));
    }
}