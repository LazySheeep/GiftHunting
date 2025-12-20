package io.lazysheeep.gifthunting.skills;

import io.lazysheeep.gifthunting.buffs.CounteringBuff;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.player.GHPlayer;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public enum Skill
{
    COUNTER(CustomItem.SKILL_COUNTER, 1, 200, 20)
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
    };

    private final CustomItem itemType;
    private final int maxCharges;
    private final int cooldownDuration;
    private final int aftercastDuration;

    Skill(CustomItem itemType, int maxCharges, int cooldownDuration, int aftercastDuration)
    {
        this.itemType = itemType;
        this.maxCharges = maxCharges;
        this.cooldownDuration = cooldownDuration;
        this.aftercastDuration = aftercastDuration;
    }

    public abstract void onUse(GHPlayer host, SkillState skillState);

    public SkillState createDefaultState()
    {
        return new SkillState(maxCharges);
    }

    public boolean tryUse(GHPlayer host, SkillState skillState)
    {
        if(skillState.charges <= 0 || skillState.aftercastTimer > 0)
        {
            return false;
        }
        if(aftercastDuration > 0)
        {
            skillState.aftercastTimer = aftercastDuration;
            skillState.overlayAftercastApplied = false;
        }
        else
        {
            skillState.charges--;
            if(skillState.cooldownTimer <= 0) skillState.cooldownTimer = cooldownDuration;
            skillState.overlayCooldownApplied = false;
        }
        onUse(host, skillState);
        return true;
    }

    private void ensureItemPresent(GHPlayer host)
    {
        Player p = host.getPlayer();
        boolean has = false;
        for(ItemStack it : p.getInventory())
        {
            if(CustomItem.checkItem(it) == itemType)
            {
                has = true;
                break;
            }
        }
        if(!has)
        {
            p.getInventory().addItem(itemType.create());
        }
    }

    private void syncItemAmount(GHPlayer host, SkillState skillState)
    {
        Player p = host.getPlayer();
        ItemStack found = null;
        for(ItemStack it : p.getInventory())
        {
            if(CustomItem.checkItem(it) == itemType)
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
        if(skillState.aftercastTimer > 0 && !skillState.overlayAftercastApplied)
        {
            int ticks = Math.max(1, aftercastDuration);
            host.getPlayer().setCooldown(itemType.material, ticks);
            skillState.overlayAftercastApplied = true;
            skillState.overlayCooldownApplied = false;
        }
    }

    private void applyCooldownOverlayIfNeeded(GHPlayer host, SkillState skillState)
    {
        if(skillState.cooldownTimer > 0 && !skillState.overlayCooldownApplied)
        {
            if(skillState.charges == 0)
            {
                int remaining = Math.max(1, skillState.cooldownTimer);
                host.getPlayer().setCooldown(itemType.material, remaining);
            }
            skillState.overlayCooldownApplied = true;
            skillState.overlayAftercastApplied = false;
        }
    }

    private void clearOverlayIfIdle(GHPlayer host, SkillState skillState)
    {
        if(skillState.aftercastTimer <= 0 && skillState.cooldownTimer <= 0)
        {
            host.getPlayer().setCooldown(itemType.material, 0);
            skillState.overlayAftercastApplied = false;
            skillState.overlayCooldownApplied = false;
        }
    }

    public void tick(GHPlayer host, SkillState skillState)
    {
        ensureItemPresent(host);
        if(skillState.aftercastTimer > 0)
        {
            applyAftercastOverlayIfNeeded(host, skillState);
            skillState.aftercastTimer--;
            if(skillState.aftercastTimer <= 0)
            {
                skillState.charges--;
                if(skillState.charges < 0) skillState.charges = 0;
                if(skillState.cooldownTimer <= 0) skillState.cooldownTimer = cooldownDuration;
                skillState.overlayCooldownApplied = false;
            }
        }
        else if(skillState.cooldownTimer > 0)
        {
            applyCooldownOverlayIfNeeded(host, skillState);
            skillState.cooldownTimer--;
            if(skillState.cooldownTimer <= 0)
            {
                if(skillState.charges < maxCharges)
                {
                    skillState.charges++;
                }
                if(skillState.charges < maxCharges)
                {
                    skillState.cooldownTimer = cooldownDuration;
                    skillState.overlayCooldownApplied = false;
                }
            }
        }
        else
        {
            clearOverlayIfIdle(host, skillState);
        }

        syncItemAmount(host, skillState);

        ItemStack itemInMainHand = host.getPlayer().getInventory().getItemInMainHand();
        if(CustomItem.checkItem(itemInMainHand) == itemType)
        {
            var msg = io.lazysheeep.gifthunting.factory.MessageFactory.getSkillCooldownActionbar(
                skillState.charges,
                maxCharges,
                skillState.cooldownTimer,
                cooldownDuration,
                skillState.aftercastTimer,
                aftercastDuration);
            io.lazysheeep.lazuliui.LazuliUI.sendMessage(host.getPlayer(), msg);
        }
    }
}
