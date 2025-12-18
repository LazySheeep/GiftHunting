package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
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
}
