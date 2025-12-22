package io.lazysheeep.gifthunting.factory;

import io.lazysheeep.gifthunting.buffs.Buff;
import io.lazysheeep.gifthunting.game.GameInstance;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MessageFactory
{
    public static final TextColor COLOR_VARIABLE = NamedTextColor.GOLD;
    public static final TextColor COLOR_VALUE = NamedTextColor.GREEN;
    public static final TextColor COLOR_TEXT = NamedTextColor.AQUA;
    public static final TextColor COLOR_CAUTION = NamedTextColor.YELLOW;
    public static final TextColor COLOR_SPECIAL = NamedTextColor.LIGHT_PURPLE;
    public static final TextColor COLOR_VITAL = NamedTextColor.RED;
    public static final TextColor COLOR_GRAY = NamedTextColor.GRAY;
    public static final TextColor COLOR_PLAIN = NamedTextColor.WHITE;
    public static final TextColor COLOR_GOOD = NamedTextColor.GREEN;
    public static final TextColor COLOR_BAD = NamedTextColor.RED;

    private MessageFactory() {}

    public static Message getOnBecomeGHPlayerMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("已进入圣诞活动区域", COLOR_GRAY),
                Message.LoadMode.REPLACE,
                1
        );
    }

    public static Message getOnReconnectGHPlayerMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("已重新进入圣诞活动区域", COLOR_GRAY),
                Message.LoadMode.REPLACE,
                1
        );
    }

    public static Message getOnNoLongerGHPlayerMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("已离开圣诞活动区域", COLOR_GRAY),
                Message.LoadMode.REPLACE,
                1
        );
    }

    public static Message getGameReadyingMsg(int readyStateDuration)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏将在 ", COLOR_CAUTION)
                        .append(getFormattedTime(readyStateDuration))
                        .append(Component.text(" 后开始！", COLOR_CAUTION)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.REPLACE,
                1
        );
    }

    public static Message getGameReadyingActionbar(int readyStateDuration, int currentTime)
    {
        int countDown = readyStateDuration - currentTime;
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

    public static Message getProgressingActionbarPrefix(int time)
    {
        return new Message(
                Message.Type.ACTIONBAR_PREFIX,
                Component.text("时间: ", COLOR_VARIABLE)
                        .append(getFormattedTime(time)),
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

    public static List<Message> getActionbarSuffixWhenScoreChanged(int currentScore, int scoreDelta)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(currentScore, COLOR_VALUE))
                        .append(scoreDelta >= 0 ? Component.text("+" + scoreDelta, COLOR_CAUTION) : Component.text(scoreDelta, COLOR_BAD)),
                Message.LoadMode.REPLACE,
                30)
        );
        messages.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_VARIABLE)
                        .append(Component.text(currentScore + scoreDelta, COLOR_VALUE)),
                Message.LoadMode.WAIT,
                -1)
        );
        return messages;
    }

    public static Message getGiftClickedActionbar(Gift gift)
    {
        int progressBarToFetchL = (int)(20*((float)gift.getClicksToNextFetch()/gift.getType().clicksPerFetch));
        int progressBarToFetchR = 20 - progressBarToFetchL;
        int remainingCapacity = gift.getRemainingCapacity();
        TextComponent textComponent = Component.text("开启中 ", COLOR_TEXT)
                                               .append(Component.text("[", COLOR_GRAY))
                                               .append(Component.text("|".repeat(progressBarToFetchL), COLOR_VALUE))
                                               .append(Component.text("|".repeat(progressBarToFetchR), COLOR_GRAY))
                                               .append(Component.text("]", COLOR_GRAY));
        if(remainingCapacity > 1)
        {
            textComponent = textComponent.append(Component.text(" x", COLOR_CAUTION)).append(Component.text(remainingCapacity, COLOR_VALUE));
        }
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                textComponent,
                Message.LoadMode.REPLACE,
                10
        );
    }

    public static Message getGiftFetchedActionbar(Gift gift)
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("已开启 ", COLOR_CAUTION)
                        .append(Component.text("[", COLOR_GRAY))
                        .append(Component.text("|".repeat(20), COLOR_GRAY))
                        .append(Component.text("]", COLOR_GRAY)),
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
                20
        );
    }

    public static Message getDeliverSpecialGiftMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("特殊礼物已生成！", COLOR_SPECIAL),
                Sound.ENTITY_PLAYER_LEVELUP,
                Message.LoadMode.WAIT,
                20
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
                20
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
                20
        );
    }

    public static Message getSilencedMsg(GHPlayer badGuy)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("被 ", COLOR_BAD)
                         .append(Component.text(badGuy.getPlayer().getName(), COLOR_VALUE))
                         .append(Component.text(" 沉默了！", COLOR_BAD)),
                Message.LoadMode.REPLACE,
                1
        );
    }

    public static Message getSilenceCounteredMsg(GHPlayer badGuy)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("识破了 ", COLOR_GOOD)
                         .append(Component.text(badGuy.getPlayer().getName(), COLOR_VALUE))
                         .append(Component.text(" 的沉默效果！", COLOR_GOOD)),
                Message.LoadMode.REPLACE,
                1
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
                        .append(Component.text(" 点数", COLOR_CAUTION)),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                Message.LoadMode.REPLACE,
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
                        .append(Component.text(" 点数", COLOR_BAD)),
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

    public static Message getStealReflectedBroadcastMsg(Player thief, Player victim, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(victim.getName(), COLOR_VALUE)
                         .append(Component.text(" 识破了 ", COLOR_TEXT))
                         .append(Component.text(thief.getName(), COLOR_VALUE))
                         .append(Component.text(" 的偷窃，反夺取了 ", COLOR_TEXT))
                         .append(Component.text(score, COLOR_VALUE))
                         .append(Component.text(" 点数!", COLOR_TEXT)),
                Sound.ENTITY_VILLAGER_CELEBRATE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getRevolutionBroadcastMsg(GHPlayer revolutionist, GHPlayer revolutionTarget)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(revolutionist.getPlayer().getName(), COLOR_VALUE)
                         .append(Component.text(" 发动了对 ", COLOR_TEXT))
                         .append(Component.text(revolutionTarget.getPlayer().getName(), COLOR_VALUE))
                         .append(Component.text(" 的革命！", COLOR_TEXT))
                         .append(Component.text(revolutionTarget.getPlayer().getName(), COLOR_VALUE))
                         .append(Component.text(" 目前的得分为 ", COLOR_TEXT))
                         .append(Component.text(revolutionTarget.getScore(), COLOR_VALUE)),
                Sound.ENTITY_ENDER_DRAGON_GROWL,
                Message.LoadMode.WAIT,
                20
        );
    }

    public static List<Message> getGameFinishedMsg(GHPlayer ghPlayer, List<GHPlayer> allGHPlayersSorted)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("有玩家达到了获胜分数，游戏结束！", COLOR_SPECIAL),
                Sound.ENTITY_ENDER_DRAGON_DEATH,
                Message.LoadMode.REPLACE,
                100
        ));
        messages.add(getRankingMsg(ghPlayer, allGHPlayersSorted));
        return messages;
    }

    public static Message getRankingMsg(GHPlayer ghPlayer, List<GHPlayer> allGHPlayersSorted)
    {
        TextComponent component = Component.text("\n【得分排名】\n", COLOR_SPECIAL);

        int ranking = 1;
        for(GHPlayer ghP : allGHPlayersSorted)
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

    public static Message getGameFinishedActionbarInfix(int duration)
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("游戏结束", COLOR_SPECIAL),
                Message.LoadMode.REPLACE,
                duration
        );
    }

    public static Message getGameFinishedActionbarSuffix(@NotNull GHPlayer ghPlayer, int duration)
    {
        return new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("最终得分: ", COLOR_SPECIAL)
                        .append(Component.text(ghPlayer.getScore(), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                duration
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

    public static Message getNormalSpawnerCountActionbar(int spawnerCount)
    {
        return new Message(
            Message.Type.ACTIONBAR_PREFIX,
            Component.text("生成点数量: ", COLOR_VARIABLE)
                    .append(Component.text(spawnerCount, COLOR_VALUE)),
            Message.LoadMode.REPLACE,
            30
        );
    }

    public static Message getSpecialSpawnerCountActionbar(int spawnerCount)
    {
        return new Message(
            Message.Type.ACTIONBAR_PREFIX,
            Component.text("生成点数量: ", COLOR_VARIABLE)
                    .append(Component.text(spawnerCount, COLOR_VALUE)),
            Message.LoadMode.REPLACE,
            30
        );
    }

    public static TextComponent getGameStatsText(@Nullable GameInstance gameInstance)
    {
        if(gameInstance == null)
        {
            return Component.text("No active game instance.", COLOR_VITAL);
        }
        return Component.text("state: ", COLOR_VARIABLE).append(Component.text(gameInstance.getCurrentStateEnum().toString(), COLOR_VALUE))
                .append(Component.text("\nonline players: ", COLOR_VARIABLE)).append(Component.text(gameInstance.getPlayerManager().getOnlineGHPlayerCount(), COLOR_VALUE))
                .append(Component.text("\noffline players: ", COLOR_VARIABLE)).append(Component.text(gameInstance.getPlayerManager().getOfflineGHPlayerCount(), COLOR_VALUE))
                .append(Component.text("\nnormalGiftSpawners: ", COLOR_VARIABLE)).append(Component.text(gameInstance.getGiftManager().getNormalSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\nspecialGiftSpawners: ", COLOR_VARIABLE)).append(Component.text(gameInstance.getGiftManager().getSpecialSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\nnormalGifts: ", COLOR_VARIABLE)).append(Component.text(gameInstance.getGiftManager().getNormalGiftCount(), COLOR_VALUE))
                .append(Component.text("\nspecialGift: ", COLOR_VARIABLE)).append(Component.text(gameInstance.getGiftManager().hasSpecialGift(), COLOR_VALUE));

    }

    public static TextComponent getEventStartText()
    {
        return Component.text("Game started!", COLOR_GOOD);
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

    private static TextComponent getFormattedTime(int ticks)
    {
        int mm = (ticks/20) / 60;
        int ss = (ticks/20) % 60;
        return Component.text(String.format("%02d:%02d", mm, ss), COLOR_VALUE);
    }

    public static Message getSkillCooldownActionbar(int charges, int maxCharges, int cooldownTimer, int cooldownDuration, int aftercastTimer, int aftercastDuration)
    {
        boolean isAftercast = aftercastTimer > 0 && aftercastDuration > 0;
        int left = isAftercast ? Math.max(0, charges - 1) : charges;
        int rightVal = isAftercast ? Math.min(maxCharges, Math.max(0, charges)) : Math.min(maxCharges, charges + 1);
        if(maxCharges <= 0) maxCharges = 1;
        boolean isMax = charges >= maxCharges && cooldownTimer <= 0 && aftercastTimer <= 0;
        int filled;
        Component bar;
        if(isAftercast)
        {
            filled = Math.max(0, Math.min(20, (int)(20f * ((float) aftercastTimer / (float) aftercastDuration))));
            int empty = 20 - filled;
            bar = Component.text("[", COLOR_GRAY)
                           .append(Component.text("|".repeat(filled), COLOR_VALUE))
                           .append(Component.text("|".repeat(empty), COLOR_GRAY))
                           .append(Component.text("]", COLOR_GRAY));
        }
        else if(isMax)
        {
            bar = Component.text("[", COLOR_GRAY)
                           .append(Component.text("|".repeat(20), COLOR_SPECIAL))
                           .append(Component.text("]", COLOR_GRAY));
        }
        else
        {
            filled = 0;
            if(cooldownDuration > 0)
            {
                int progressed = Math.max(0, cooldownDuration - Math.max(0, cooldownTimer));
                filled = Math.max(0, Math.min(20, (int)(20f * ((float) progressed / (float) cooldownDuration))));
            }
            int empty = 20 - filled;
            bar = Component.text("[", COLOR_GRAY)
                           .append(Component.text("|".repeat(filled), COLOR_VALUE))
                           .append(Component.text("|".repeat(empty), COLOR_GRAY))
                           .append(Component.text("]", COLOR_GRAY));
        }
        Component rightComp = isMax ? Component.text("MAX", COLOR_SPECIAL) : Component.text(rightVal, COLOR_VALUE);
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("", COLOR_TEXT)
                        .append(Component.text(left, COLOR_VALUE))
                        .append(Component.text(" ", COLOR_TEXT))
                        .append(bar)
                        .append(Component.text(" ", COLOR_TEXT))
                        .append(rightComp),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getBuffsActionbar(List<Buff> buffs)
    {
        TextComponent comp = Component.text("");
        boolean first = true;
        for(Buff b : buffs)
        {
            if(!first)
            {
                comp = comp.append(Component.text("  "));
            }
            first = false;
            int rt = b.getRemainingTime();
            TextComponent timeComp = (rt == -1)
                ? Component.text("∞", COLOR_VALUE)
                : Component.text(rt / 20, COLOR_VALUE);
            comp = comp
                .append(b.getDisplayName())
                .append(Component.text("(", COLOR_PLAIN))
                .append(timeComp)
                .append(Component.text(")", COLOR_PLAIN));
        }
        return new Message(
            Message.Type.ACTIONBAR_INFIX,
            comp,
            Message.LoadMode.IMMEDIATE,
            1
        );
    }

    public static Message getCapturedItemFromOtherMsg(String sourceName, Component itemName)
    {
        TextComponent comp = Component.text("获取了", COLOR_TEXT)
            .append(Component.text(sourceName, COLOR_VALUE))
            .append(Component.text("掉落的", COLOR_TEXT))
            .append(itemName);
        return new Message(Message.Type.CHAT, comp, Message.LoadMode.REPLACE, 1);
    }

    public static Message getCapturedScoreFromOtherMsg(String sourceName, int scoreValue)
    {
        TextComponent comp = Component.text("获取了", COLOR_TEXT)
            .append(Component.text(sourceName, COLOR_VALUE))
            .append(Component.text("掉落的", COLOR_TEXT))
            .append(Component.text(scoreValue + "分数", COLOR_VARIABLE));
        return new Message(Message.Type.CHAT, comp, Message.LoadMode.REPLACE, 1);
    }
}
