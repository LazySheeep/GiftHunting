package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SpeedBuff extends Buff
{
    public SpeedBuff(int duration)
    {
        super(duration);
    }

    @Override
    public void onApply(GHPlayer player)
    {

    }

    @Override
    public void onRemove(GHPlayer player)
    {

    }

    @Override
    protected void onTick(GHPlayer player)
    {
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 10, 0));
    }

    @Override
    public boolean tryMerge(Buff otherBuff)
    {
        if (otherBuff instanceof SpeedBuff)
        {
            this.remainingTime = Math.max(this.remainingTime, otherBuff.getRemainingTime());
            return true;
        }
        return false;
    }

    @Override
    public net.kyori.adventure.text.TextComponent getDisplayName()
    {
        return Component.text("迅捷", NamedTextColor.GREEN);
    }
}
