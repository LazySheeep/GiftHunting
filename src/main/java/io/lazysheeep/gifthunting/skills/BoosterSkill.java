package io.lazysheeep.gifthunting.skills;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.player.GHPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.util.Vector;

public class BoosterSkill extends Skill
{
    public BoosterSkill(GHPlayer host)
    {
        super(host, CustomItem.BOOSTER, 3, 200, 0);
    }

    @Override
    public void onUse()
    {
        if(tryUse())
        {
            var player = host.getPlayer();
            player.setVelocity(player.getVelocity()
                                     .add(player.getLocation().getDirection().multiply(1.5f))
                                     .add(new Vector(0.0f, 0.2f, 0.0f)));
            player.getWorld().playSound(player, Sound.ITEM_FIRECHARGE_USE, SoundCategory.MASTER, 1.0f, 1.0f);
            player.getWorld().spawnParticle(Particle.EXPLOSION, player.getLocation(), 2, 0.2f, 0.2f, 0.2f, 0.5f);
        }
    }
}
