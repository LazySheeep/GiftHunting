package io.lazysheeep.mczju.christmas;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class CEventListener implements Listener
{
    // player click
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // get event attributes
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();
        Block clickedBlock = event.getClickedBlock();

        if(item != null)
        {
            // switch between different items
            if(item.isSimilar(CItemFactory.giftSpawnerSetter))  // use giftSpawnerSetter to set a spawner
            {
                if(action == Action.RIGHT_CLICK_BLOCK && clickedBlock != null)
                {
                    Location newLocation = clickedBlock.getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f));
                    Christmas.plugin.config.addGiftSpawnerLocation(newLocation);
                    event.getPlayer().sendMessage(CMessageFactory.getAddGiftSpawnerMsg(newLocation));
                }
            }
            else if(item.isSimilar(CItemFactory.booster))       // use booster to take off
            {
                if(action.isRightClick())
                {
                    player.setVelocity(player.getVelocity().setY(1.0f));
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
    }

    // player change current held item
    private boolean showSpawnerLocations = false;
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerItemHeld(PlayerItemHeldEvent event)
    {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItem(event.getNewSlot());

        if(player.hasPermission("christmas.op"))
        {
            showSpawnerLocations = (item != null && item.isSimilar(CItemFactory.giftSpawnerSetter));
        }
    }

    // player click armor_stand
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        CGift gift = CGift.getGift(event.getRightClicked());
        if(gift != null)
        {
            gift.clicked(event.getPlayer());
            event.setCancelled(true);
        }
    }

    // server tick
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerTickStartEvent(ServerTickStartEvent event)
    {
        // ALWAYS
        if(showSpawnerLocations)
        {
            for(Player player : Christmas.plugin.getServer().getOnlinePlayers())
            {
                if(player.hasPermission("christmas.op"))
                    for(Location spawnerLocation : Christmas.plugin.config.getGiftSpawnerLocations())
                    {
                        player.spawnParticle(Particle.COMPOSTER, spawnerLocation.add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                    }
            }
        }

        // READYING
        if(Christmas.plugin.eventStats.state == Christmas.EventStats.State.READYING)
        {
            Christmas.plugin.eventStats.timer ++;
            // display countdown every second
            if(Christmas.plugin.eventStats.timer % 20 == 0)
            {
                Collection<? extends Player> onlinePlayers = Christmas.plugin.getServer().getOnlinePlayers();
                for(Player player : onlinePlayers)
                {
                    player.sendActionBar(CMessageFactory.getEventCountDownActionbarMsg());
                }
            }
            // goto PROGRESSING
            if(Christmas.plugin.eventStats.timer >= Christmas.plugin.config.readyStateDuration)
            {
                Christmas.plugin.eventStats.state = Christmas.EventStats.State.PROGRESSING;
                Christmas.plugin.eventStats.timer = 0;
            }
        }
        // PROGRESSING
        else if(Christmas.plugin.eventStats.state == Christmas.EventStats.State.PROGRESSING)
        {
            Christmas.plugin.eventStats.timer ++;
            // deliver gifts
            for(Map<String, Object> giftBatch : Christmas.plugin.config.giftBatches)
            {
                if((Integer)giftBatch.get("time") == Christmas.plugin.eventStats.timer)
                {
                    deliverGiftBatch(giftBatch);
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
                List<Location> spawnLocations = CUtil.randomPick(Christmas.plugin.config.getGiftSpawnerLocations(), amount);
                for (Location loc : spawnLocations)
                {
                    new CGift(loc, CGift.GiftType.NORMAL);
                }
                Christmas.plugin.getServer().broadcast(CMessageFactory.getSpawnGiftMsg(amount, CGift.GiftType.NORMAL), "christmas.op");
            }
            case "SPECIAL" ->
            {
                Location spawnLocation = CUtil.randomPickOne(Christmas.plugin.config.getGiftSpawnerLocations());
                new CGift(spawnLocation, CGift.GiftType.SPECIAL);
                Christmas.plugin.getServer().broadcast(CMessageFactory.getSpawnGiftMsg(1, CGift.GiftType.SPECIAL), "christmas.op");
            }
            default -> {}
        }
    }
}