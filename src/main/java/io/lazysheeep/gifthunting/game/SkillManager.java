package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.buffs.CounteringBuff;
import io.lazysheeep.gifthunting.buffs.BindBuff;
import io.lazysheeep.gifthunting.buffs.SilenceBuff;
import io.lazysheeep.gifthunting.buffs.SpeedBuff;
import io.lazysheeep.gifthunting.factory.CustomItem;
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
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurationNode;

public class SkillManager implements Listener
{
    private float _stealerScorePercentage;
    private int _silenceDuration;
    private float _silenceDistance;
    private int _counterDuration;
    private int _revolutionDuration;
    private int _speedDuration;
    private int _bindDuration;

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
        _counterDuration = configNode.node("counterDuration").getInt(0);
        _revolutionDuration = configNode.node("revolutionDuration").getInt(0);
        _speedDuration = configNode.node("speedDuration").getInt(0);
        _bindDuration = configNode.node("bindDuration").getInt(0);
    }

    private void onUseBooster(GHPlayer ghPlayer)
    {
        var skill = ghPlayer.getSkill(CustomItem.BOOSTER);
        if(skill != null)
        {
            skill.onUse();
        }
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
        var skill = ghPlayer.getSkill(CustomItem.COUNTER);
        if(skill != null)
        {
            skill.onUse();
        }
    }

    private void onUseSpeed(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        ghPlayer.addBuff(new SpeedBuff(_speedDuration));
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

    private void onUseBind(GHPlayer ghPlayer, GHPlayer clickedGHPlayer)
    {
        if(!clickedGHPlayer.hasBuff(CounteringBuff.class))
        {
            clickedGHPlayer.addBuff(new BindBuff(_bindDuration));
        }
    }

    // right click use items
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
        Action action = event.getAction();
        ItemStack item = event.getItem();
        CustomItem customItem = CustomItem.checkItem(item);

        if(customItem != null && action.isRightClick() && ghPlayer != null)
        {
            if(ghPlayer.hasBuff(SilenceBuff.class) || ghPlayer.hasBuff(BindBuff.class))
            {
                event.setCancelled(true);
                return;
            }

            switch (customItem)
            {
                case BOOSTER ->
                {
                    event.setCancelled(true);
                    onUseBooster(ghPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
                case SILENCER ->
                {
                    event.setCancelled(true);
                    onUseSilence(ghPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
                case COUNTER ->
                {
                    event.setCancelled(true);
                    onUseCounter(ghPlayer);
                }
                case REVOLUTION ->
                {
                    event.setCancelled(true);
                }
                case SPEED ->
                {
                    event.setCancelled(true);
                    onUseSpeed(ghPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
            }
        }
    }

    // bind
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
    {
        Player player = event.getPlayer();
        Entity clickedEntity = event.getRightClicked();
        ItemStack item = player.getInventory().getItem(event.getHand());
        GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
        if(ghPlayer != null && clickedEntity instanceof Player clickedPlayer)
        {
            GHPlayer clickedGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(clickedPlayer);
            if(clickedGHPlayer != null)
            {
                if(ghPlayer.hasBuff(SilenceBuff.class) || ghPlayer.hasBuff(BindBuff.class))
                {
                    event.setCancelled(true);
                    return;
                }

                if (CustomItem.checkItem(item) == CustomItem.BIND)
                {
                    onUseBind(ghPlayer, clickedGHPlayer);
                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
        }
    }

    // steal
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerFishEvent(PlayerFishEvent event)
    {
        Player player = event.getPlayer();
        GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
        Entity hookedEntity = event.getCaught();
        if(ghPlayer != null && event.getState() == PlayerFishEvent.State.CAUGHT_ENTITY && hookedEntity instanceof Player hookedPlayer)
        {
            if(ghPlayer.hasBuff(SilenceBuff.class) || ghPlayer.hasBuff(BindBuff.class))
            {
                event.setCancelled(true);
                return;
            }

            GHPlayer hookedGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(hookedPlayer);
            ItemStack item = player.getInventory().getItemInMainHand();
            if(hookedGHPlayer != null && !ghPlayer.hasBuff(SilenceBuff.class) && CustomItem.checkItem(item) == CustomItem.STEALER)
            {
                onUseSteal(ghPlayer, hookedGHPlayer);
                item.setAmount(item.getAmount() - 1);
            }
        }
    }

    // club knock back
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPrePlayerAttackEntity(PrePlayerAttackEntityEvent event)
    {
        Player player = event.getPlayer();
        GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(player);
        Entity attackedEntity = event.getAttacked();
        ItemStack item = player.getInventory().getItemInMainHand();

        if(ghPlayer != null && attackedEntity instanceof Player attackedPlayer)
        {
            if(ghPlayer.hasBuff(SilenceBuff.class) || ghPlayer.hasBuff(BindBuff.class))
            {
                event.setCancelled(true);
                return;
            }

            if(CustomItem.checkItem(item) == CustomItem.CLUB)
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
