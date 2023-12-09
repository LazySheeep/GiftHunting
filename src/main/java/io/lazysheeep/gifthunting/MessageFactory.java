package io.lazysheeep.gifthunting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

public class MessageFactory
{
    private MessageFactory() {}

    public static TextComponent getClearAllGiftMsg(int count)
    {
        return Component.text("Cleared " + count + " gifts!");
    }

    public static TextComponent getClearUntrackedGiftMsg(int count)
    {
        return Component.text("Cleared " + count + " untracked gifts!");
    }

    public static TextComponent getSpawnGiftMsg(int amount, Gift.GiftType type)
    {
        return Component.text("Spawned " + amount + " " + type.toString() + " gifts!");
    }

    public static TextComponent getGiftClickedActionbarMsg(Gift gift)
    {
        int pp = (int)(20*((float)gift.clicksToNextFetch/gift.clicksPerFetch));
        return Component.text("开启中>>>", NamedTextColor.AQUA)
            .append(Component.text(" [", NamedTextColor.YELLOW))
            .append(Component.text("|".repeat(pp), NamedTextColor.GREEN))
            .append(Component.text("|".repeat(20-pp), NamedTextColor.GRAY))
            .append(Component.text("] ", NamedTextColor.YELLOW))
            .append(Component.text("[*]".repeat(gift.capacityInFetches-1), NamedTextColor.YELLOW));
    }

    public static TextComponent getGiftFetchedActionbarMsg(int scorePerFetch)
    {
        return Component.text("开启礼物 分数+" + scorePerFetch);
    }

    public static TextComponent getTimerActionbarMsg()
    {
        int mm = (GiftHunting.plugin.eventStats.timer/20) / 60;
        int ss = (GiftHunting.plugin.eventStats.timer/20) % 60;
        return Component.text(String.format("Time %02d:%02d", mm, ss), NamedTextColor.YELLOW);
    }

    public static TextComponent getScoreActionbarMsg(int score)
    {
        return Component.text("Score: ", NamedTextColor.AQUA)
            .append(Component.text(score, NamedTextColor.YELLOW));
    }

    public static TextComponent getScoreIncreasedActionbarMsg(int prevScore, int increment)
    {
        return getScoreActionbarMsg(prevScore).append(Component.text("+" + increment, NamedTextColor.LIGHT_PURPLE));
    }

    public static TextComponent getEventCountDownActionbarMsg()
    {
        return Component.text("距活动开始还有: " + (GiftHunting.plugin.config.readyStateDuration- GiftHunting.plugin.eventStats.timer)/20 + " 秒");
    }

    public static TextComponent getEventStatsMsg()
    {
        String msg = "";
        msg += "state: " + GiftHunting.plugin.eventStats.state.toString() + "\n";
        msg += "timer: " + GiftHunting.plugin.eventStats.timer + "\n";
        msg += "giftSpawners: " + GiftHunting.plugin.config.getGiftSpawnerLocations().size() + "\n";
        msg += "trackedGifts: " + Gift.getNumber() + "\n";
        return Component.text(msg);
    }

    public static TextComponent getAddGiftSpawnerMsg(Location location)
    {
        return Component.text("New Gift Spawner Added!\n" + location.toString());
    }

    public static TextComponent getEventCantStartMsg()
    {
        return Component.text("The event has already begun!");
    }

    public static TextComponent getEventCantEndMsg()
    {
        return Component.text("The event is not in progress!");
    }

}
