package io.lazysheeep.gifthunting;

import io.lazysheeep.uimanager.Message;
import io.lazysheeep.uimanager.UIManager;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class PlayerEventListener implements Listener
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
            if(item.isSimilar(ItemFactory.giftSpawnerSetter))  // use giftSpawnerSetter to set or remove a spawner
            {
                if(clickedBlock != null && player.hasPermission(Permission.OP.name))
                {
                    if(action == Action.RIGHT_CLICK_BLOCK)
                    {
                        Location newLocation = clickedBlock.getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f));
                        GiftHunting.config.addGiftSpawner(newLocation);
                        UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getAddGiftSpawnerMsg(newLocation), Message.LoadMode.REPLACE, 30));
                    }
                    else if(action == Action.LEFT_CLICK_BLOCK)
                    {
                        Location location = clickedBlock.getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f));
                        if(GiftHunting.config.removeGiftSpawner(location))
                            UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getRemoveGiftSpawnerMsg(location), Message.LoadMode.REPLACE, 30));
                        event.setCancelled(true);
                    }
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
}