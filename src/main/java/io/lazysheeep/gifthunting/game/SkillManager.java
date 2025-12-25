package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.buffs.*;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.entity.BombDrone;
import io.lazysheeep.gifthunting.entity.ScoreOrb;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.skills.Skill;
import io.lazysheeep.gifthunting.utils.MCUtil;
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
    private int _speedDuration;
    private int _bindDuration;
    private int _oathDuration;
    private double _absorbRadius;

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
        _speedDuration = configNode.node("speedDuration").getInt(0);
        _bindDuration = configNode.node("bindDuration").getInt(0);
        _oathDuration = configNode.node("oathDuration").getInt(600);
        _absorbRadius = configNode.node("absorbRadius").getDouble(6.0);
    }

    private void onUseSilence(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_HURT, SoundCategory.MASTER, 1.0f, 1.0f);
        for(GHPlayer victimGHPlayer : ghPlayer.getGameInstance().getPlayerManager().getOnlineGHPlayers())
        {
            Player victimPlayer = victimGHPlayer.getPlayer();
            if(victimGHPlayer != ghPlayer && victimGHPlayer.getPlayer().getLocation().distance(player.getLocation()) <= _silenceDistance)
            {
                if (victimGHPlayer.hasBuff(CounteringBuff.class))
                {
                    ghPlayer.addBuff(new SilenceBuff(_silenceDuration * 2));
                    LazuliUI.sendMessage(player, MessageFactory.getSilencedMsg(victimGHPlayer));
                    player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation()
                                                                                   .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);

                    LazuliUI.sendMessage(victimPlayer, MessageFactory.getSilenceCounteredMsg(ghPlayer));
                    victimPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, victimPlayer.getLocation()
                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    victimPlayer.getWorld().playSound(victimPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
                    victimGHPlayer.removeBuff(CounteringBuff.class);
                }
                else
                {
                    victimGHPlayer.addBuff(new SilenceBuff(_silenceDuration));
                    LazuliUI.sendMessage(victimPlayer, MessageFactory.getSilencedMsg(ghPlayer));
                    victimPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, victimPlayer.getLocation()
                                                                                             .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
                    victimPlayer.getWorld().playSound(victimPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
                }
            }
        }
    }

    private void onUseBind(GHPlayer ghPlayer, GHPlayer clickedGHPlayer)
    {
        Player player = ghPlayer.getPlayer();
        Player clickedPlayer = clickedGHPlayer.getPlayer();
        if (clickedGHPlayer.hasBuff(CounteringBuff.class) || clickedGHPlayer.hasBuff(DawnBuff.class))
        {
            ghPlayer.addBuff(new BindBuff(_bindDuration));
            LazuliUI.sendMessage(player, MessageFactory.getBindCounteredMsg(clickedGHPlayer));
            player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, player.getLocation()
                                                                           .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);

            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getCounteringBindMsg(ghPlayer));
            clickedPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, clickedPlayer.getLocation()
                                                                                       .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedGHPlayer.removeBuff(CounteringBuff.class);
            MCUtil.GiveItem(clickedPlayer, CustomItem.BIND.create());
        }
        else
        {
            clickedGHPlayer.addBuff(new BindBuff(_bindDuration));
            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getBoundMsg(ghPlayer));
            clickedPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation().add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
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
        if (clickedGHPlayer.hasBuff(CounteringBuff.class) || clickedGHPlayer.hasBuff(DawnBuff.class))
        {
            stealScore = (int) (_stealerScorePercentage * ghPlayer.getScore());
            ghPlayer.addScore(-stealScore);
            _gameInstance.getEntityManager().addEntity(new ScoreOrb(ghPlayer.getBodyLocation(), ghPlayer, clickedGHPlayer, stealScore));
            LazuliUI.sendMessage(player, MessageFactory.getStealCounteredMsg(clickedPlayer, stealScore));
            player.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                  .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            player.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);

            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getCounteringStealMsg(player, stealScore));
            clickedPlayer.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, clickedPlayer.getLocation()
                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedGHPlayer.removeBuff(CounteringBuff.class);
            MCUtil.GiveItem(clickedPlayer, CustomItem.STEALER.create());

            for(GHPlayer p : _gameInstance.getPlayerManager().getOnlineGHPlayers())
            {
                if(p != ghPlayer && p != clickedGHPlayer)
                {
                    LazuliUI.sendMessage(p.getPlayer(), MessageFactory.getStealCounteredBroadcastMsg(player, clickedPlayer, stealScore));
                }
            }
        }
        else
        {
            stealScore = (int) (_stealerScorePercentage * clickedGHPlayer.getScore());
            LazuliUI.sendMessage(player, MessageFactory.getStealMsg(clickedPlayer, stealScore));

            LazuliUI.sendMessage(clickedPlayer, MessageFactory.getBeenStolenMsg(player, stealScore));
            clickedPlayer.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, clickedPlayer.getLocation()
                                                                                         .add(0.0f, 1.0f, 0.0f), 8, 0.3f, 0.3f, 0.3f);
            clickedPlayer.getWorld().playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);
            clickedGHPlayer.addScore(-stealScore);
            _gameInstance.getEntityManager().addEntity(new ScoreOrb(clickedGHPlayer.getBodyLocation(), clickedGHPlayer, ghPlayer, stealScore));

            for(GHPlayer p : _gameInstance.getPlayerManager().getOnlineGHPlayers())
            {
                if(p != ghPlayer && p != clickedGHPlayer)
                {
                    LazuliUI.sendMessage(p.getPlayer(), MessageFactory.getStealBroadcastMsg(player, clickedPlayer, stealScore));
                }
            }
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
            MCUtil.GiveItem(attackedPlayer, CustomItem.STICK.create());

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

    private boolean onUseAbsorb(GHPlayer ghPlayer)
    {
        double r2 = _absorbRadius * _absorbRadius;
        int count = 0;
        for(var orb : _gameInstance.getEntityManager().getOrbs())
        {
            if(orb.getLocation().distanceSquared(ghPlayer.getBodyLocation()) <= r2)
            {
                orb.setTarget(ghPlayer);
                count++;
            }
        }
        if(count > 0)
        {
            ghPlayer.getPlayer().getWorld().playSound(ghPlayer.getBodyLocation(), Sound.ITEM_TRIDENT_RETURN, SoundCategory.MASTER, 1.0f, 1.0f);
            LazuliUI.sendMessage(ghPlayer.getPlayer(), MessageFactory.getAbsorbMsg(count));
            return true;
        }
        else
        {
            LazuliUI.sendMessage(ghPlayer.getPlayer(), MessageFactory.getAbsorbFailedMsg());
            return false;
        }
    }

    private boolean onUseBombDrone(GHPlayer ghPlayer)
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
            Player player = ghPlayer.getPlayer();
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.MASTER, 1.0f, 1.0f);
            ghPlayer.getGameInstance().getEntityManager().addEntity(new BombDrone(ghPlayer.getEyeLocation(), ghPlayer, targetGHPlayer));
            return true;
        }
        return false;
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
                case SPEED ->
                {
                    event.setCancelled(true);
                    onUseSpeed(ghPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
                case SILENCER ->
                {
                    event.setCancelled(true);
                    onUseSilence(ghPlayer);
                    item.setAmount(item.getAmount() - 1);
                }
                case ABSORB ->
                {
                    event.setCancelled(true);
                    if(onUseAbsorb(ghPlayer))
                    {
                        item.setAmount(item.getAmount() - 1);
                    }
                }
                case BOMB_DRONE ->
                {
                    event.setCancelled(true);
                    if(onUseBombDrone(ghPlayer))
                    {
                        item.setAmount(item.getAmount() - 1);
                    }
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
                    onUseBind(ghPlayer, clickedGHPlayer);
                    item.setAmount(item.getAmount() - 1);
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

    // dawn bow shoot
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

    // dawn arrow hit
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onProjectileHit(ProjectileHitEvent event)
    {
        if(!(event.getEntity() instanceof Arrow arrow)) return;
        NamespacedKey key = new NamespacedKey(io.lazysheeep.gifthunting.GiftHunting.GetPlugin(), "dawn_arrow");
        Integer tag = arrow.getPersistentDataContainer().get(key, PersistentDataType.INTEGER);
        if(tag == null || tag != 1) return;
        ProjectileSource src = arrow.getShooter();
        if(!(src instanceof Player attackerPlayer)) return;
        GHPlayer attackerGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(attackerPlayer);
        if(attackerGHPlayer == null) return;
        Entity hit = event.getHitEntity();
        if(!(hit instanceof Player victimPlayer)) return;
        GHPlayer victimGHPlayer = _gameInstance.getPlayerManager().getGHPlayer(victimPlayer);
        if(victimGHPlayer == null) return;

        event.setCancelled(true);

        if(!victimGHPlayer.hasBuff(DawnBuff.class)) return;
        if(victimGHPlayer.hasBuff(OathBuff.class)) return;

        if(victimGHPlayer.hasBuff(CounteringBuff.class))
        {
            victimGHPlayer.addBuff(new OathBuff(_oathDuration));
            victimPlayer.getWorld().playSound(victimPlayer, Sound.BLOCK_ANVIL_LAND, SoundCategory.MASTER, 1.0f, 1.0f);
            LazuliUI.sendMessage(victimPlayer, MessageFactory.getCounteringDawnMsg(attackerGHPlayer));
            victimGHPlayer.removeBuff(CounteringBuff.class);

            LazuliUI.sendMessage(attackerPlayer, MessageFactory.getDawnCounteredMsg(victimGHPlayer));
            attackerPlayer.getWorld().playSound(attackerPlayer, Sound.ENTITY_VILLAGER_NO, SoundCategory.MASTER, 1.0f, 1.0f);

            for(GHPlayer p : _gameInstance.getPlayerManager().getOnlineGHPlayers())
            {
                if(p != attackerGHPlayer && p != victimGHPlayer)
                {
                    LazuliUI.sendMessage(p.getPlayer(), MessageFactory.getDawnCounteredBroadcastMsg(attackerGHPlayer, victimGHPlayer));
                }
            }
        }
        else
        {
            event.setCancelled(false);
            int lose = Math.max(1, (int)(victimGHPlayer.getScore() * 0.1f));

            LazuliUI.sendMessage(attackerPlayer, MessageFactory.getDawnHitMsg(victimGHPlayer, lose));

            victimGHPlayer.addScore(-lose);
            _gameInstance.getEntityManager().addEntity(new ScoreOrb(victimGHPlayer.getBodyLocation(), victimGHPlayer, attackerGHPlayer, lose));
            LazuliUI.sendMessage(victimPlayer, MessageFactory.getDawnBeenHitMsg(attackerGHPlayer, lose));

            for(GHPlayer p : _gameInstance.getPlayerManager().getOnlineGHPlayers())
            {
                if(p != attackerGHPlayer && p != victimGHPlayer)
                {
                    LazuliUI.sendMessage(p.getPlayer(), MessageFactory.getDawnHitBroadcastMsg(attackerGHPlayer, victimGHPlayer, lose));
                }
            }
        }
    }
}
