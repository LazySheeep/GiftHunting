package io.lazysheeep.gifthunting.skills;

import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.player.GHPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class Skill
{
    protected final GHPlayer host;
    protected final CustomItem itemType;
    protected final int maxCharges;
    protected final int cooldownTicks;
    protected final int aftercastTicks;

    protected int charges;
    protected int aftercastLeft;
    protected int cooldownLeft;

    private boolean overlayAftercastApplied;
    private boolean overlayCooldownApplied;

    protected Skill(GHPlayer host, CustomItem itemType, int maxCharges, int cooldownTicks, int aftercastTicks)
    {
        this.host = host;
        this.itemType = itemType;
        this.maxCharges = maxCharges;
        this.cooldownTicks = cooldownTicks;
        this.aftercastTicks = aftercastTicks;
        this.charges = maxCharges;
        this.aftercastLeft = 0;
        this.cooldownLeft = 0;
        this.overlayAftercastApplied = false;
        this.overlayCooldownApplied = false;
    }

    public CustomItem getItemType()
    {
        return itemType;
    }

    public boolean tryUse()
    {
        if(charges <= 0 || aftercastLeft > 0)
        {
            return false;
        }
        if(aftercastTicks > 0)
        {
            aftercastLeft = aftercastTicks;
            overlayAftercastApplied = false;
        }
        else
        {
            charges --;
            if(charges < 0) charges = 0;
            cooldownLeft = cooldownTicks;
            overlayCooldownApplied = false;
        }
        return true;
    }

    protected void ensureItemPresent()
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

    protected void syncItemAmount()
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
            int amount = Math.max(1, Math.min(64, charges == 0 ? 1 : charges));
            found.setAmount(amount);
        }
    }

    private void applyAftercastOverlayIfNeeded()
    {
        if(aftercastLeft > 0 && !overlayAftercastApplied)
        {
            int ticks = Math.max(1, aftercastTicks);
            host.getPlayer().setCooldown(itemType.material, ticks);
            overlayAftercastApplied = true;
            overlayCooldownApplied = false;
        }
    }

    private void applyCooldownOverlayIfNeeded()
    {
        if(cooldownLeft > 0 && !overlayCooldownApplied)
        {
            if(charges == 0)
            {
                host.getPlayer().setCooldown(itemType.material, cooldownTicks);
            }
            overlayCooldownApplied = true;
            overlayAftercastApplied = false;
        }
    }

    private void clearOverlayIfIdle()
    {
        if(aftercastLeft <= 0 && cooldownLeft <= 0)
        {
            host.getPlayer().setCooldown(itemType.material, 0);
            overlayAftercastApplied = false;
            overlayCooldownApplied = false;
        }
    }

    public void tick()
    {
        ensureItemPresent();
        if(aftercastLeft > 0)
        {
            applyAftercastOverlayIfNeeded();
            aftercastLeft --;
            if(aftercastLeft <= 0)
            {
                charges --;
                if(charges < 0) charges = 0;
                cooldownLeft = cooldownTicks;
                overlayCooldownApplied = false;
            }
        }
        else if(cooldownLeft > 0)
        {
            applyCooldownOverlayIfNeeded();
            cooldownLeft --;
            if(cooldownLeft <= 0)
            {
                if(charges < maxCharges)
                {
                    charges ++;
                }
                if(charges < maxCharges)
                {
                    cooldownLeft = cooldownTicks;
                    overlayCooldownApplied = false;
                }
            }
        }
        else
        {
            clearOverlayIfIdle();
        }

        syncItemAmount();
    }

    public abstract void onUse();
}
