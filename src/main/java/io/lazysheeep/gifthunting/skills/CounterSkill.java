package io.lazysheeep.gifthunting.skills;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.buffs.CounteringBuff;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.player.GHPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class CounterSkill extends Skill
{
    public CounterSkill(GHPlayer host)
    {
        super(host, CustomItem.COUNTER, 1, 200, 40);
    }

    @Override
    public void onUse()
    {
        if(tryUse())
        {
            host.addBuff(new CounteringBuff(aftercastTicks));
            host.getPlayer().playSound(host.getPlayer().getLocation(), Sound.BLOCK_ANVIL_USE, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }
}
