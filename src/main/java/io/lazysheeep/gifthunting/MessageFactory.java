package io.lazysheeep.gifthunting;

import io.lazysheeep.lazuliui.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

class MessageFactory
{
    private static final TextColor COLOR_VARIABLE = TextColor.color(125, 200, 255);
    private static final TextColor COLOR_VALUE = NamedTextColor.GREEN;
    private static final TextColor COLOR_TEXT = TextColor.color(100, 250, 200);
    private static final TextColor COLOR_CAUTION = NamedTextColor.YELLOW;
    private static final TextColor COLOR_SPECIAL = NamedTextColor.LIGHT_PURPLE;
    private static final TextColor COLOR_VITAL = NamedTextColor.RED;
    private static final TextColor COLOR_GRAY = NamedTextColor.GRAY;
    private static final TextColor COLOR_GOOD = NamedTextColor.GREEN;
    private static final TextColor COLOR_BAD = NamedTextColor.RED;

    private MessageFactory() {}

    public static String getClearAllGiftLog(int count)
    {
        return "Cleared " + count + " gifts!";
    }

    public static String getClearUntrackedGiftLog(int count)
    {
        return "Cleared " + count + " untracked gifts!";
    }

    public static String getSpawnGiftLog(int amount, Gift.GiftType type)
    {
        return "Spawned " + amount + " " + type.toString() + " gifts!";
    }

    public static Message getGameReadyingMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏将在 ", COLOR_CAUTION)
                        .append(getFormattedTime(GiftHunting.config.readyStateDuration))
                        .append(Component.text(" 后开始！", COLOR_CAUTION)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameReadyingActionbar()
    {
        int countDown = GiftHunting.config.readyStateDuration - GiftHunting.gameManager.getTimer();
        Sound tone = countDown <= 200 ? Sound.BLOCK_NOTE_BLOCK_HAT : null;
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("游戏将在 ", COLOR_TEXT)
                        .append(getFormattedTime(countDown))
                        .append(Component.text(" 后开始", COLOR_TEXT)),
                tone,
                Message.LoadMode.REPLACE,
                20
        );
    }

    public static Message getGameStartMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏正式开始！", COLOR_SPECIAL),
                Sound.ENTITY_PLAYER_LEVELUP,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameStartActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("出发吧!", COLOR_SPECIAL),
                Message.LoadMode.REPLACE,
                40
        );
    }

    public static Message getProgressingActionbarPrefix()
    {
        return new Message(
                Message.Type.ACTIONBAR_PREFIX,
                Component.text("时间: ", COLOR_VARIABLE)
                        .append(getFormattedTime(GiftHunting.gameManager.getTimer())),
                Message.LoadMode.REPLACE,
                1
        );
    }

    public static Message getProgressingActionbarSuffix(@NotNull Player player)
    {
        return new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(GiftHunting.plugin.scoreboardObj.getScore(player).getScore(), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                -1
        );
    }

    public static List<Message> getProgressingActionbarSuffixWhenScoreIncreased(@NotNull Player player, int increment)
    {
        List<Message> result = new ArrayList<>();
        result.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(GiftHunting.plugin.scoreboardObj.getScore(player).getScore(), COLOR_VALUE))
                        .append(Component.text("+" + increment, COLOR_CAUTION)),
                Message.LoadMode.REPLACE,
                20)
        );
        result.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(GiftHunting.plugin.scoreboardObj.getScore(player).getScore() + increment, COLOR_VALUE)),
                Message.LoadMode.WAIT,
                -1)
        );
        return result;
    }

    public static Message getGiftClickedActionbar(Gift gift)
    {
        int progressBarToFetchL = (int)(20*((float)gift.clicksToNextFetch/gift.clicksPerFetch));
        int progressBarToFetchR = 20 - progressBarToFetchL;
        int progressBarTotalL = gift.remainingCapacity - 1;
        int progressBarTotalR = gift.capacityInFetches - gift.remainingCapacity;
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("开启中 ", COLOR_TEXT)
                        .append(Component.text("*".repeat(progressBarTotalL), COLOR_SPECIAL))
                        .append(Component.text("[", COLOR_SPECIAL))
                        .append(Component.text("|".repeat(progressBarToFetchL), COLOR_VALUE))
                        .append(Component.text("|".repeat(progressBarToFetchR), COLOR_GRAY))
                        .append(Component.text("]", COLOR_SPECIAL))
                        .append(Component.text("*".repeat(progressBarTotalR), COLOR_GRAY)),
                Message.LoadMode.REPLACE,
                10
        );
    }

    public static Message getGiftFetchedActionbar(Gift gift)
    {
        int progressBarTotalL = gift.remainingCapacity - 1;
        int progressBarTotalR = gift.capacityInFetches - gift.remainingCapacity;
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("已开启 ", COLOR_CAUTION)
                        .append(Component.text("*".repeat(progressBarTotalL), COLOR_SPECIAL))
                        .append(Component.text("[", COLOR_GRAY))
                        .append(Component.text("|".repeat(20), COLOR_GRAY))
                        .append(Component.text("]", COLOR_GRAY))
                        .append(Component.text("*".repeat(progressBarTotalR), COLOR_GRAY)),
                Message.LoadMode.REPLACE,
                20
        );
    }

    public static Message getGameFinishedMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏结束！", COLOR_SPECIAL),
                Sound.ENTITY_ENDER_DRAGON_DEATH,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameFinishedActionbarInfix()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("游戏结束", COLOR_SPECIAL),
                Message.LoadMode.REPLACE,
                GiftHunting.config.finishedStateDuration
        );
    }

    public static Message getGameFinishedActionbarSuffix(@NotNull Player player)
    {
        return new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("最终得分: ", COLOR_SPECIAL)
                        .append(Component.text(GiftHunting.plugin.scoreboardObj.getScore(player).getScore(), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                GiftHunting.config.finishedStateDuration
        );
    }

    public static Message getGamePausedMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("管理员暂停了游戏！", COLOR_VITAL),
                Sound.ENTITY_VILLAGER_NO,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGamePausedActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("游戏已暂停", COLOR_VITAL),
                Message.LoadMode.REPLACE,
                -1
        );
    }

    public static Message getGameUnpauseMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏继续进行！", COLOR_GOOD),
                Sound.ENTITY_VILLAGER_YES,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameUnPauseActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("游戏继续", COLOR_GOOD),
                Message.LoadMode.REPLACE,
                40
        );
    }

    public static Message getGameTerminatedMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("管理员中止了游戏！", COLOR_BAD),
                Sound.ENTITY_VILLAGER_HURT,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getAddGiftSpawnerActionbar(Location location)
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("New Gift Spawner Added: \n", COLOR_TEXT)
                        .append(Component.text(location.toVector().toString(), COLOR_VALUE)),
                Sound.BLOCK_BEACON_ACTIVATE,
                Message.LoadMode.REPLACE,
                30
        );
    }

    public static Message getRemoveGiftSpawnerActionbar(Location location)
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("Gift Spawner Removed: \n", COLOR_TEXT)
                        .append(Component.text(location.toVector().toString(), COLOR_VALUE)),
                Sound.BLOCK_BEACON_DEACTIVATE,
                Message.LoadMode.REPLACE,
                30
        );
    }

    public static TextComponent getGameStatsText()
    {
        return Component.text("state: ", COLOR_VARIABLE).append(Component.text(GiftHunting.gameManager.getState().toString(), COLOR_VALUE))
                .append(Component.text("\ntimer: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.gameManager.getTimer(), COLOR_VALUE))
                .append(Component.text("\ngiftSpawners: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.config.getGiftSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\ntrackedGifts: ", COLOR_VARIABLE)).append(Component.text(Gift.getGiftCount(), COLOR_VALUE));
    }

    public static TextComponent getEventCantStartText()
    {
        return Component.text("The game has already begun!", COLOR_VITAL);
    }

    public static TextComponent getEventCantEndText()
    {
        return Component.text("The game is not in progress!", COLOR_VITAL);
    }

    public static TextComponent getEventCantPauseText()
    {
        return Component.text("The game is not in progress!", COLOR_VITAL);
    }

    public static TextComponent getEventCantUnpauseText()
    {
        return Component.text("The game is not in pause!", COLOR_VITAL);
    }

    private static TextComponent getFormattedTime(int time)
    {
        int mm = (time/20) / 60;
        int ss = (time/20) % 60;
        return Component.text(String.format("%02d:%02d", mm, ss), COLOR_VALUE);
    }
}
