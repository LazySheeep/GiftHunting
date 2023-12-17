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
    private static final TextColor COLOR_VARIABLE = NamedTextColor.AQUA;
    private static final TextColor COLOR_VALUE = NamedTextColor.GREEN;
    private static final TextColor COLOR_TEXT = TextColor.color(205,144,105);
    private static final TextColor COLOR_CAUTION = NamedTextColor.YELLOW;
    private static final TextColor COLOR_SPECIAL = NamedTextColor.LIGHT_PURPLE;
    private static final TextColor COLOR_VITAL = NamedTextColor.RED;
    private static final TextColor COLOR_GRAY = NamedTextColor.GRAY;
    private static final TextColor COLOR_GOOD = NamedTextColor.GREEN;
    private static final TextColor COLOR_BAD = NamedTextColor.RED;

    private MessageFactory() {}

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

    public static List<Message> getGameIntroMsg()
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("欢迎！", COLOR_TEXT),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.REPLACE,
                60
        ));
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("游戏规则介绍", COLOR_TEXT),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                100
        ));
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("礼物生成详情", COLOR_TEXT),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                100
        ));
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("道具介绍", COLOR_TEXT),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                100
        ));
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("游戏即将开始", COLOR_TEXT),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                1
        ));
        return messages;
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
                        .append(Component.text(GiftHunting.gameManager.getScore(player), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                -1
        );
    }

    public static List<Message> getProgressingActionbarSuffixWhenScoreChanged(@NotNull Player player, int score)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(GiftHunting.gameManager.getScore(player), COLOR_VALUE))
                        .append(score >= 0 ? Component.text("+" + score, COLOR_CAUTION) : Component.text(score, COLOR_BAD)),
                Message.LoadMode.REPLACE,
                30)
        );
        messages.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(GiftHunting.gameManager.getScore(player) + score, COLOR_VALUE)),
                Message.LoadMode.WAIT,
                -1)
        );
        return messages;
    }

    public static Message getDeliverGiftMsg(Gift.GiftType type)
    {
        if(type == Gift.GiftType.NORMAL)
            return new Message(
                    Message.Type.CHAT,
                    Component.text("一波礼物已送达！", COLOR_GOOD),
                    Sound.ENTITY_PLAYER_LEVELUP,
                    Message.LoadMode.IMMEDIATE,
                    1
            );
        else
            return new Message(
                    Message.Type.CHAT,
                    Component.text("出现了特殊礼物！", COLOR_SPECIAL),
                    Sound.ENTITY_ALLAY_AMBIENT_WITH_ITEM,
                    Message.LoadMode.IMMEDIATE,
                    1
            );
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

    public static Message getBonusEventMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("奖励事件！得分暂时落后的玩家得到了道具奖励！", COLOR_SPECIAL),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealMsg(Player victim)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("你从 ", COLOR_CAUTION)
                        .append(Component.text(victim.getName(), COLOR_VALUE))
                        .append(Component.text(" 身上偷取了礼物，获得了", COLOR_CAUTION))
                        .append(Component.text(GiftHunting.config.stealerScore, COLOR_VALUE))
                        .append(Component.text("点分数", COLOR_CAUTION)),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getBeenStolenMsg(Player thief)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("你的礼物被 ", COLOR_BAD)
                        .append(Component.text(thief.getName(), COLOR_VALUE))
                        .append(Component.text(" 偷走了！失去了", COLOR_BAD))
                        .append(Component.text(GiftHunting.config.stealerScore, COLOR_VALUE))
                        .append(Component.text("点分数", COLOR_BAD)),
                Sound.ENTITY_VILLAGER_HURT,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealBroadcastMsg(Player thief, Player victim)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(victim.getName(), COLOR_VALUE)
                        .append(Component.text(" 发现自己的背包变轻了，原来是礼物被 ", COLOR_TEXT))
                        .append(Component.text(thief.getName(), COLOR_VALUE))
                        .append(Component.text(" 偷走了！", COLOR_TEXT)),
                Sound.ENTITY_VILLAGER_CELEBRATE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static List<Message> getGameFinishedMsg(Player player)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("游戏结束！", COLOR_SPECIAL),
                Sound.ENTITY_ENDER_DRAGON_DEATH,
                Message.LoadMode.REPLACE,
                100
        ));
        messages.add(getRankingMsg(player));
        return messages;
    }

    public static Message getRankingMsg(Player player)
    {
        TextComponent component = Component.text("得分排名:\n", COLOR_SPECIAL);

        int ranking = 1;
        for(Player p : GiftHunting.gameManager.getRankedPlayerList())
        {
            component = component.append(Component.text(ranking + " - " + p.getName() + " - " + GiftHunting.gameManager.getScore(p) + "\n", p == player ? COLOR_GOOD : COLOR_CAUTION));
            ranking ++;
        }

        return new Message(
                Message.Type.CHAT,
                component,
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                100
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
                        .append(Component.text(GiftHunting.gameManager.getScore(player), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                GiftHunting.config.finishedStateDuration
        );
    }

    public static Message getGameBackToIdleMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("感谢大家的参与，圣诞快乐！", COLOR_SPECIAL),
                Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,
                Message.LoadMode.IMMEDIATE,
                1
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
                Component.text("New Gift Spawner Added: ", COLOR_TEXT)
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
                Component.text("Gift Spawner Removed: ", COLOR_TEXT)
                        .append(Component.text(location.toVector().toString(), COLOR_VALUE)),
                Sound.BLOCK_BEACON_DEACTIVATE,
                Message.LoadMode.REPLACE,
                30
        );
    }

    public static Message getSpawnerCountActionbar()
    {
        return new Message(
            Message.Type.ACTIONBAR_PREFIX,
            Component.text("生成点数量: ", COLOR_VARIABLE)
                    .append(Component.text(GiftHunting.config.getGiftSpawnerCount(), COLOR_VALUE)),
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

    public static TextComponent getClearAllGiftMsg(int count)
    {
        return Component.text("Cleared " + count + " gifts!", COLOR_CAUTION);
    }

    public static TextComponent getClearUntrackedGiftMsg(int count)
    {
        return Component.text("Cleared " + count + " untracked gifts!", COLOR_CAUTION);
    }

    private static TextComponent getFormattedTime(int time)
    {
        int mm = (time/20) / 60;
        int ss = (time/20) % 60;
        return Component.text(String.format("%02d:%02d", mm, ss), COLOR_VALUE);
    }
}
