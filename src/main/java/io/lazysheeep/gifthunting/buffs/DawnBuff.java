package io.lazysheeep.gifthunting.buffs;

import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

public class DawnBuff extends Buff
{
    public DawnBuff()
    {
        super();
    }

    public DawnBuff(int duration)
    {
        super(duration);
    }

    @Override
    public void onApply(GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().setGlowing(true);
        ghPlayer.getPlayer().getWorld().playSound(ghPlayer.getPlayer(), Sound.BLOCK_BEACON_ACTIVATE, SoundCategory.MASTER, 1f, 1f);
    }

    @Override
    public void onRemove(GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().setGlowing(false);
        ghPlayer.getPlayer().getWorld().playSound(ghPlayer.getPlayer(), Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.MASTER, 1f, 1f);
    }

    @Override
    protected void onTick(GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().setGlowing(true);
    }

    @Override
    public TextComponent getDisplayName()
    {
        return Component.text("决胜", NamedTextColor.LIGHT_PURPLE);
    }

    @Override
    public String getDescription()
    {
        return "得分达到胜利分数80%时获得, 玩家将获得发光效果, 并自动识破偷窃和束缚";
    }
}
