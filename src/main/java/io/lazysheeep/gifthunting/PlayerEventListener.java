package io.lazysheeep.gifthunting;

import io.lazysheeep.lazuliui.LazuliUI;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
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
            // use giftSpawnerSetter to set or remove a spawner
            if(item.isSimilar(ItemFactory.giftSpawnerSetter))
            {
                if(clickedBlock != null && player.hasPermission(Permission.OP.name) && (GiftHunting.gameManager.getState() == GameManager.State.IDLE || GiftHunting.gameManager.getState() == GameManager.State.PAUSED))
                {
                    if(action == Action.RIGHT_CLICK_BLOCK)
                    {
                        Location newLocation = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                        GiftHunting.config.addGiftSpawner(newLocation);
                        LazuliUI.sendMessage(player, MessageFactory.getAddGiftSpawnerActionbar());
                    }
                    else if(action == Action.LEFT_CLICK_BLOCK)
                    {
                        Location location = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                        if(GiftHunting.config.removeGiftSpawner(location))
                            LazuliUI.sendMessage(player, MessageFactory.getRemoveGiftSpawnerActionbar());
                        event.setCancelled(true);
                    }
                }
            }
            // use booster to take off
            else if(item.isSimilar(ItemFactory.booster))
            {
                if(action.isRightClick() && GiftHunting.gameManager.getState() == GameManager.State.PROGRESSING && player.hasPermission(Permission.PLAYER.name))
                {
                    player.setVelocity(player.getVelocity().add(player.getLocation().getDirection().multiply(1.5f)).add(new Vector(0.0f, 0.2f, 0.0f)));
                    item.setAmount(item.getAmount() - 1);
                    GiftHunting.plugin.world.playSound(player, Sound.ITEM_FIRECHARGE_USE, SoundCategory.MASTER, 1.0f, 1.0f);
                    GiftHunting.plugin.world.spawnParticle(Particle.EXPLOSION_NORMAL, player.getLocation(), 20, 0.1f, 0.0f, 0.1f, 0.5f);
                }
            }
        }
    }

    // player click entity
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        ItemStack item = player.getInventory().getItem(event.getHand());
        if(player.hasPermission(Permission.PLAYER.name) && GiftHunting.gameManager.getState() == GameManager.State.PROGRESSING)
        {
            // entity clicked is player
            if(clickedEntity instanceof Player clickedPlayer && clickedPlayer.hasPermission(Permission.PLAYER.name))
            {
                // steal
                if(item.isSimilar(ItemFactory.stealer))
                {
                    int scoreStolen = GiftHunting.config.stealerScore;
                    LazuliUI.sendMessage(player, MessageFactory.getStealMsg(clickedPlayer));
                    LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(player, scoreStolen));
                    LazuliUI.sendMessage(clickedPlayer, MessageFactory.getBeenStolenMsg(player));
                    LazuliUI.sendMessage(clickedPlayer, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(clickedPlayer, -scoreStolen));
                    LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getStealBroadcastMsg(player, clickedPlayer));
                    GiftHunting.plugin.world.spawnParticle(Particle.VILLAGER_ANGRY, clickedPlayer.getLocation().add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    item.setAmount(item.getAmount() - 1);
                    GiftHunting.gameManager.addScore(player, scoreStolen);
                    GiftHunting.gameManager.addScore(clickedPlayer, -scoreStolen);
                }
            }
        }
    }

    // player attack entity
    @EventHandler(priority = EventPriority.HIGH)
    public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity attackedEntity = event.getAttacked();
        ItemStack item = player.getInventory().getItemInMainHand();
        if(player.hasPermission(Permission.PLAYER.name) && item.isSimilar(ItemFactory.club))
        {
            item.setAmount(item.getAmount() - 1);
            player.playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);
            attackedEntity.setVelocity(attackedEntity.getVelocity().add(new Vector(0.0f, 0.5f, 0.0f)));
        }
    }

    // player click armor_stand
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        Gift gift = Gift.getGift(event.getRightClicked());
        if(gift != null)
        {
            if(GiftHunting.gameManager.getState() == GameManager.State.PROGRESSING)
            {
                gift.clicked(event.getPlayer());
            }
            event.setCancelled(true);
        }
    }

    // player fall damage
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL && player.hasPermission(Permission.PLAYER.name))
            {
                event.setCancelled(true);
            }
        }
    }
}