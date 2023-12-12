package io.lazysheeep.gifthunting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Location;

class MessageFactory
{
    private static final TextColor COLOR_VARIABLE = TextColor.color(125, 200, 255);
    private static final TextColor COLOR_VALUE = NamedTextColor.GREEN;
    private static final TextColor COLOR_TEXT = TextColor.color(200, 210, 160);
    private static final TextColor COLOR_CAUTION = NamedTextColor.YELLOW;
    private static final TextColor COLOR_SPECIAL = NamedTextColor.LIGHT_PURPLE;
    private static final TextColor COLOR_VITAL = NamedTextColor.RED;
    private static final TextColor COLOR_GRAY = NamedTextColor.GRAY;

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

    private static TextComponent getFormattedTime(int time)
    {
        int mm = (time/20) / 60;
        int ss = (time/20) % 60;
        return Component.text(String.format("%02d:%02d", mm, ss), COLOR_VALUE);
    }

    public static TextComponent getGameReadyingActionbar()
    {
        return Component.text("游戏将在 ", COLOR_TEXT)
                .append(getFormattedTime(GiftHunting.config.readyStateDuration-GiftHunting.gameManager.getTimer()))
                .append(Component.text(" 后开始", COLOR_TEXT));
    }

    public static TextComponent getGameStartActionbar()
    {
        return Component.text("出发吧！", COLOR_SPECIAL);
    }

    public static TextComponent getProgressingActionbarPrefix()
    {
        return Component.text("Time: ", COLOR_VARIABLE)
                .append(getFormattedTime(GiftHunting.gameManager.getTimer()));
    }

    public static TextComponent getGiftClickedActionbar(Gift gift)
    {
        int pp = (int)(20*((float)gift.clicksToNextFetch/gift.clicksPerFetch));
        return Component.text("开启中>>> ", COLOR_TEXT)
                .append(Component.text("[]".repeat(gift.capacityInFetches-1), COLOR_SPECIAL))
                .append(Component.text(" [", COLOR_VARIABLE))
                .append(Component.text("|".repeat(pp), NamedTextColor.GREEN))
                .append(Component.text("|".repeat(20-pp), COLOR_GRAY))
                .append(Component.text("] ", COLOR_VARIABLE));
    }

    public static TextComponent getProgressingActionbarSuffix(int score)
    {
        return Component.text("Score: ", COLOR_VARIABLE)
            .append(Component.text(score, COLOR_VALUE));
    }

    public static TextComponent getProgressingActionbarSuffixWhenScoreIncreased(int prevScore, int increment)
    {
        return getProgressingActionbarSuffix(prevScore).append(Component.text("+" + increment, COLOR_CAUTION));
    }

    public static TextComponent getGamePausedActionbar()
    {
        return Component.text("游戏已暂停", COLOR_CAUTION);
    }

    public static TextComponent getGameStatsMsg()
    {
        return Component.text("state: ", COLOR_VARIABLE)
                .append(Component.text(GiftHunting.gameManager.getState().toString(), COLOR_VALUE))
                .append(Component.text("\ntimer: ", COLOR_VARIABLE))
                .append(Component.text(GiftHunting.gameManager.getTimer(), COLOR_VALUE))
                .append(Component.text("\ngiftSpawners: ", COLOR_VARIABLE))
                .append(Component.text(GiftHunting.config.getGiftSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\ntrackedGifts: ", COLOR_VARIABLE))
                .append(Component.text(Gift.getGiftCount(), COLOR_VALUE));
    }

    public static TextComponent getAddGiftSpawnerMsg(Location location)
    {
        return Component.text("New Gift Spawner Added: \n", COLOR_TEXT)
                .append(Component.text(location.toVector().toString(), COLOR_VALUE));
    }

    public static TextComponent getRemoveGiftSpawnerMsg(Location location)
    {
        return Component.text("Gift Spawner Removed: \n", COLOR_TEXT)
                .append(Component.text(location.toVector().toString(), COLOR_VALUE));
    }

    public static TextComponent getGameReadyingMsg()
    {
        return Component.text("游戏将在 ", COLOR_CAUTION)
                .append(getFormattedTime(GiftHunting.config.readyStateDuration))
                .append(Component.text(" 后开始！", COLOR_CAUTION));
    }

    public static TextComponent getGameStartMsg()
    {
        return Component.text("游戏正式开始！", COLOR_SPECIAL);
    }

    public static TextComponent getGameFinishedMsg()
    {
        return Component.text("游戏结束！", COLOR_SPECIAL);
    }

    public static TextComponent getGameTerminatedMsg()
    {
        return Component.text("游戏被中止！", COLOR_VITAL);
    }

    public static TextComponent getGamePausedMsg()
    {
        return Component.text("管理员暂停了游戏！", COLOR_VITAL);
    }

    public static TextComponent getGameUnpauseMsg()
    {
        return Component.text("游戏继续进行！", COLOR_VITAL);
    }

    public static TextComponent getEventCantStartMsg()
    {
        return Component.text("The game has already begun!", COLOR_VITAL);
    }

    public static TextComponent getEventCantEndMsg()
    {
        return Component.text("The game is not in progress!", COLOR_VITAL);
    }

    public static TextComponent getEventCantPauseMsg()
    {
        return Component.text("The game is not in progress!", COLOR_VITAL);
    }

    public static TextComponent getEventCantUnpauseMsg()
    {
        return Component.text("The game is not in pause!", COLOR_VITAL);
    }

}
