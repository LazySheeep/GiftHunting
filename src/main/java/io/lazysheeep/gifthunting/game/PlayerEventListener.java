package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.lazuliui.LazuliUI;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
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
import org.spongepowered.configurate.ConfigurationNode;

public class PlayerEventListener implements Listener
{
    private float _stealerScorePercentage;

    public void loadConfig()
    {
        ConfigurationNode configNode = GiftHunting.GetPlugin().getConfigRootNode();
        _stealerScorePercentage = configNode.node("stealerScorePercentage").getFloat(0.0f);
    }

    // player click
    @EventHandler(priority = EventPriority.HIGHEST)
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
            if(item.isSimilar(ItemFactory.normalGiftSpawnerSetter))
            {
                if(clickedBlock != null && player.hasPermission("op") && (GiftHunting.GetPlugin().getGameManager().getState() == GameState.IDLE))
                {
                    if(action == Action.RIGHT_CLICK_BLOCK)
                    {
                        Location newLocation = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                        GiftHunting.GetPlugin().getGiftManager().addNormalSpawner(newLocation);
                        LazuliUI.sendMessage(player, MessageFactory.getAddGiftSpawnerActionbar());
                    }
                    else if(action == Action.LEFT_CLICK_BLOCK)
                    {
                        Location location = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                        if(GiftHunting.GetPlugin().getGiftManager().removeNormalSpawner(location))
                            LazuliUI.sendMessage(player, MessageFactory.getRemoveGiftSpawnerActionbar());
                        event.setCancelled(true);
                    }
                }
            }
            else if(item.isSimilar(ItemFactory.specialGiftSpawnerSetter))
            {
                if(clickedBlock != null && player.hasPermission("op") && (GiftHunting.GetPlugin().getGameManager().getState() == GameState.IDLE))
                {
                    if(action == Action.RIGHT_CLICK_BLOCK)
                    {
                        Location newLocation = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                        GiftHunting.GetPlugin().getGiftManager().addSpecialSpawner(newLocation);
                        LazuliUI.sendMessage(player, MessageFactory.getAddGiftSpawnerActionbar());
                    }
                    else if(action == Action.LEFT_CLICK_BLOCK)
                    {
                        Location location = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                        if(GiftHunting.GetPlugin().getGiftManager().removeSpecialSpawner(location))
                            LazuliUI.sendMessage(player, MessageFactory.getRemoveGiftSpawnerActionbar());
                        event.setCancelled(true);
                    }
                }
            }
            // use booster to take off
            else if(item.isSimilar(ItemFactory.booster))
            {
                if(action.isRightClick() && GiftHunting.GetPlugin().getGameManager().getState() == GameState.PROGRESSING)
                {
                    GHPlayer ghPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(player);
                    if(ghPlayer != null)
                    {
                        player.setVelocity(player.getVelocity()
                                                 .add(player.getLocation().getDirection().multiply(1.5f))
                                                 .add(new Vector(0.0f, 0.2f, 0.0f)));
                        item.setAmount(item.getAmount() - 1);
                        player.playSound(player, Sound.ITEM_FIRECHARGE_USE, SoundCategory.MASTER, 1.0f, 1.0f);
                        player.spawnParticle(Particle.EXPLOSION, player.getLocation(), 20, 0.1f, 0.0f, 0.1f, 0.5f);
                    }
                }
            }
        }
    }

    // player click entity
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        ItemStack item = player.getInventory().getItem(event.getHand());
        GHPlayer ghPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(player);
        if(ghPlayer != null)
        {
            // entity clicked is player
            if(clickedEntity instanceof Player clickedPlayer)
            {
                GHPlayer clickedGHPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(clickedPlayer);
                if(clickedGHPlayer != null)
                {
                    // steal
                    if (GiftHunting.GetPlugin().getGameManager().getState() == GameState.PROGRESSING && item.isSimilar(ItemFactory.stealer))
                    {
                        int scoreStolen = (int)(_stealerScorePercentage * clickedGHPlayer.getScore());
                        LazuliUI.sendMessage(player, MessageFactory.getStealMsg(clickedPlayer, scoreStolen));
                        LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(ghPlayer, scoreStolen));
                        LazuliUI.sendMessage(clickedPlayer, MessageFactory.getBeenStolenMsg(player, scoreStolen));
                        LazuliUI.sendMessage(clickedPlayer, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(clickedGHPlayer, -scoreStolen));
                        LazuliUI.broadcast(MessageFactory.getStealBroadcastMsg(player, clickedPlayer));
                        clickedPlayer.spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                                       .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                        item.setAmount(item.getAmount() - 1);
                        ghPlayer.addScore(scoreStolen);
                        clickedGHPlayer.addScore(-scoreStolen);
                    }
                }
            }
        }
    }

    // player attack entity
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity attackedEntity = event.getAttacked();
        ItemStack item = player.getInventory().getItemInMainHand();
        GHPlayer ghPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(player);
        if(ghPlayer != null)
        {
            // entity attacked is player
            if(attackedEntity instanceof Player attackedPlayer && item.isSimilar(ItemFactory.club))
            {
                item.setAmount(item.getAmount() - 1);
                player.playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);
                attackedPlayer.setVelocity(attackedPlayer.getVelocity().add(new Vector(0.0f, 0.5f, 0.0f)));
            }
        }
    }

    // player click armor_stand
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        Gift gift = GiftHunting.GetPlugin().getGiftManager().getGift(event.getRightClicked());
        GHPlayer ghPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(event.getPlayer());
        if(gift != null && ghPlayer != null)
        {
            if(GiftHunting.GetPlugin().getGameManager().getState() == GameState.PROGRESSING)
            {
                gift.clicked(ghPlayer);
            }
            event.setCancelled(true);
        }
    }

    // player fall damage
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL && GiftHunting.GetPlugin().getPlayerManager().isGHPlayer(player))
            {
                event.setCancelled(true);
            }
        }
    }

    // protect itemFrame
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event)
    {
        Player player = event.getPlayer();
        if(!player.hasPermission("op"))
        {
            event.setCancelled(true);
        }
    }
}