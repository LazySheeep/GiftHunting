package io.lazysheeep.gifthunting;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.uimanager.Message;
import io.lazysheeep.uimanager.UIManager;
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

import java.util.List;
import java.util.Map;

public class EventListener implements Listener
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
            if(item.isSimilar(ItemFactory.giftSpawnerSetter))  // use giftSpawnerSetter to set a spawner
            {
                if(action == Action.RIGHT_CLICK_BLOCK && clickedBlock != null)
                {
                    Location newLocation = clickedBlock.getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f));
                    GiftHunting.plugin.config.addGiftSpawnerLocation(newLocation);
                    UIManager.sendMessage(event.getPlayer(), new Message(Message.Type.CHAT, MessageFactory.getAddGiftSpawnerMsg(newLocation), Message.LoadMode.REPLACE, 1));
                }
            }
            else if(item.isSimilar(ItemFactory.booster))       // use booster to take off
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

        if(player.hasPermission("gifthunting.op"))
        {
            showSpawnerLocations = (item != null && item.isSimilar(ItemFactory.giftSpawnerSetter));
        }
    }

    // player click armor_stand
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

    // server tick
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerTickStartEvent(ServerTickStartEvent event)
    {
        // ALWAYS
        if(showSpawnerLocations)
        {
            for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
            {
                if(player.hasPermission("gifthunting.op"))
                    for(Location spawnerLocation : GiftHunting.plugin.config.getGiftSpawnerLocations())
                    {
                        player.spawnParticle(Particle.COMPOSTER, spawnerLocation.add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                    }
            }
        }

        // READYING
        if(GiftHunting.plugin.eventStats.state == GiftHunting.EventStats.State.READYING)
        {
            GiftHunting.plugin.eventStats.timer ++;
            // display countdown every second
            if(GiftHunting.plugin.eventStats.timer % 20 == 0)
            {
                for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                {
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getEventCountDownActionbarMsg(), Message.LoadMode.REPLACE, 20));
                }
            }
            // goto PROGRESSING
            if(GiftHunting.plugin.eventStats.timer >= GiftHunting.plugin.config.readyStateDuration)
            {
                GiftHunting.plugin.eventStats.state = GiftHunting.EventStats.State.PROGRESSING;
                GiftHunting.plugin.eventStats.timer = 0;

                for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                {
                    // give club
                    player.getInventory().remove(ItemFactory.club);
                    player.getInventory().addItem(ItemFactory.club);
                    // set actionbar
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, MessageFactory.getScoreActionbarMsg(0), Message.LoadMode.REPLACE, -1));
                }
            }
        }
        // PROGRESSING
        else if(GiftHunting.plugin.eventStats.state == GiftHunting.EventStats.State.PROGRESSING)
        {
            GiftHunting.plugin.eventStats.timer ++;

            // display time
            for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
            {
                UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_PREFIX, MessageFactory.getTimerActionbarMsg(), Message.LoadMode.IMMEDIATE, 1));
            }

            // deliver gifts
            for(Map<String, Object> giftBatch : GiftHunting.plugin.config.giftBatches)
            {
                if((Integer)giftBatch.get("time") == GiftHunting.plugin.eventStats.timer)
                {
                    deliverGiftBatch(giftBatch);
                }
            }
        }
    }

    private void deliverGiftBatch(Map<String, Object> giftBatch)
    {
        String type = (String)giftBatch.get("type");
        switch (type)
        {
            case "NORMAL" ->
            {
                int amount = (Integer)giftBatch.get("amount");
                List<Location> spawnLocations = Util.randomPick(GiftHunting.plugin.config.getGiftSpawnerLocations(), amount);
                for (Location loc : spawnLocations)
                {
                    new Gift(loc, Gift.GiftType.NORMAL);
                }
                GiftHunting.plugin.getServer().broadcast(MessageFactory.getSpawnGiftMsg(amount, Gift.GiftType.NORMAL), "gifthunting.op");
            }
            case "SPECIAL" ->
            {
                Location spawnLocation = Util.randomPickOne(GiftHunting.plugin.config.getGiftSpawnerLocations());
                new Gift(spawnLocation, Gift.GiftType.SPECIAL);
                GiftHunting.plugin.getServer().broadcast(MessageFactory.getSpawnGiftMsg(1, Gift.GiftType.SPECIAL), "gifthunting.op");
            }
            default -> {}
        }
    }
}