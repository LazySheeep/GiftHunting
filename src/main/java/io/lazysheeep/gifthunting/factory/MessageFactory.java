package io.lazysheeep.gifthunting.factory;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.lazuliui.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MessageFactory
{
    private static final TextColor COLOR_VARIABLE = NamedTextColor.GOLD;
    private static final TextColor COLOR_VALUE = NamedTextColor.GREEN;
    private static final TextColor COLOR_TEXT = NamedTextColor.AQUA;
    private static final TextColor COLOR_CAUTION = NamedTextColor.YELLOW;
    private static final TextColor COLOR_SPECIAL = NamedTextColor.LIGHT_PURPLE;
    private static final TextColor COLOR_VITAL = NamedTextColor.RED;
    private static final TextColor COLOR_GRAY = NamedTextColor.GRAY;
    private static final TextColor COLOR_GOOD = NamedTextColor.GREEN;
    private static final TextColor COLOR_BAD = NamedTextColor.RED;

    private MessageFactory() {}

    public static Message getGameReadyingMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏将在 ", COLOR_CAUTION)
                        .append(getFormattedTime(GiftHunting.GetPlugin().getGameManager().getReadyStateDuration()))
                        .append(Component.text(" 后开始！", COLOR_CAUTION)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameReadyingActionbar()
    {
        int countDown = GiftHunting.GetPlugin().getGameManager().getReadyStateDuration() - GiftHunting.GetPlugin().getGameManager().getMainTimer();
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

    public static List<Message> getGameIntroMsg(int victoryScore)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
            Message.Type.CHAT,
            Component.text("游戏即将开始...", COLOR_TEXT),
            Sound.BLOCK_NOTE_BLOCK_PLING,
            Message.LoadMode.REPLACE,
            60));
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("取得 ", COLOR_TEXT)
                         .append(Component.text(victoryScore, COLOR_VALUE))
                         .append(Component.text(" 分即可获胜", COLOR_TEXT)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                1));
        return messages;
    }

    public static Message getGameStartMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏开始！", COLOR_SPECIAL),
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
                        .append(getFormattedTime(GiftHunting.GetPlugin().getGameManager().getMainTimer())),
                Message.LoadMode.REPLACE,
                20
        );
    }

    public static Message getProgressingActionbarSuffix(@NotNull GHPlayer ghPlayer)
    {
        return new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(ghPlayer.getScore(), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                -1
        );
    }

    public static List<Message> getProgressingActionbarSuffixWhenScoreChanged(@NotNull GHPlayer ghPlayer, int score)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(ghPlayer.getScore(), COLOR_VALUE))
                        .append(score >= 0 ? Component.text("+" + score, COLOR_CAUTION) : Component.text(score, COLOR_BAD)),
                Message.LoadMode.REPLACE,
                30)
        );
        messages.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(ghPlayer.getScore() + score, COLOR_VALUE)),
                Message.LoadMode.WAIT,
                -1)
        );
        return messages;
    }

    public static Message getGiftClickedActionbar(Gift gift)
    {
        int progressBarToFetchL = (int)(20*((float)gift.getClicksToNextFetch()/gift.clicksPerFetch));
        int progressBarToFetchR = 20 - progressBarToFetchL;
        int progressBarTotalL = gift.getRemainingCapacity() - 1;
        int progressBarTotalR = gift.capacityInFetches - gift.getRemainingCapacity();
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("开启中 ", COLOR_TEXT)
                        .append(Component.text("[]".repeat(progressBarTotalL), COLOR_SPECIAL))
                        .append(Component.text("[", COLOR_SPECIAL))
                        .append(Component.text("|".repeat(progressBarToFetchL), COLOR_VALUE))
                        .append(Component.text("|".repeat(progressBarToFetchR), COLOR_GRAY))
                        .append(Component.text("]", COLOR_SPECIAL))
                        .append(Component.text("[]".repeat(progressBarTotalR), COLOR_GRAY)),
                Message.LoadMode.REPLACE,
                10
        );
    }

    public static Message getGiftFetchedActionbar(Gift gift)
    {
        int progressBarTotalL = gift.getRemainingCapacity() - 1;
        int progressBarTotalR = gift.capacityInFetches - gift.getRemainingCapacity();
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("已开启 ", COLOR_CAUTION)
                        .append(Component.text("[]".repeat(progressBarTotalL), COLOR_SPECIAL))
                        .append(Component.text("[", COLOR_GRAY))
                        .append(Component.text("|".repeat(20), COLOR_GRAY))
                        .append(Component.text("]", COLOR_GRAY))
                        .append(Component.text("[]".repeat(progressBarTotalR), COLOR_GRAY)),
                Message.LoadMode.REPLACE,
                20
        );
    }

    public static Message getDeliverNormalGiftMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("场景中生成了新的礼物！", COLOR_GOOD),
                Sound.ENTITY_PLAYER_LEVELUP,
                Message.LoadMode.WAIT,
                60
        );
    }

    public static Message getDeliverSpecialGiftMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("特殊礼物已生成！", COLOR_SPECIAL),
                Sound.ENTITY_PLAYER_LEVELUP,
                Message.LoadMode.WAIT,
                60
        );
    }

    public static Message getDiscipleBirthMsg(GHPlayer disciple)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(disciple.getPlayer().getName(), COLOR_VALUE)
                .append(Component.text(" 成为了信徒，跟随其指引即可找到特殊礼物之所在！", COLOR_SPECIAL)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                60
        );
    }

    public static Message getGiveStealerMsg(int stealerGiveScore)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("所有分数低于 ", COLOR_SPECIAL)
                         .append(Component.text(stealerGiveScore, COLOR_VALUE))
                         .append(Component.text(" 的玩家都获得了道具奖励！", COLOR_SPECIAL)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                60
        );
    }

    public static Message getSilencedActionbarInfix(GHPlayer badGuy, int duration)
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("被 ", COLOR_BAD)
                         .append(Component.text(badGuy.getPlayer().getName(), COLOR_VALUE))
                         .append(Component.text(" 沉默了！", COLOR_BAD)),
                Message.LoadMode.REPLACE,
                duration
        );
    }

    public static Message getStealMsg(Player victim, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("你从 ", COLOR_CAUTION)
                        .append(Component.text(victim.getName(), COLOR_VALUE))
                        .append(Component.text(" 身上偷取了礼物，获得了", COLOR_CAUTION))
                        .append(Component.text(score, COLOR_VALUE))
                        .append(Component.text("点分数", COLOR_CAUTION)),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getBeenStolenMsg(Player thief, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("你的礼物被 ", COLOR_BAD)
                        .append(Component.text(thief.getName(), COLOR_VALUE))
                        .append(Component.text(" 偷走了！失去了", COLOR_BAD))
                        .append(Component.text(score, COLOR_VALUE))
                        .append(Component.text("点分数", COLOR_BAD)),
                Sound.ENTITY_VILLAGER_HURT,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealBroadcastMsg(Player thief, Player victim, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(thief.getName(), COLOR_VALUE)
                         .append(Component.text(" 从 ", COLOR_TEXT))
                         .append(Component.text(victim.getName(), COLOR_VALUE))
                         .append(Component.text(" 身上窃取了 ", COLOR_TEXT))
                         .append(Component.text(score, COLOR_VALUE))
                         .append(Component.text(" 点数!", COLOR_TEXT)),
                Sound.ENTITY_VILLAGER_CELEBRATE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealReflectedMsg(Player thief, Player victim, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(thief.getName(), COLOR_VALUE)
                         .append(Component.text(" 试图从 ", COLOR_TEXT))
                         .append(Component.text(victim.getName(), COLOR_VALUE))
                         .append(Component.text(" 身上窃取礼物，但是被识破了！反而送上 ", COLOR_TEXT))
                         .append(Component.text(score, COLOR_VALUE))
                         .append(Component.text(" 点数!", COLOR_TEXT)),
                Sound.ENTITY_VILLAGER_CELEBRATE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static List<Message> getGameFinishedMsg(GHPlayer ghPlayer)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("有玩家达到了获胜分数，游戏结束！", COLOR_SPECIAL),
                Sound.ENTITY_ENDER_DRAGON_DEATH,
                Message.LoadMode.REPLACE,
                100
        ));
        messages.add(getRankingMsg(ghPlayer));
        return messages;
    }

    public static Message getRankingMsg(GHPlayer ghPlayer)
    {
        TextComponent component = Component.text("\n【得分排名】\n", COLOR_SPECIAL);

        int ranking = 1;
        for(GHPlayer ghP : GiftHunting.GetPlugin().getPlayerManager().getSortedGHPlayers())
        {
            component = component.append(Component.text(ranking + " - " + ghP.getPlayer().getName() + " - " + ghP.getScore() + "\n", ghP == ghPlayer ? COLOR_GOOD : COLOR_CAUTION));
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
                GiftHunting.GetPlugin().getGameManager().getFinishedStateDuration()
        );
    }

    public static Message getGameFinishedActionbarSuffix(@NotNull GHPlayer ghPlayer)
    {
        return new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("最终得分: ", COLOR_SPECIAL)
                        .append(Component.text(ghPlayer.getScore(), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                GiftHunting.GetPlugin().getGameManager().getFinishedStateDuration()
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

    public static Message getAddGiftSpawnerActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("生成点已添加", COLOR_TEXT),
                Sound.BLOCK_BEACON_ACTIVATE,
                Message.LoadMode.REPLACE,
                30
        );
    }

    public static Message getRemoveGiftSpawnerActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("生成点已移除", COLOR_TEXT),
                Sound.BLOCK_BEACON_DEACTIVATE,
                Message.LoadMode.REPLACE,
                30
        );
    }

    public static Message getNormalSpawnerCountActionbar()
    {
        return new Message(
            Message.Type.ACTIONBAR_PREFIX,
            Component.text("生成点数量: ", COLOR_VARIABLE)
                    .append(Component.text(GiftHunting.GetPlugin().getGiftManager().getNormalSpawnerCount(), COLOR_VALUE)),
            Message.LoadMode.REPLACE,
            30
        );
    }

    public static Message getSpecialSpawnerCountActionbar()
    {
        return new Message(
            Message.Type.ACTIONBAR_PREFIX,
            Component.text("生成点数量: ", COLOR_VARIABLE)
                    .append(Component.text(GiftHunting.GetPlugin().getGiftManager().getSpecialSpawnerCount(), COLOR_VALUE)),
            Message.LoadMode.REPLACE,
            30
        );
    }

    public static TextComponent getGameStatsText()
    {
        return Component.text("state: ", COLOR_VARIABLE).append(Component.text(GiftHunting.GetPlugin().getGameManager().getState().toString(), COLOR_VALUE))
                .append(Component.text("\ntimer: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.GetPlugin().getGameManager().getMainTimer(), COLOR_VALUE))
                .append(Component.text("\nplayers: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.GetPlugin().getPlayerManager().getGHPlayerCount(), COLOR_VALUE))
                .append(Component.text("\nnormalGiftSpawners: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.GetPlugin().getGiftManager().getNormalSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\nspecialGiftSpawners: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.GetPlugin().getGiftManager().getSpecialSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\nnormalGifts: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.GetPlugin().getGiftManager().getNormalGiftCount(), COLOR_VALUE))
                .append(Component.text("\nspecialGift: ", COLOR_VARIABLE)).append(Component.text(GiftHunting.GetPlugin().getGiftManager().hasSpecialGift(), COLOR_VALUE));

    }

    public static TextComponent getEventStartText()
    {
        return Component.text("Game started!", COLOR_GOOD);
    }

    public static TextComponent getEventCantStartText()
    {
        return Component.text("Can't start the game! Please check if the game has already begun or the game spawn has not been set!", COLOR_VITAL);
    }

    public static TextComponent getEventCantEndText()
    {
        return Component.text("The game is not in progress!", COLOR_VITAL);
    }

    public static TextComponent getSaveConfigText()
    {
        return Component.text("Config saved!", COLOR_GOOD);
    }

    public static TextComponent getReloadConfigText()
    {
        return Component.text("Config reloaded!", COLOR_GOOD);
    }

    public static TextComponent getEventStopText()
    {
        return Component.text("Game stopped!", COLOR_CAUTION);
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
