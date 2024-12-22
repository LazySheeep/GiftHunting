package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.lazuliui.LazuliUI;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurationNode;

public class SkillManager implements Listener
{
    private float _stealerScorePercentage;
    private int _silenceDuration;
    private float _silenceDistance;
    private int _reflectDuration;

    public void loadConfig()
    {
        ConfigurationNode configNode = GiftHunting.GetPlugin().getConfigRootNode();
        _stealerScorePercentage = configNode.node("stealerScorePercentage").getFloat(0.0f);
        _silenceDuration = configNode.node("silenceDuration").getInt(0);
        _silenceDistance = configNode.node("silenceDistance").getFloat(0.0f);
        _reflectDuration = configNode.node("reflectDuration").getInt(0);
    }

    // right click
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // get event attributes
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();

        if(item != null && action.isRightClick())
        {
            GHPlayer ghPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(player);
            if(ghPlayer != null)
            {
                event.setCancelled(true);
                if(GiftHunting.GetPlugin().getGameManager().getState() == GameState.PROGRESSING && ghPlayer.silenceTimer == 0)
                {
                    // booster
                    if(item.isSimilar(ItemFactory.Booster))
                    {
                        player.setVelocity(player.getVelocity()
                                                 .add(player.getLocation().getDirection().multiply(1.5f))
                                                 .add(new Vector(0.0f, 0.2f, 0.0f)));
                        item.setAmount(item.getAmount() - 1);
                        player.getWorld().playSound(player, Sound.ITEM_FIRECHARGE_USE, SoundCategory.MASTER, 1.0f, 1.0f);
                        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 2, 0.2f, 0.2f, 0.2f, 0.5f);
                    }
                    // silence
                    else if(item.isSimilar(ItemFactory.Silencer))
                    {
                        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, 1.0f, 1.0f);
                        for(GHPlayer otherGHPlayer : GiftHunting.GetPlugin().getPlayerManager().getAllGHPlayers())
                        {
                            if(otherGHPlayer != ghPlayer && otherGHPlayer.getPlayer().getLocation().distance(player.getLocation()) <= _silenceDistance)
                            {
                                if(otherGHPlayer.reflectTimer == 0)
                                {
                                    otherGHPlayer.silenceTimer = _silenceDuration;
                                    Player otherPlayer = otherGHPlayer.getPlayer();
                                    LazuliUI.sendMessage(otherPlayer, MessageFactory.getSilencedActionbarInfix(ghPlayer, _silenceDuration));
                                    otherPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, otherPlayer.getLocation()
                                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                                    otherPlayer.getWorld().playSound(otherPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                                }
                                // reflect
                                else
                                {
                                    ghPlayer.silenceTimer = _silenceDuration * 2;
                                    LazuliUI.sendMessage(player, MessageFactory.getSilencedActionbarInfix(otherGHPlayer, _silenceDuration * 2));
                                    player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation()
                                                                                                   .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                                    Player otherPlayer = otherGHPlayer.getPlayer();
                                    otherPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, otherPlayer.getLocation()
                                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                                    otherPlayer.getWorld().playSound(otherPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
                                }
                            }
                        }
                        item.setAmount(item.getAmount() - 1);
                    }
                    // reflector
                    else if(item.isSimilar(ItemFactory.Reflector))
                    {
                        ghPlayer.reflectTimer = _reflectDuration;
                        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.MASTER, 1.0f, 1.0f);
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }
        }
    }

    // steal
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        ItemStack item = player.getInventory().getItem(event.getHand());
        GHPlayer ghPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(player);
        if(ghPlayer != null && ghPlayer.silenceTimer == 0 && GiftHunting.GetPlugin().getGameManager().getState() == GameState.PROGRESSING)
        {
            // entity clicked is player
            if(clickedEntity instanceof Player clickedPlayer)
            {
                GHPlayer clickedGHPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(clickedPlayer);
                if(clickedGHPlayer != null)
                {
                    // steal
                    if (GiftHunting.GetPlugin().getGameManager().getState() == GameState.PROGRESSING && item.isSimilar(ItemFactory.Stealer))
                    {
                        int stealScore;
                        if(clickedGHPlayer.reflectTimer == 0)
                        {
                            stealScore = (int) (_stealerScorePercentage * clickedGHPlayer.getScore());
                            LazuliUI.sendMessage(player, MessageFactory.getStealMsg(clickedPlayer, stealScore));
                            LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(ghPlayer, stealScore));
                            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getBeenStolenMsg(player, stealScore));
                            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(clickedGHPlayer, -stealScore));
                            LazuliUI.broadcast(MessageFactory.getStealBroadcastMsg(player, clickedPlayer, stealScore));
                            clickedPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                            ghPlayer.addScore(stealScore);
                            clickedGHPlayer.addScore(-stealScore);
                        }
                        // reflect
                        else
                        {
                            stealScore = (int) (_stealerScorePercentage * ghPlayer.getScore() * 1.5f);
                            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getStealMsg(player, stealScore));
                            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(clickedGHPlayer, stealScore));
                            LazuliUI.sendMessage(player, MessageFactory.getBeenStolenMsg(clickedPlayer, stealScore));
                            LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(ghPlayer, -stealScore));
                            LazuliUI.broadcast(MessageFactory.getStealReflectedMsg(player, clickedPlayer, stealScore));
                            player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                            player.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                            clickedPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, clickedPlayer.getLocation()
                                                                                                     .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
                            ghPlayer.addScore(-stealScore);
                            clickedGHPlayer.addScore(stealScore);
                        }
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }
        }
    }

    // club knock back
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity attackedEntity = event.getAttacked();
        ItemStack item = player.getInventory().getItemInMainHand();
        GHPlayer ghPlayer = GiftHunting.GetPlugin().getPlayerManager().getGHPlayer(player);
        if(ghPlayer != null && ghPlayer.silenceTimer == 0 && GiftHunting.GetPlugin().getGameManager().getState() == GameState.PROGRESSING)
        {
            // entity attacked is player
            if(attackedEntity instanceof Player attackedPlayer && item.isSimilar(ItemFactory.Club))
            {
                item.setAmount(item.getAmount() - 1);
                Vector knockBack = attackedPlayer.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(2.0f).add(new Vector(0.0f, 0.5f, 0.0f));
                attackedPlayer.setVelocity(attackedPlayer.getVelocity().add(knockBack));
                player.getWorld().playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);
            }
        }
    }
}