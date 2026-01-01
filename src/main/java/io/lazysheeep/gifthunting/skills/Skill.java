package io.lazysheeep.gifthunting.skills;

import io.lazysheeep.gifthunting.buffs.CounteringBuff;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.lazuliui.LazuliUI;
import io.lazysheeep.lazuliui.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.bukkit.inventory.meta.CompassMeta;
import org.jetbrains.annotations.Nullable;

public enum Skill
{
    COUNTER(CustomItem.SKILL_COUNTER, 1, 300, 20)
    {
        @Override
        public void onUse(GHPlayer host, SkillState skillState)
        {
            host.addBuff(new CounteringBuff(20));
            host.getPlayer().playSound(host.getPlayer().getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    },
    BOOST(CustomItem.SKILL_BOOSTER, 3, 200, 0)
    {
        @Override
        public void onUse(GHPlayer host, SkillState skillState)
        {
            var player = host.getPlayer();
            player.setVelocity(player.getVelocity()
                .add(player.getLocation().getDirection().multiply(1.5f))
                .add(new Vector(0.0f, 0.2f, 0.0f)));
            player.getWorld().playSound(player, Sound.ITEM_FIRECHARGE_USE, SoundCategory.MASTER, 1.0f, 1.0f);
            player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 2, 0.2f, 0.2f, 0.2f, 0.5f);
        }
    },
    DAWN(CustomItem.SKILL_DAWN_BOW, 1, 1200, 0)
    {
        @Override
        public void onUse(GHPlayer host, SkillState skillState)
        {
            host.updateSlot();
        }

        @Override
        protected void onChargeGained(GHPlayer host, SkillState state)
        {
            MCUtil.GiveItem(host.getPlayer(), CustomItem.SKILL_DAWN_ARROW.create());
        }

        @Override
        public void onDisable(GHPlayer host, SkillState skillState)
        {
            super.onDisable(host, skillState);
            MCUtil.RemoveItem(host.getPlayer(), CustomItem.SKILL_DAWN_ARROW);
        }
    },
    DETECT(CustomItem.SKILL_DETECT, 1, 2400, 600)
    {
        @Override
        public void onUse(GHPlayer host, SkillState skillState) { }

        @Override
        protected void tickActive(GHPlayer host, SkillState skillState)
        {
            var player = host.getPlayer();
            var gm = host.getGameInstance().getGiftManager();
            var special = gm.getSpecialGift();
            if(special != null)
            {
                setDetectCompassTarget(player, special.getLocation());
                LazuliUI.sendMessage(player, MessageFactory.getDetectActionbar(true, (int) player.getLocation().distance(special.getLocation())));
            }
            else
            {
                double minDist = Double.MAX_VALUE;
                Location nearest = null;
                for(var g : gm.getNormalGifts())
                {
                    double d = player.getLocation().distance(g.getLocation());
                    if(d < minDist)
                    {
                        minDist = d;
                        nearest = g.getLocation();
                    }
                }
                if(nearest != null)
                {
                    setDetectCompassTarget(player, nearest);
                    LazuliUI.sendMessage(player, MessageFactory.getDetectActionbar(false, (int) minDist));
                }
            }
        }

        @Override
        protected void onActiveEnd(GHPlayer host, SkillState state)
        {
            setDetectCompassTarget(host.getPlayer(), null);
        }
    };

    public final CustomItem skillItem;
    public final int maxCharges;
    public final int cooldownDuration;
    public final int activeDuration;

    Skill(CustomItem skillItem, int maxCharges, int cooldownDuration, int activeDuration)
    {
        this.skillItem = skillItem;
        this.maxCharges = maxCharges;
        this.cooldownDuration = cooldownDuration;
        this.activeDuration = activeDuration;
    }

    public abstract void onUse(GHPlayer host, SkillState skillState);

    public SkillState createDefaultState()
    {
        return new SkillState();
    }

    public void onEnable(GHPlayer host, SkillState skillState)
    {
        if(skillState.charges > 0)
        {
            onChargeGained(host, skillState);
        }
        if(skillState.charges < maxCharges && skillState.cooldownTimer == -1)
        {
            skillState.cooldownTimer = 0;
            skillState.overlayCooldownApplied = false;
        }
    }

    public void onDisable(GHPlayer host, SkillState skillState)
    {
        MCUtil.RemoveItem(host.getPlayer(), skillItem);
        skillState.overlayCooldownApplied = false;
        skillState.overlayAftercastApplied = false;
    }

    public boolean tryUse(GHPlayer host, SkillState skillState)
    {
        if(!skillState.enabled) return false;
        if(skillState.charges <= 0 || skillState.activeTimer >= 0) return false;
        if(activeDuration > 0)
        {
            skillState.activeTimer = 0;
            skillState.overlayAftercastApplied = false;
        }
        else
        {
            skillState.charges--;
            if(skillState.cooldownTimer == -1)
            {
                skillState.cooldownTimer = 0;
                skillState.overlayCooldownApplied = false;
                applyCooldownOverlayIfNeeded(host, skillState);
            }
        }
        onUse(host, skillState);
        return true;
    }

    protected void onChargeGained(GHPlayer host, SkillState state) { }

    protected void tickActive(GHPlayer host, SkillState state) { }

    protected void onActiveEnd(GHPlayer host, SkillState state) { }

    public void tick(GHPlayer host, SkillState skillState)
    {
        if(!skillState.enabled) return;

        ensureItemPresent(host);

        if(activeDuration > 0 && skillState.activeTimer >= 0)
        {
            applyAftercastOverlayIfNeeded(host, skillState);
            tickActive(host, skillState);
            skillState.activeTimer++;
            if(skillState.activeTimer >= activeDuration)
            {
                onActiveEnd(host, skillState);
                skillState.activeTimer = -1;
                skillState.overlayAftercastApplied = false;
                skillState.charges--;
                if(skillState.charges < 0) skillState.charges = 0;
                if(skillState.charges < maxCharges && skillState.cooldownTimer == -1)
                {
                    skillState.cooldownTimer = 0;
                    skillState.overlayCooldownApplied = false;
                }
            }
        }
        else
        {
            if(skillState.charges < maxCharges)
            {
                if(skillState.cooldownTimer == -1)
                {
                    skillState.cooldownTimer = 0;
                    skillState.overlayCooldownApplied = false;
                }
                else
                {
                    applyCooldownOverlayIfNeeded(host, skillState);
                    skillState.cooldownTimer++;
                    if(skillState.cooldownTimer >= cooldownDuration)
                    {
                        skillState.cooldownTimer = -1;
                        skillState.overlayCooldownApplied = false;
                        if(skillState.charges < maxCharges)
                        {
                            skillState.charges++;
                            onChargeGained(host, skillState);
                            if(skillState.charges < maxCharges)
                            {
                                skillState.cooldownTimer = 0;
                                skillState.overlayCooldownApplied = false;
                            }
                        }
                    }
                }
            }
            else
            {
                skillState.cooldownTimer = -1;
                clearOverlayIfIdle(host, skillState);
            }
        }

        syncItemAmount(host, skillState);

        ItemStack itemInMainHand = host.getPlayer().getInventory().getItemInMainHand();
        if(CustomItem.checkItem(itemInMainHand) == skillItem)
        {
            Message msg = MessageFactory.getSkillCooldownActionbar(
                skillState.charges,
                maxCharges,
                skillState.cooldownTimer,
                cooldownDuration,
                skillState.activeTimer,
                activeDuration);
            LazuliUI.sendMessage(host.getPlayer(), msg);
        }
    }

    private void ensureItemPresent(GHPlayer host)
    {
        Player p = host.getPlayer();
        boolean has = false;
        for(ItemStack it : p.getInventory())
        {
            if(CustomItem.checkItem(it) == skillItem)
            {
                has = true;
                break;
            }
        }
        if(!has)
        {
            MCUtil.GiveItem(p, skillItem.create());
        }
    }

    private void syncItemAmount(GHPlayer host, SkillState skillState)
    {
        Player p = host.getPlayer();
        ItemStack found = null;
        for(ItemStack it : p.getInventory())
        {
            if(CustomItem.checkItem(it) == skillItem)
            {
                found = it;
                break;
            }
        }
        if(found != null)
        {
            int amount = Math.max(1, Math.min(64, skillState.charges == 0 ? 1 : skillState.charges));
            found.setAmount(amount);
        }
    }

    private void applyAftercastOverlayIfNeeded(GHPlayer host, SkillState skillState)
    {
        if(skillState.activeTimer >= 0 && !skillState.overlayAftercastApplied)
        {
            int remaining = Math.max(1, activeDuration - skillState.activeTimer);
            host.getPlayer().setCooldown(skillItem.material, remaining);
            skillState.overlayAftercastApplied = true;
            skillState.overlayCooldownApplied = false;
        }
    }

    private void applyCooldownOverlayIfNeeded(GHPlayer host, SkillState skillState)
    {
        if(skillState.cooldownTimer >= 0 && !skillState.overlayCooldownApplied)
        {
            if(skillState.charges == 0)
            {
                int remaining = Math.max(1, cooldownDuration - skillState.cooldownTimer);
                host.getPlayer().setCooldown(skillItem.material, remaining);
                skillState.overlayCooldownApplied = true;
                skillState.overlayAftercastApplied = false;
            }
        }
    }

    private void clearOverlayIfIdle(GHPlayer host, SkillState skillState)
    {
        if(skillState.activeTimer < 0 && skillState.cooldownTimer < 0)
        {
            host.getPlayer().setCooldown(skillItem.material, 0);
            skillState.overlayAftercastApplied = false;
            skillState.overlayCooldownApplied = false;
        }
    }

    private static void setDetectCompassTarget(Player player, @Nullable Location location)
    {
        var inv = player.getInventory();
        for(int i = 0; i < inv.getSize(); i++)
        {
            ItemStack it = inv.getItem(i);
            if(CustomItem.checkItem(it) == CustomItem.SKILL_DETECT)
            {
                it.editMeta(m -> {
                    if(m instanceof CompassMeta cm)
                    {
                        cm.setLodestoneTracked(false);
                        cm.setLodestone(location);
                    }
                });
                inv.setItem(i, it);
                break;
            }
        }
    }
}
