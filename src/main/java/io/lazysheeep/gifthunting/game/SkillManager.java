package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.buffs.*;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.entity.BombDrone;
import io.lazysheeep.gifthunting.entity.ScoreOrb;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.skills.Skill;
import io.lazysheeep.lazuliui.LazuliUI;
import io.papermc.paper.event.player.PrePlayerAttackEntityEvent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Arrow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.NamespacedKey;
import org.spongepowered.configurate.ConfigurationNode;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class SkillManager implements Listener
{
    private float _stealerScorePercentage;
    private int _silenceDuration;
    private float _silenceDistance;
    private int _counterDuration;
    private int _revolutionDuration;
    private int _speedDuration;
    private int _bindDuration;
    private int _bindStacksRequired;
    private int _oathDuration = 200;
    private double _absorbRadius = 6.0;

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
        _bindStacksRequired = configNode.node("bindStacksRequired").getInt(5);
        _absorbRadius = configNode.node("absorbRadius").getDouble(6.0);
    }

    private void onUseSilence(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, 1.0f, 1.0f);
        for(GHPlayer victimGHPlayer : ghPlayer.getGameInstance().getPlayerManager().getOnlineGHPlayers())
        {
            if(victimGHPlayer != ghPlayer && victimGHPlayer.getPlayer().getLocation().distance(player.getLocation()) <= _silenceDistance)
            {
                if(!victimGHPlayer.hasBuff(CounteringBuff.class))
                {
                    victimGHPlayer.addBuff(new SilenceBuff(_silenceDuration));
                    Player otherPlayer = victimGHPlayer.getPlayer();
                    LazuliUI.sendMessage(otherPlayer, MessageFactory.getSilencedMsg(ghPlayer));
                    otherPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, otherPlayer.getLocation()
                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    otherPlayer.getWorld().playSound(otherPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                }
                else
                {
                    ghPlayer.addBuff(new SilenceBuff(_silenceDuration * 2));
                    LazuliUI.sendMessage(player, MessageFactory.getSilencedMsg(victimGHPlayer));
                    player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation()
                                                                                   .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);

                    Player victimPlayer = victimGHPlayer.getPlayer();
                    LazuliUI.sendMessage(victimPlayer, MessageFactory.getSilenceCounteredMsg(ghPlayer));
                    victimPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, victimPlayer.getLocation()
                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    victimPlayer.getWorld().playSound(victimPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
                    victimGHPlayer.removeBuff(CounteringBuff.class);
                }
            }
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
            LazuliUI.broadcast(MessageFactory.getStealBroadcastMsg(player, clickedPlayer, stealScore));
            clickedPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedGHPlayer.addScore(-stealScore);
            _gameInstance.getEntityManager().addEntity(new ScoreOrb(clickedGHPlayer.getBodyLocation(), clickedGHPlayer, ghPlayer, stealScore));
        }
        else
        {
            stealScore = (int) (_stealerScorePercentage * ghPlayer.getScore() * 1.5f);
            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getStealMsg(player, stealScore));
            LazuliUI.sendMessage(player, MessageFactory.getBeenStolenMsg(clickedPlayer, stealScore));
            LazuliUI.broadcast(MessageFactory.getStealReflectedBroadcastMsg(player, clickedPlayer, stealScore));
            player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                  .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            player.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, clickedPlayer.getLocation()
                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
            ghPlayer.addScore(-stealScore);
            _gameInstance.getEntityManager().addEntity(new ScoreOrb(ghPlayer.getBodyLocation(), ghPlayer, clickedGHPlayer, stealScore));
            clickedGHPlayer.removeBuff(CounteringBuff.class);
        }
    }

    private void onUseClub(GHPlayer ghPlayer, GHPlayer attackedGHPlayer)
    {
        Player player = ghPlayer.getPlayer();
        Player attackedPlayer = attackedGHPlayer.getPlayer();
        if(!attackedGHPlayer.hasBuff(CounteringBuff.class))
        {
            player.getWorld().playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);

            Vector knockBack = player.getLocation().getDirection().setY(0.0).normalize().setY(0.3).multiply(2.0);
            attackedPlayer.setVelocity(attackedPlayer.getVelocity().add(knockBack));
            attackedPlayer.getWorld().playSound(player, Sound.ENTITY_PLAYER_HURT, SoundCategory.MASTER, 1.0f, 1.0f);
            attackedPlayer.getWorld().spawnParticle(Particle.CRIT, attackedGHPlayer.getBodyLocation(), 8, 0.3f, 0.3f, 0.3f);
            attackedPlayer.sendHurtAnimation(0);
        }
        else
        {
            player.getWorld().playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 1.0f);

            attackedPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, attackedPlayer.getLocation()
                                                                                           .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            attackedPlayer.getWorld().playSound(attackedPlayer, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
            attackedGHPlayer.removeBuff(CounteringBuff.class);

            Vector knockBack = player.getLocation().getDirection().setY(0.0).normalize().setY(0.3).multiply(-3.0);
            player.setVelocity(player.getVelocity().add(knockBack));
            player.getWorld().playSound(player, Sound.ENTITY_PLAYER_HURT, SoundCategory.MASTER, 1.0f, 1.0f);
            player.getWorld().spawnParticle(Particle.CRIT, attackedGHPlayer.getBodyLocation(), 8, 0.3f, 0.3f, 0.3f);
            player.sendHurtAnimation(0);
        }
    }

    private void onUseBigClub(GHPlayer ghPlayer, GHPlayer attackedGHPlayer)
    {
        Player player = ghPlayer.getPlayer();
        Player attackedPlayer = attackedGHPlayer.getPlayer();

        player.getWorld().playSound(player, Sound.ENTITY_ITEM_BREAK, SoundCategory.MASTER, 1.0f, 0.8f);

        Vector knockBack = player.getLocation().getDirection().setY(0.0).normalize().setY(1.5).multiply(80.0);
        attackedPlayer.setVelocity(attackedPlayer.getVelocity().add(knockBack));
        attackedPlayer.getWorld().playSound(player, Sound.ENTITY_PLAYER_HURT, SoundCategory.MASTER, 1.0f, 1.0f);
        attackedPlayer.getWorld().playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1.0f, 0.9f);
        attackedPlayer.getWorld().spawnParticle(Particle.CRIT, attackedGHPlayer.getBodyLocation(), 16, 0.5f, 0.5f, 0.5f);
        attackedPlayer.getWorld().spawnParticle(Particle.EXPLOSION, attackedGHPlayer.getBodyLocation(), 1, 0.0f, 0.0f, 0.0f);
        attackedPlayer.sendHurtAnimation(0);
        attackedGHPlayer.addBuff(new BlewUpBuff(20));
    }

    private void onUseBind(ItemStack item, GHPlayer ghPlayer, GHPlayer clickedGHPlayer)
    {
        if(!clickedGHPlayer.hasBuff(CounteringBuff.class))
        {
            clickedGHPlayer.addBuff(new BindBuff(_bindDuration));
            item.setAmount(item.getAmount() - 1);
        }
        else
        {

        }
    }

    private void onUseAbsorb(GHPlayer ghPlayer)
    {
        double r2 = _absorbRadius * _absorbRadius;
        for(var orb : _gameInstance.getEntityManager().getOrbs())
        {
            if(orb.getLocation().distanceSquared(ghPlayer.getBodyLocation()) <= r2)
            {
                orb.setTarget(ghPlayer);
            }
        }
    }

    private void onUseBombDrone(GHPlayer ghPlayer)
    {
        GHPlayer targetGHPlayer = null;
        for(GHPlayer otherGHPlayer : ghPlayer.getGameInstance().getPlayerManager().getOnlineGHPlayers())
        {
            if(otherGHPlayer != ghPlayer)
            {
                if(targetGHPlayer == null || otherGHPlayer.getScore() > targetGHPlayer.getScore())
                {
                    targetGHPlayer = otherGHPlayer;
                }
            }
        }
        if(targetGHPlayer != null)
        {
            ghPlayer.getGameInstance().getEntityManager().addEntity(new BombDrone(ghPlayer.getEyeLocation(), targetGHPlayer));
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
                case SKILL_BOOSTER ->
                {
                    event.setCancelled(true);
                    ghPlayer.useSkill(Skill.BOOST);
                }
                case SILENCER ->
                {
                    event.setCancelled(true);
                    onUseSilence(ghPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
                case SKILL_COUNTER ->
                {
                    event.setCancelled(true);
                    ghPlayer.useSkill(Skill.COUNTER);
                }
                case SKILL_DETECT ->
                {
                    event.setCancelled(true);
                    ghPlayer.useSkill(Skill.DETECT);
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
                case ABSORB ->
                {
                    event.setCancelled(true);
                    onUseAbsorb(ghPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
                case BOMB_DRONE ->
                {
                    event.setCancelled(true);
                    onUseBombDrone(ghPlayer);
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
                    event.setCancelled(true);
                    onUseBind(item, ghPlayer, clickedGHPlayer);
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

            GHPlayer attackedGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(attackedPlayer);
            if(attackedGHPlayer != null)
            {
                CustomItem type = CustomItem.checkItem(item);
                if(type == CustomItem.STICK)
                {
                    onUseClub(ghPlayer, attackedGHPlayer);
                }
                else if(type == CustomItem.SUPER_STICK)
                {
                    onUseBigClub(ghPlayer, attackedGHPlayer);
                }
                item.setAmount(item.getAmount() - 1);
                event.setCancelled(true);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityShootBow(EntityShootBowEvent event)
    {
        if(!(event.getEntity() instanceof Player shooter)) return;
        GHPlayer gh = _gameInstance.getPlayerManager().getGHPlayer(shooter);
        if(gh == null) return;
        ItemStack bow = event.getBow();
        if(CustomItem.checkItem(bow) != CustomItem.SKILL_DAWN_BOW) return;
        ItemStack consumed = event.getConsumable();
        if(CustomItem.checkItem(consumed) != CustomItem.SKILL_DAWN_ARROW)
        {
            event.setCancelled(true);
            return;
        }
        Arrow arrow = (Arrow) event.getProjectile();
        NamespacedKey key = new NamespacedKey(io.lazysheeep.gifthunting.GiftHunting.GetPlugin(), "dawn_arrow");
        arrow.getPersistentDataContainer().set(key, PersistentDataType.INTEGER, 1);
        arrow.setPickupStatus(Arrow.PickupStatus.DISALLOWED);
        gh.useSkill(Skill.DAWN);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if(!(event.getEntity() instanceof Arrow arrow)) return;
        NamespacedKey key = new NamespacedKey(io.lazysheeep.gifthunting.GiftHunting.GetPlugin(), "dawn_arrow");
        Integer tag = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if(tag == null || tag != 1) return;
        ProjectileSource src = arrow.getShooter();
        if(!(src instanceof Player shooter)) return;
        GHPlayer attacker = _gameInstance.getPlayerManager().getGHPlayer(shooter);
        if(attacker == null) return;
        Entity hit = event.getHitEntity();
        if(!(hit instanceof Player victimPlayer)) return;
        GHPlayer victim = _gameInstance.getPlayerManager().getGHPlayer(victimPlayer);
        if(victim == null) return;

        event.setCancelled(true);

        if(!victim.hasBuff(DawnBuff.class)) return;
        if(victim.hasBuff(OathBuff.class)) return;

        if(victim.hasBuff(CounteringBuff.class))
        {
            victim.addBuff(new OathBuff(_oathDuration));
            victim.removeBuff(CounteringBuff.class);
        }
        else
        {
            event.setCancelled(false);
            int lose = Math.max(1, (int)(victim.getScore() * 0.1f));
            victim.addScore(-lose);
            _gameInstance.getEntityManager().addEntity(new ScoreOrb(victim.getBodyLocation(), victim, null, lose));
        }
    }
}
