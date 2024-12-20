package io.lazysheeep.gifthunting;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.game.GameState;
import io.lazysheeep.lazuliui.LazuliUI;
import io.lazysheeep.lazuliui.Message;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("gifthunting")
@CommandPermission("op")
public class GiftHuntingCommand extends BaseCommand
{
    @Default
    @Description("GiftHunting default command")
    public void onDefault(CommandSender sender)
    {

    }

    @Subcommand("stats")
    @Description("Get stats")
    public void onStats(CommandSender sender)
    {
        sender.sendMessage(MessageFactory.getGameStatsText());
    }

    @Subcommand("start")
    @Description("Start the game")
    public void onStart(CommandSender sender)
    {
        if (GiftHunting.GetPlugin().getGameManager().switchState(GameState.READYING))
        {
            sender.sendMessage(MessageFactory.getEventStartText());
        }
        else
        {
            sender.sendMessage(MessageFactory.getEventCantStartText());
        }
    }

    @Subcommand("stop")
    @Description("Stop the game")
    public void onStop(CommandSender sender)
    {
        if (GiftHunting.GetPlugin().getGameManager().switchState(GameState.IDLE))
        {
            sender.sendMessage(MessageFactory.getEventStopText());
        }
        else
        {
            sender.sendMessage(MessageFactory.getEventCantEndText());
        }
    }

    @Subcommand("save")
    @Description("Save config to file")
    public void onSave(CommandSender sender)
    {
        GiftHunting.GetPlugin().saveConfig();
        sender.sendMessage(MessageFactory.getSaveConfigText());
    }

    @Subcommand("reload")
    @Description("Reload config from file")
    public void onReload(CommandSender sender)
    {
        GiftHunting.GetPlugin().reloadConfig();
        sender.sendMessage(MessageFactory.getReloadConfigText());
    }

    @Subcommand("get")
    public class GetCommand extends BaseCommand
    {
        @Subcommand("normalSetter")
        @Description("Get normal gift spawn setter")
        public void onNormalSetter(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(ItemFactory.normalGiftSpawnerSetter);
        }

        @Subcommand("specialSetter")
        @Description("Get special gift spawn setter")
        public void onSpecialSetter(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(ItemFactory.specialGiftSpawnerSetter);
        }

        @Subcommand("booster")
        @Description("Get booster")
        public void onBooster(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(ItemFactory.booster);
        }

        @Subcommand("club")
        @Description("Get club")
        public void onClub(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(ItemFactory.club);
        }
    }

    @Subcommand("clear")
    public class ClearCommand extends BaseCommand
    {
        @Subcommand("normalSpawner")
        @Description("Clear all normal spawners")
        public void onNormalSpawner(CommandSender sender)
        {
            GiftHunting.GetPlugin().getGiftManager().clearNormalSpawners();
        }

        @Subcommand("specialSpawner")
        @Description("Clear all special spawners")
        public void onSpecialSpawner(CommandSender sender)
        {
            GiftHunting.GetPlugin().getGiftManager().clearSpecialSpawners();
        }

        @Subcommand("gift")
        @Description("Clear all gifts")
        public void onGift(CommandSender sender)
        {
            int counter = GiftHunting.GetPlugin().getGiftManager().removeAllGifts();
            sender.sendMessage(MessageFactory.getClearAllGiftMsg(counter));
        }

        @Subcommand("untracked")
        @Description("Clear all untracked gifts")
        public void onUntracked(CommandSender sender)
        {
            int counter = GiftHunting.GetPlugin().getGiftManager().removeUnTracked();
            sender.sendMessage(MessageFactory.getClearUntrackedGiftMsg(counter));
        }
    }
}