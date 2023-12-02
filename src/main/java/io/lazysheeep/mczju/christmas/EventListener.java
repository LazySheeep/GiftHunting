package io.lazysheeep.mczju.christmas;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
            Christmas.plugin.cfg.giftSpawnerLocations.add(newLocation);
            event.getPlayer().sendMessage(MessageFactory.getAddGiftSpawnerMsg(newLocation));
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
        if(Christmas.plugin.eventStats.state == Christmas.EventStats.State.READYING)
        {
            Christmas.plugin.eventStats.timer ++;

            if(Christmas.plugin.eventStats.timer % 20 == 0)
            {
                Collection<? extends Player> onlinePlayers = Christmas.plugin.getServer().getOnlinePlayers();
                for(Player player : onlinePlayers)
                {
                    player.sendActionBar(MessageFactory.getEventCountDownActionbarMsg());
                }
            }

            if(Christmas.plugin.eventStats.timer >= Christmas.plugin.cfg.readyStateDuration)
            {
                Christmas.plugin.eventStats.state = Christmas.EventStats.State.PROGRESSING;
                Christmas.plugin.eventStats.timer = 0;
            }
        }
        else if(Christmas.plugin.eventStats.state == Christmas.EventStats.State.PROGRESSING)
        {
            Christmas.plugin.eventStats.timer ++;

            for(Map<String, Object> giftBatch : Christmas.plugin.cfg.giftBatches)
            {
                if((Integer)giftBatch.get("time") == Christmas.plugin.eventStats.timer)
                {
                    deliverGiftBatch(giftBatch);
                    // Christmas.plugin.cfg.giftBatches.remove(giftBatch);
                }
            }
        }
    }

    private void deliverGiftBatch(Map<String, Object> giftBatch)
    {
        // TODO
        String type = (String)giftBatch.get("type");
        switch (type)
        {
            case "NORMAL" ->
            {
                int amount = (Integer)giftBatch.get("amount");
                List<Location> spawnLocations = Util.randomPick(Christmas.plugin.cfg.giftSpawnerLocations, amount);
                for (Location loc : spawnLocations)
                {
                    new Gift(loc, Gift.GiftType.NORMAL);
                }
                Christmas.plugin.getServer().broadcast(MessageFactory.getSpawnGiftMsg(amount, Gift.GiftType.NORMAL), "christmas.op");
            }
            case "SPECIAL" ->
            {
                Location spawnLocation = Util.randomPickOne(Christmas.plugin.cfg.giftSpawnerLocations);
                new Gift(spawnLocation, Gift.GiftType.SPECIAL);
                Christmas.plugin.getServer().broadcast(MessageFactory.getSpawnGiftMsg(1, Gift.GiftType.SPECIAL), "christmas.op");
            }
            default -> {}
        }
    }
}