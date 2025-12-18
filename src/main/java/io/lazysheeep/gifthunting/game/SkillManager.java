package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.buffs.CounteringBuff;
import io.lazysheeep.gifthunting.buffs.SilenceBuff;
import io.lazysheeep.gifthunting.buffs.SpeedBuff;
import io.lazysheeep.gifthunting.factory.CustomItems;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.orbs.ScoreOrb;
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
import org.bukkit.event.player.PlayerFishEvent;
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
    private int _revolutionDuration;
    private int _speedUpDuration;

    private final GameInstance _gameInstance;

    public SkillManager(GameInstance gameInstance)
    {
        _gameInstance = gameInstance;
    }

    public void loadConfig(ConfigurationNode configNode)
    {
        _stealerScorePercentage = configNode.node("stealerScorePercentage").getFloat(0.0f);
        _silenceDuration = configNode.node("silenceDuration").getInt(0);
        _silenceDistance = configNode.node("silenceDistance").getFloat(0.0f);
        _reflectDuration = configNode.node("reflectDuration").getInt(0);
        _revolutionDuration = configNode.node("revolutionDuration").getInt(0);
        _speedUpDuration = configNode.node("speedUpDuration").getInt(0);
    }

    private void onUseBooster(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        player.setVelocity(player.getVelocity()
                                 .add(player.getLocation().getDirection().multiply(1.5f))
                                 .add(new Vector(0.0f, 0.2f, 0.0f)));
        player.getWorld().playSound(player, Sound.ITEM_FIRECHARGE_USE, SoundCategory.MASTER, 1.0f, 1.0f);
        player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 2, 0.2f, 0.2f, 0.2f, 0.5f);
    }

    private void onUseSilence(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, 1.0f, 1.0f);
        for(GHPlayer otherGHPlayer : ghPlayer.getGameInstance().getPlayerManager().getOnlineGHPlayers())
        {
            if(otherGHPlayer != ghPlayer && otherGHPlayer.getPlayer().getLocation().distance(player.getLocation()) <= _silenceDistance)
            {
                if(!otherGHPlayer.hasBuff(CounteringBuff.class))
                {
                    otherGHPlayer.addBuff(new SilenceBuff(_silenceDuration));
                    Player otherPlayer = otherGHPlayer.getPlayer();
                    LazuliUI.sendMessage(otherPlayer, MessageFactory.getSilencedActionbarInfix(ghPlayer, _silenceDuration));
                    otherPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, otherPlayer.getLocation()
                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    otherPlayer.getWorld().playSound(otherPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                }
                // reflect
                else
                {
                    ghPlayer.addBuff(new SilenceBuff(_silenceDuration * 2));
                    LazuliUI.sendMessage(player, MessageFactory.getSilencedActionbarInfix(otherGHPlayer, _silenceDuration * 2));
                    player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation()
                                                                                   .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                    Player otherPlayer = otherGHPlayer.getPlayer();
                    otherPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, otherPlayer.getLocation()
                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    otherPlayer.getWorld().playSound(otherPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
                    otherGHPlayer.removeBuff(CounteringBuff.class);
                }
            }
        }
    }

    private void onUseCounter(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        ghPlayer.addBuff(new CounteringBuff(_reflectDuration));
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.MASTER, 1.0f, 1.0f);
    }

    private void onUseSpeed(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        ghPlayer.addBuff(new SpeedBuff(_speedUpDuration));
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_TRIAL_SPAWNER_OMINOUS_ACTIVATE, SoundCategory.MASTER, 1.0f, 0.7f);
    }

    private void onUseSteal(GHPlayer ghPlayer, GHPlayer clickedGHPlayer)
    {
        Player player = ghPlayer.getPlayer();
        Player clickedPlayer = clickedGHPlayer.getPlayer();
        int stealScore;
        if(!clickedGHPlayer.hasBuff(CounteringBuff.class))
        {
            stealScore = (int) (_stealerScorePercentage * clickedGHPlayer.getScore());
            LazuliUI.sendMessage(player, MessageFactory.getStealMsg(clickedPlayer, stealScore));
            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getBeenStolenMsg(player, stealScore));
            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(clickedGHPlayer.getScore(), -stealScore));
            LazuliUI.broadcast(MessageFactory.getStealBroadcastMsg(player, clickedPlayer, stealScore));
            clickedPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedGHPlayer.addScore(-stealScore);
            _gameInstance.getOrbManager().addOrb(new ScoreOrb(stealScore, ghPlayer, clickedGHPlayer.getBodyLocation()));
        }
        // reflect
        else
        {
            stealScore = (int) (_stealerScorePercentage * ghPlayer.getScore() * 1.5f);
            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getStealMsg(player, stealScore));
            LazuliUI.sendMessage(player, MessageFactory.getBeenStolenMsg(clickedPlayer, stealScore));
            LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(ghPlayer.getScore(), -stealScore));
            LazuliUI.broadcast(MessageFactory.getStealReflectedBroadcastMsg(player, clickedPlayer, stealScore));
            player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                  .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            player.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, clickedPlayer.getLocation()
                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
            ghPlayer.addScore(-stealScore);
            _gameInstance.getOrbManager().addOrb(new ScoreOrb(stealScore, clickedGHPlayer, ghPlayer.getBodyLocation()));
            clickedGHPlayer.removeBuff(CounteringBuff.class);
        }
    }

    private void onUseClub(GHPlayer ghPlayer, GHPlayer attackedGHPlayer)
    {
        Player player = ghPlayer.getPlayer();
        Player attackedPlayer = attackedGHPlayer.getPlayer();
        if(!attackedGHPlayer.hasBuff(CounteringBuff.class))
        {
            Vector knockBack = attackedPlayer.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(2.0f).add(new Vector(0.0f, 0.5f, 0.0f));
            attackedPlayer.setVelocity(attackedPlayer.getVelocity().add(knockBack));
            player.getWorld().playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);
        }
        else
        {
            Vector knockBack = attackedPlayer.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().multiply(-3.0f).add(new Vector(0.0f, 0.5f, 0.0f));
            player.setVelocity(player.getVelocity().add(knockBack));
            player.getWorld().playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);
            attackedPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, attackedPlayer.getLocation()
                                                                                           .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            attackedPlayer.getWorld().playSound(attackedPlayer, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
            attackedGHPlayer.removeBuff(CounteringBuff.class);
        }
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
            GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
            if(ghPlayer != null)
            {
                // Only handle and cancel when using our custom right-click items; otherwise let vanilla behavior (e.g., fishing rod cast) proceed.
                if(_gameInstance.getCurrentStateEnum() == GHStates.PROGRESSING && !ghPlayer.hasBuff(SilenceBuff.class))
                {
                    // booster
                    if(CustomItems.checkItemType(item, CustomItems.BOOSTER))
                    {
                        event.setCancelled(true);
                        onUseBooster(ghPlayer);
                        item.setAmount(item.getAmount() - 1);
                    }
                    // silence
                    else if(CustomItems.checkItemType(item, CustomItems.SILENCER))
                    {
                        event.setCancelled(true);
                        onUseSilence(ghPlayer);
                        item.setAmount(item.getAmount() - 1);
                    }
                    // reflector
                    else if(CustomItems.checkItemType(item, CustomItems.REFLECTOR))
                    {
                        event.setCancelled(true);
                        onUseCounter(ghPlayer);
                        item.setAmount(item.getAmount() - 1);
                    }
                    // revolution
                    else if(CustomItems.checkItemType(item, CustomItems.REVOLUTION))
                    {
                        event.setCancelled(true);
                        /*GHPlayer revolutionTarget = _gameInstance.getPlayerManager().getAllGHPlayersSorted().getFirst();
                        ghPlayer.revolutionTimer = _revolutionDuration;
                        ghPlayer.revolutionTarget = revolutionTarget;
                        LazuliUI.broadcast(MessageFactory.getRevolutionBroadcastMsg(ghPlayer, revolutionTarget));
                        item.setAmount(item.getAmount() - 1);*/
                    }
                    // speed up
                    else if(CustomItems.checkItemType(item, CustomItems.SPEED_UP))
                    {
                        event.setCancelled(true);
                        onUseSpeed(ghPlayer);
                        item.setAmount(item.getAmount() - 1);
                    }
                    // else: do nothing and do not cancel, to allow normal item use such as fishing rods.
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
        GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
        if(ghPlayer != null && !ghPlayer.hasBuff(SilenceBuff.class) && _gameInstance.getCurrentStateEnum() == GHStates.PROGRESSING)
        {
            // entity clicked is player
            if(clickedEntity instanceof Player clickedPlayer)
            {
                GHPlayer clickedGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(clickedPlayer);
                if(clickedGHPlayer != null)
                {
                    // steal
                    if (_gameInstance.getCurrentStateEnum() == GHStates.PROGRESSING && CustomItems.checkItemType(item, CustomItems.STEALER))
                    {
                        onUseSteal(ghPlayer, clickedGHPlayer);
                        item.setAmount(item.getAmount() - 1);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFishEvent(PlayerFishEvent event)
    {
        if(event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY)
        {
            Entity hookedEntity = event.getCaught();
            if(hookedEntity instanceof Player hookedPlayer)
            {
                Player player = event.getPlayer();
                GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
                GHPlayer hookedGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(hookedPlayer);
                ItemStack item = player.getInventory().getItemInMainHand();
                if(ghPlayer != null && hookedGHPlayer != null && !ghPlayer.hasBuff(SilenceBuff.class) && CustomItems.checkItemType(item, CustomItems.STEALER) && _gameInstance.getCurrentStateEnum() == GHStates.PROGRESSING)
                {
                    onUseSteal(ghPlayer, hookedGHPlayer);
                    item.setAmount(item.getAmount() - 1);
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
        if(CustomItems.checkItemType(item, CustomItems.CLUB) && _gameInstance.getCurrentStateEnum() == GHStates.PROGRESSING)
        {
            GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
            if(ghPlayer != null && !ghPlayer.hasBuff(SilenceBuff.class) && attackedEntity instanceof Player attackedPlayer)
            {
                GHPlayer attackedGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(attackedPlayer);
                if(attackedGHPlayer != null)
                {
                    onUseClub(ghPlayer, attackedGHPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
    }
}