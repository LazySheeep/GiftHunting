package io.lazysheeep.mczju.christmas;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;

public class MessageFactory
{
    private MessageFactory() {}

    public static Component getClearAllGiftMsg(int count)
    {
        return Component.text("Cleared " + count + " gifts!");
    }

    public static Component getClearUntrackedGiftMsg(int count)
    {
        return Component.text("Cleared " + count + " untracked gifts!");
    }

    public static Component getSpawnGiftMsg(int amount, Gift.GiftType type)
    {
        return Component.text("Spawned " + amount + " " + type.toString() + " gifts!");
    }

    public static Component getGiftClickedActionbarMsg(int clicksToNextFetch, int clicksPerFetch, int capacityInFetches)
    {
        Component msg = Component.text("开启中>>>", NamedTextColor.AQUA);
        msg = msg.append(Component.text(" [", NamedTextColor.YELLOW));
        int pp = (int)(20*((float)clicksToNextFetch/clicksPerFetch));
        msg = msg.append(Component.text("|".repeat(pp), NamedTextColor.GREEN));
        msg = msg.append(Component.text("|".repeat(20-pp), NamedTextColor.GRAY));
        msg = msg.append(Component.text("] ", NamedTextColor.YELLOW));
        msg = msg.append(Component.text("[*]".repeat(capacityInFetches-1), NamedTextColor.YELLOW));
        return msg;
    }

    public static Component getGiftFetchedActionbarMsg(int scorePerFetch)
    {
        return Component.text("开启礼物 分数+" + scorePerFetch);
    }

    public static Component getEventCountDownActionbarMsg()
    {
        return Component.text("距活动开始还有：" + (Christmas.plugin.cfg.readyStateDuration-Christmas.plugin.eventStats.timer)/20 + " 秒");
    }

    public static Component getEventStatsMsg()
    {
        String msg = "";
        msg += "state: " + Christmas.plugin.eventStats.state.toString() + "\n";
        msg += "timer: " + Christmas.plugin.eventStats.timer + "\n";
        msg += "giftSpawners: " + Christmas.plugin.cfg.giftSpawnerLocations.size() + "\n";
        msg += "trackedGifts: " + Gift.getNumber() + "\n";
        return Component.text(msg);
    }

    public static Component getAddGiftSpawnerMsg(Location location)
    {
        return Component.text("New Gift Spawner Added!\n" + location.toString());
    }

    public static Component getEventCantStartMsg()
    {
        return Component.text("The event has already begun!");
    }

    public static Component getEventCantEndMsg()
    {
        return Component.text("The event is not in progress!");
    }

}
