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
    public static final TextColor COLOR_TEXT_NORMAL = NamedTextColor.AQUA;
    public static final TextColor COLOR_TEXT_CAUTION = NamedTextColor.YELLOW;
    public static final TextColor COLOR_TEXT_IMPORTANT = NamedTextColor.LIGHT_PURPLE;
    public static final TextColor COLOR_TEXT_VITAL = NamedTextColor.RED;
    public static final TextColor COLOR_VALUE = NamedTextColor.GREEN;
    public static final TextColor COLOR_EMPHASIS = NamedTextColor.GREEN;
    public static final TextColor COLOR_BACKGROUND = NamedTextColor.GRAY;
    public static final TextColor COLOR_INFO_GOOD = NamedTextColor.GREEN;
    public static final TextColor COLOR_INFO_NORMAL = NamedTextColor.AQUA;
    public static final TextColor COLOR_INFO_BAD = NamedTextColor.RED;
    public static final TextColor COLOR_PLAYER_NAME = NamedTextColor.GOLD;
    public static final TextColor COLOR_ITEM_NAME = NamedTextColor.LIGHT_PURPLE;
    public static final TextColor COLOR_ITEM_DESCRIPTION = COLOR_TEXT_NORMAL;
    public static final TextColor COLOR_TITLE = NamedTextColor.YELLOW;
    public static final TextColor COLOR_HINT = NamedTextColor.GRAY;
    public static final TextColor COLOR_MISC = NamedTextColor.WHITE;

    private MessageFactory() {}

    public static Message getOnBecomeGHPlayerMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("已进入圣诞活动区域", COLOR_HINT),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getOnReconnectGHPlayerMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("已重新进入圣诞活动区域", COLOR_HINT),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getOnNoLongerGHPlayerMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("已离开圣诞活动区域", COLOR_HINT),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameReadyingMsg(int readyStateDuration)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏将在 ", COLOR_TEXT_CAUTION)
                        .append(getFormattedTime(readyStateDuration))
                        .append(Component.text(" 后开始！", COLOR_TEXT_CAUTION)),
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
                Component.text("游戏将在 ", COLOR_TEXT_NORMAL)
                        .append(getFormattedTime(countDown))
                        .append(Component.text(" 后开始", COLOR_TEXT_NORMAL)),
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
            Component.text("游戏即将开始...", COLOR_TEXT_NORMAL),
            Sound.BLOCK_NOTE_BLOCK_PLING,
            Message.LoadMode.REPLACE,
            60));
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("取得 ", COLOR_TEXT_NORMAL)
                         .append(Component.text(victoryScore, COLOR_VALUE))
                         .append(Component.text(" 分即可获胜", COLOR_TEXT_NORMAL)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                1));
        return messages;
    }

    public static Message getGameStartMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("游戏开始！", COLOR_TEXT_IMPORTANT),
                Sound.ENTITY_PLAYER_LEVELUP,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameStartActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("出发吧!", COLOR_TEXT_IMPORTANT),
                Message.LoadMode.REPLACE,
                40
        );
    }

    public static Message getProgressingActionbarPrefix(int time)
    {
        return new Message(
                Message.Type.ACTIONBAR_PREFIX,
                Component.text("时间: ", COLOR_TITLE)
                        .append(getFormattedTime(time)),
                Message.LoadMode.REPLACE,
                20
        );
    }

    public static Message getProgressingActionbarSuffix(@NotNull GHPlayer ghPlayer)
    {
        return new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_TITLE)
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
                Component.text("得分: ", COLOR_TITLE)
                        .append(Component.text(currentScore, COLOR_VALUE))
                        .append(scoreDelta >= 0 ? Component.text("+" + scoreDelta, COLOR_TEXT_CAUTION) : Component.text(scoreDelta, COLOR_INFO_BAD)),
                Message.LoadMode.REPLACE,
                30)
        );
        messages.add(new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("得分: ", COLOR_TITLE)
                        .append(Component.text(currentScore + scoreDelta, COLOR_VALUE)),
                Message.LoadMode.WAIT,
                -1)
        );
        return messages;
    }

    public static Component getGiftNameComponent(Gift gift)
    {
        int progressBarLength = gift.getType().clicksPerFetch;
        int progressBarToFetchL = (int)(progressBarLength * ((float)gift.getClicksToNextFetch()/gift.getType().clicksPerFetch));
        int progressBarToFetchR = progressBarLength - progressBarToFetchL;
        int remainingCapacity = gift.getRemainingCapacity();
        TextComponent textComponent = Component.text("[", COLOR_BACKGROUND)
                                               .append(Component.text("|".repeat(progressBarToFetchL), COLOR_EMPHASIS))
                                               .append(Component.text("|".repeat(progressBarToFetchR), COLOR_BACKGROUND))
                                               .append(Component.text("]", COLOR_BACKGROUND));
        if(remainingCapacity > 1)
        {
            textComponent = textComponent.append(Component.text(" x", COLOR_MISC)).append(Component.text(remainingCapacity, COLOR_VALUE));
        }
        return textComponent;
    }

    public static Message getDeliverNormalGiftMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("场景中生成了新的礼物", COLOR_TEXT_CAUTION),
                Sound.ENTITY_PLAYER_LEVELUP,
                Message.LoadMode.WAIT,
                20
        );
    }

    public static Message getDeliverSpecialGiftMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("超级礼物已生成", COLOR_TEXT_IMPORTANT),
                Sound.ENTITY_PLAYER_LEVELUP,
                Message.LoadMode.WAIT,
                20
        );
    }

    public static Message getGiveStealerMsg(int stealerGiveScore)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("所有分数低于 ", COLOR_TEXT_IMPORTANT)
                         .append(Component.text(stealerGiveScore, COLOR_VALUE))
                         .append(Component.text(" 的玩家都获得了道具奖励", COLOR_TEXT_IMPORTANT)),
                Sound.BLOCK_NOTE_BLOCK_PLING,
                Message.LoadMode.WAIT,
                20
        );
    }

    public static Message getSilencedMsg(GHPlayer badGuy)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("被 ", COLOR_INFO_BAD)
                         .append(Component.text(badGuy.getPlayer().getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 沉默了", COLOR_INFO_BAD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getSilenceCounteredMsg(GHPlayer badGuy)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("识破了 ", COLOR_INFO_GOOD)
                         .append(Component.text(badGuy.getPlayer().getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的沉默效果", COLOR_INFO_GOOD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealMsg(Player victim, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("偷取了 ", COLOR_INFO_GOOD)
                        .append(Component.text(victim.getName(), COLOR_PLAYER_NAME))
                        .append(Component.text(" 的 ", COLOR_INFO_GOOD))
                        .append(Component.text(score, COLOR_VALUE))
                        .append(Component.text(" 点礼物", COLOR_INFO_GOOD)),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getBeenStolenMsg(Player thief, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("被 ", COLOR_INFO_BAD)
                        .append(Component.text(thief.getName(), COLOR_PLAYER_NAME))
                        .append(Component.text(" 偷走了", COLOR_INFO_BAD))
                        .append(Component.text(score, COLOR_VALUE))
                        .append(Component.text(" 点礼物", COLOR_INFO_BAD)),
                Sound.ENTITY_VILLAGER_HURT,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealCounteredMsg(Player smartGuy, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("偷窃被 ", COLOR_INFO_BAD)
                         .append(Component.text(smartGuy.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 识破，失去了 ", COLOR_INFO_BAD))
                         .append(Component.text(score, COLOR_VALUE))
                         .append(Component.text(" 点礼物", COLOR_INFO_BAD)),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getCounteringStealMsg(Player thief, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("识破了 ", COLOR_INFO_GOOD)
                         .append(Component.text(thief.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的偷窃，夺取了 ", COLOR_INFO_GOOD))
                         .append(Component.text(score, COLOR_VALUE))
                         .append(Component.text(" 点礼物", COLOR_INFO_GOOD)),
                Sound.UI_TOAST_CHALLENGE_COMPLETE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealBroadcastMsg(Player thief, Player victim, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(thief.getName(), COLOR_PLAYER_NAME)
                         .append(Component.text(" 偷取了 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(victim.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(score, COLOR_VALUE))
                         .append(Component.text(" 点礼物!", COLOR_TEXT_NORMAL)),
                Sound.ENTITY_VILLAGER_CELEBRATE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getStealCounteredBroadcastMsg(Player thief, Player victim, int score)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(victim.getName(), COLOR_PLAYER_NAME)
                         .append(Component.text(" 识破了 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(thief.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的偷窃，反夺取了 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(score, COLOR_VALUE))
                         .append(Component.text(" 点礼物!", COLOR_TEXT_NORMAL)),
                Sound.ENTITY_VILLAGER_CELEBRATE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static List<Message> getGameFinishedMsg(GHPlayer ghPlayer, List<GHPlayer> allGHPlayersSorted)
    {
        List<Message> messages = new ArrayList<>();
        messages.add(new Message(
                Message.Type.CHAT,
                Component.text("有玩家达到了获胜分数，游戏结束!", COLOR_TEXT_IMPORTANT),
                Sound.ENTITY_ENDER_DRAGON_DEATH,
                Message.LoadMode.REPLACE,
                100
        ));
        messages.add(getRankingMsg(ghPlayer, allGHPlayersSorted));
        return messages;
    }

    public static Message getRankingMsg(GHPlayer ghPlayer, List<GHPlayer> allGHPlayersSorted)
    {
        TextComponent component = Component.text("\n【得分排名】\n", COLOR_TEXT_IMPORTANT);

        int ranking = 1;
        for(GHPlayer ghP : allGHPlayersSorted)
        {
            component = component.append(Component.text(ranking, COLOR_TITLE))
                                 .append(Component.text(" - ", COLOR_MISC))
                                 .append(Component.text(ghP.getPlayer().getName(), ghP == ghPlayer ? COLOR_EMPHASIS : COLOR_PLAYER_NAME))
                                 .append(Component.text(" - ", COLOR_MISC))
                                 .append(Component.text(ghP.getScore() + "\n", COLOR_VALUE));
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
                Component.text("游戏结束", COLOR_TEXT_IMPORTANT),
                Message.LoadMode.REPLACE,
                duration
        );
    }

    public static Message getGameFinishedActionbarSuffix(@NotNull GHPlayer ghPlayer, int duration)
    {
        return new Message(
                Message.Type.ACTIONBAR_SUFFIX,
                Component.text("最终得分: ", COLOR_TITLE)
                        .append(Component.text(ghPlayer.getScore(), COLOR_VALUE)),
                Message.LoadMode.REPLACE,
                duration
        );
    }

    public static Message getGameBackToIdleMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("感谢大家的参与，圣诞快乐!", COLOR_TEXT_IMPORTANT),
                Sound.ENTITY_FIREWORK_ROCKET_TWINKLE,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getGameTerminatedMsg()
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("管理员中止了游戏!", COLOR_TEXT_IMPORTANT),
                Sound.ENTITY_VILLAGER_HURT,
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static Message getAddGiftSpawnerActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("生成点已添加", COLOR_TEXT_NORMAL),
                Sound.BLOCK_BEACON_ACTIVATE,
                Message.LoadMode.REPLACE,
                30
        );
    }

    public static Message getRemoveGiftSpawnerActionbar()
    {
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text("生成点已移除", COLOR_TEXT_NORMAL),
                Sound.BLOCK_BEACON_DEACTIVATE,
                Message.LoadMode.REPLACE,
                30
        );
    }

    public static Message getNormalSpawnerCountActionbar(int spawnerCount)
    {
        return new Message(
            Message.Type.ACTIONBAR_PREFIX,
            Component.text("生成点数量: ", COLOR_TITLE)
                    .append(Component.text(spawnerCount, COLOR_VALUE)),
            Message.LoadMode.REPLACE,
            30
        );
    }

    public static Message getSpecialSpawnerCountActionbar(int spawnerCount)
    {
        return new Message(
            Message.Type.ACTIONBAR_PREFIX,
            Component.text("生成点数量: ", COLOR_TITLE)
                    .append(Component.text(spawnerCount, COLOR_VALUE)),
            Message.LoadMode.REPLACE,
            30
        );
    }

    public static TextComponent getGameStatsText(@Nullable GameInstance gameInstance)
    {
        if(gameInstance == null)
        {
            return Component.text("No active game instance.", COLOR_TEXT_VITAL);
        }
        return Component.text("state: ", COLOR_TITLE).append(Component.text(gameInstance.getCurrentStateEnum().toString(), COLOR_VALUE))
                .append(Component.text("\nonline players: ", COLOR_TITLE)).append(Component.text(gameInstance.getPlayerManager().getOnlineGHPlayerCount(), COLOR_VALUE))
                .append(Component.text("\noffline players: ", COLOR_TITLE)).append(Component.text(gameInstance.getPlayerManager().getOfflineGHPlayerCount(), COLOR_VALUE))
                .append(Component.text("\nnormalGiftSpawners: ", COLOR_TITLE)).append(Component.text(gameInstance.getGiftManager().getNormalSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\nspecialGiftSpawners: ", COLOR_TITLE)).append(Component.text(gameInstance.getGiftManager().getSpecialSpawnerCount(), COLOR_VALUE))
                .append(Component.text("\nnormalGifts: ", COLOR_TITLE)).append(Component.text(gameInstance.getGiftManager().getNormalGiftCount(), COLOR_VALUE))
                .append(Component.text("\nspecialGift: ", COLOR_TITLE)).append(Component.text(gameInstance.getGiftManager().hasSpecialGift(), COLOR_VALUE));

    }

    private static TextComponent getFormattedTime(int ticks)
    {
        int mm = (ticks/20) / 60;
        int ss = (ticks/20) % 60;
        return Component.text(String.format("%02d:%02d", mm, ss), COLOR_VALUE);
    }

    public static Message getSkillCooldownActionbar(int charges, int maxCharges, int cooldownTimer, int cooldownDuration, int activeTimer, int activeDuration)
    {
        boolean isActive = activeDuration > 0 && activeTimer >= 0;
        int left = isActive ? Math.max(0, charges - 1) : charges;
        int rightVal = isActive ? Math.min(maxCharges, Math.max(0, charges)) : Math.min(maxCharges, charges + 1);
        if(maxCharges <= 0) maxCharges = 1;
        boolean isMax = charges >= maxCharges && cooldownTimer == -1 && activeTimer == -1;
        Component bar;
        if(isActive)
        {
            int empty = Math.max(0, Math.min(20, (int) (20f * ((float) activeTimer / (float) activeDuration))));
            int filled = 20 - empty;
            bar = Component.text("[", COLOR_BACKGROUND)
                           .append(Component.text("|".repeat(filled), COLOR_EMPHASIS))
                           .append(Component.text("|".repeat(empty), COLOR_BACKGROUND))
                           .append(Component.text("]", COLOR_BACKGROUND));
        }
        else if(isMax)
        {
            bar = Component.text("[", COLOR_BACKGROUND)
                           .append(Component.text("|".repeat(20), COLOR_TEXT_IMPORTANT))
                           .append(Component.text("]", COLOR_BACKGROUND));
        }
        else
        {
            int filled = 0;
            if(cooldownDuration > 0 && cooldownTimer >= 0)
            {
                filled = Math.max(0, Math.min(20, (int)(20f * ((float) cooldownTimer / (float) cooldownDuration))));
            }
            int empty = 20 - filled;
            bar = Component.text("[", COLOR_BACKGROUND)
                           .append(Component.text("|".repeat(filled), COLOR_EMPHASIS))
                           .append(Component.text("|".repeat(empty), COLOR_BACKGROUND))
                           .append(Component.text("]", COLOR_BACKGROUND));
        }
        Component rightComp = isMax ? Component.text("MAX", COLOR_TEXT_IMPORTANT) : Component.text(rightVal, COLOR_VALUE);
        return new Message(
                Message.Type.ACTIONBAR_INFIX,
                Component.text(left, COLOR_VALUE)
                        .append(Component.text(" "))
                        .append(bar)
                        .append(Component.text(" "))
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
                .append(Component.text("(", COLOR_MISC))
                .append(timeComp)
                .append(Component.text(")", COLOR_MISC));
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
        TextComponent comp = Component.text("拾取了来自 ", COLOR_INFO_NORMAL)
            .append(Component.text(sourceName, COLOR_PLAYER_NAME))
            .append(Component.text(" 的 ", COLOR_INFO_NORMAL))
            .append(itemName);
        return new Message(Message.Type.CHAT, comp, Message.LoadMode.IMMEDIATE, 1);
    }

    public static Message getCapturedScoreFromOtherMsg(String sourceName, int scoreValue)
    {
        TextComponent comp = Component.text("拾取了来自 ", COLOR_INFO_NORMAL)
            .append(Component.text(sourceName, COLOR_PLAYER_NAME))
            .append(Component.text(" 掉落的 ", COLOR_INFO_NORMAL))
            .append(Component.text(scoreValue, COLOR_VALUE))
            .append(Component.text(" 点礼物", COLOR_INFO_NORMAL));
        return new Message(Message.Type.CHAT, comp, Message.LoadMode.IMMEDIATE, 1);
    }

    public static Message getDetectActionbar(boolean isSpecial, int distance)
    {
        TextComponent comp = Component.text(isSpecial ? "超级礼物: " : "普通礼物: ", COLOR_TEXT_NORMAL)
                                      .append(Component.text(distance, COLOR_VALUE))
                                      .append(Component.text("m", COLOR_MISC));
        return new Message(
            Message.Type.ACTIONBAR_INFIX,
            comp,
            Message.LoadMode.IMMEDIATE,
            1
        );
    }

    public static @NotNull Message getBoundMsg(GHPlayer badGuy)
    {
        return new Message(
                Message.Type.CHAT, Component.text("被 ", COLOR_INFO_BAD)
                                            .append(Component.text(badGuy.getName(), COLOR_PLAYER_NAME))
                                            .append(Component.text(" 束缚了!", COLOR_INFO_BAD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getBindCounteredMsg(GHPlayer smartGuy)
    {
        return new Message(
                Message.Type.CHAT, Component.text("束缚被 ", COLOR_INFO_BAD)
                                            .append(Component.text(smartGuy.getName(), COLOR_PLAYER_NAME))
                                            .append(Component.text(" 识破了!", COLOR_INFO_BAD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getCounteringBindMsg(GHPlayer badGuy)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("识破了 ", COLOR_INFO_GOOD)
                         .append(Component.text(badGuy.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的束缚!", COLOR_INFO_GOOD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getAbsorbMsg(int count)
    {
        return new Message(
                Message.Type.CHAT, Component.text("吸收了 ", COLOR_INFO_GOOD)
                                            .append(Component.text(count, COLOR_VALUE))
                                            .append(Component.text(" 个礼物/道具球!", COLOR_INFO_GOOD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getAbsorbFailedMsg()
    {
        return new Message(
                Message.Type.CHAT, Component.text("范围内没有可吸收的礼物/道具球!", COLOR_INFO_BAD),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getOrbCapturedByOthersMsg(GHPlayer newTarget)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("你的礼物/道具球被 ", COLOR_INFO_BAD)
                         .append(Component.text(newTarget.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 捕获了!", COLOR_INFO_BAD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getBlewByBombDroneMsg(GHPlayer sourceGHPlayer, int dropScore)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("来自 ", COLOR_INFO_BAD)
                         .append(Component.text(sourceGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的炸弹小飞机使你掉落了 ", COLOR_INFO_BAD))
                         .append(Component.text(dropScore, COLOR_VALUE))
                         .append(Component.text(" 点礼物!", COLOR_INFO_BAD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getDroneBlewMsg(GHPlayer victimGHPlayer, int dropScore)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("你的炸弹小飞机使 ", COLOR_INFO_GOOD)
                         .append(Component.text(victimGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 掉落了 ", COLOR_INFO_NORMAL))
                         .append(Component.text(dropScore, COLOR_VALUE))
                         .append(Component.text(" 点礼物!", COLOR_INFO_NORMAL)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getDroneExplodedBroadcastMsg(GHPlayer sourceGHPlayer, int totalDroppedScore)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(sourceGHPlayer.getName(), COLOR_PLAYER_NAME)
                         .append(Component.text(" 的炸弹小飞机爆炸造成了 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(totalDroppedScore, COLOR_VALUE))
                         .append(Component.text(" 点礼物掉落!", COLOR_TEXT_NORMAL)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getCounteringDawnMsg(GHPlayer attackerGHPlayer)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("识破了 ", COLOR_INFO_GOOD)
                         .append(Component.text(attackerGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的猎杀黎明!", COLOR_INFO_GOOD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getDawnCounteredMsg(GHPlayer victimGHPlayer)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("猎杀黎明被 ", COLOR_INFO_BAD)
                         .append(Component.text(victimGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 识破了!", COLOR_INFO_BAD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getDawnCounteredBroadcastMsg(GHPlayer attackerGHPlayer, GHPlayer victimGHPlayer)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(victimGHPlayer.getName(), COLOR_PLAYER_NAME)
                         .append(Component.text(" 识破了 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(attackerGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的猎杀黎明!", COLOR_TEXT_NORMAL)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getDawnHitMsg(GHPlayer victimGHPlayer, int lose)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("猎杀黎明击中了 ", COLOR_INFO_GOOD)
                         .append(Component.text(victimGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(", 夺取了 ", COLOR_INFO_GOOD))
                         .append(Component.text(lose, COLOR_VALUE))
                         .append(Component.text(" 点礼物!", COLOR_INFO_GOOD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getDawnBeenHitMsg(GHPlayer attackerGHPlayer, int lose)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text("被 ", COLOR_INFO_BAD)
                         .append(Component.text(attackerGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(" 的猎杀黎明击中，失去了 ", COLOR_INFO_BAD))
                         .append(Component.text(lose, COLOR_VALUE))
                         .append(Component.text(" 点礼物!", COLOR_INFO_BAD)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }

    public static @NotNull Message getDawnHitBroadcastMsg(GHPlayer attackerGHPlayer, GHPlayer victimGHPlayer, int lose)
    {
        return new Message(
                Message.Type.CHAT,
                Component.text(attackerGHPlayer.getName(), COLOR_PLAYER_NAME)
                         .append(Component.text(" 的猎杀黎明击中了 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(victimGHPlayer.getName(), COLOR_PLAYER_NAME))
                         .append(Component.text(", 夺取了 ", COLOR_TEXT_NORMAL))
                         .append(Component.text(lose, COLOR_VALUE))
                         .append(Component.text(" 点礼物!", COLOR_TEXT_NORMAL)),
                Message.LoadMode.IMMEDIATE,
                1
        );
    }
}
