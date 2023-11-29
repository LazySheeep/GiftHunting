package io.lazysheeep.mczju.christmas;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.mczju.christmas.command.Chris;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

public class EventListener implements Listener
{
    // to set gift spawner
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // spawn location setter
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK
        && event.hasItem()
        && event.getItem().getType() == Material.STICK
        && event.getItem().getItemMeta().hasDisplayName()
        && event.getItem().getItemMeta().displayName().equals(Component.text("Gift Spawner Setter")))
        {
            Location newLocation = event.getPlayer().getTargetBlock(8).getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f));
            Christmas.cfg.giftSpawnerLocations.add(newLocation);
            event.getPlayer().sendMessage(Component.text("New Gift Spawner Added!" + newLocation.toString()));
        }
    }

    // to get a gift
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        Gift gift = Gift.getGift(event.getRightClicked());
        if(gift != null)
        {
            gift.clicked(event.getPlayer());
            event.setCancelled(true);
        }
    }

    // on server tick
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerTickStartEvent(ServerTickStartEvent event)
    {
        if(Christmas.eventStats.state == Christmas.EventStats.State.READYING)
        {
            Christmas.eventStats.timer ++;
            if(Christmas.eventStats.timer >= 6000)
            {
                Christmas.eventStats.state = Christmas.EventStats.State.PROGRESSING;
                Christmas.eventStats.timer = 0;
            }
        }
        else if(Christmas.eventStats.state == Christmas.EventStats.State.PROGRESSING)
        {
            Christmas.eventStats.timer ++;
        }
    }
}