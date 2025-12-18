package io.lazysheeep.gifthunting;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.lazysheeep.gifthunting.factory.CustomItems;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.game.GHStates;
import io.lazysheeep.gifthunting.game.GameInstance;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static io.lazysheeep.gifthunting.factory.MessageFactory.COLOR_GOOD;
import static io.lazysheeep.gifthunting.factory.MessageFactory.COLOR_VITAL;

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
        sender.sendMessage(MessageFactory.getGameStatsText(GiftHunting.GetPlugin().getGameInstance()));
    }

    @Subcommand("game")
    public class GameCommand extends BaseCommand
    {
        @Subcommand("load")
        @Description("Load game")
        public void onLoad(CommandSender sender)
        {
            GiftHunting.GetPlugin().loadGameInstance();
            sender.sendMessage(Component.text("Loaded game instance", COLOR_GOOD));
        }

        @Subcommand("setSpawn")
        @Description("Set game spawn point")
        public void onSetSpawn(Player senderPlayer)
        {
            GameInstance gameInstance = GiftHunting.GetPlugin().getGameInstance();
            if (gameInstance != null)
            {
                gameInstance.setGameSpawn(senderPlayer.getLocation());
                senderPlayer.sendMessage(Component.text("Game spawn set!", COLOR_GOOD));
            }
            else
            {
                senderPlayer.sendMessage(Component.text("No game instance loaded", COLOR_VITAL));
            }
        }

        @Subcommand("start")
        @Description("Start the game")
        public void onStart(CommandSender sender)
        {
            GameInstance gameInstance = GiftHunting.GetPlugin().getGameInstance();
            if (gameInstance != null)
            {
                gameInstance.switchState(GHStates.READYING);
                sender.sendMessage(MessageFactory.getEventStartText());
            }
            else
            {
                sender.sendMessage(Component.text("No game instance loaded", COLOR_VITAL));
            }
        }

        @Subcommand("stop")
        @Description("Stop the game")
        public void onStop(CommandSender sender)
        {
            GameInstance gameInstance = GiftHunting.GetPlugin().getGameInstance();
            if (gameInstance != null)
            {
                gameInstance.switchState(GHStates.IDLE);
                sender.sendMessage(MessageFactory.getEventStopText());
            }
            else
            {
                sender.sendMessage(Component.text("No game instance loaded", COLOR_VITAL));
            }
        }

        @Subcommand("save")
        @Description("Save config to file")
        public void onSave(CommandSender sender)
        {
            GiftHunting.GetPlugin().saveGHConfig();
            sender.sendMessage(MessageFactory.getSaveConfigText());
        }
    }

    @Subcommand("get")
    public class GetCommand extends BaseCommand
    {
        @Subcommand("normalSetter")
        @Description("Get normal gift spawn setter")
        public void onNormalSetter(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.NORMAL_GIFT_SPAWNER_SETTER.create());
        }

        @Subcommand("specialSetter")
        @Description("Get special gift spawn setter")
        public void onSpecialSetter(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.SPECIAL_GIFT_SPAWNER_SETTER.create());
        }

        @Subcommand("booster")
        @Description("Get booster")
        public void onBooster(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.BOOSTER.create());
        }

        @Subcommand("club")
        @Description("Get club")
        public void onClub(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.CLUB.create());
        }

        @Subcommand("stealer")
        @Description("Get stealer")
        public void onStealer(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.STEALER.create());
        }

        @Subcommand("silencer")
        @Description("Get silencer")
        public void onSilencer(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.SILENCER.create());
        }

        @Subcommand("reflector")
        @Description("Get reflector")
        public void onReflector(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.REFLECTOR.create());
        }

        @Subcommand("revolution")
        @Description("Get revolution")
        public void onRevolution(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.REVOLUTION.create());
        }

        @Subcommand("speedup")
        @Description("Get speedup")
        public void onSpeedup(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.SPEED_UP.create());
        }

        @Subcommand("souvenir")
        @Description("Get souvenir")
        public void onSouvenir(Player senderPlayer)
        {
            senderPlayer.getInventory().addItem(CustomItems.SOUVENIR.create());
        }
    }

    @Subcommand("clear")
    public class ClearCommand extends BaseCommand
    {
        @Subcommand("normalSpawner")
        @Description("Clear all normal spawners")
        public void onNormalSpawner(CommandSender sender)
        {
            GiftHunting.GetPlugin().getGameInstance().getGiftManager().clearNormalSpawners();
        }

        @Subcommand("specialSpawner")
        @Description("Clear all special spawners")
        public void onSpecialSpawner(CommandSender sender)
        {
            GiftHunting.GetPlugin().getGameInstance().getGiftManager().clearSpecialSpawners();
        }

        @Subcommand("gift")
        @Description("Clear all gifts")
        public void onGift(CommandSender sender)
        {
            int counter = GiftHunting.GetPlugin().getGameInstance().getGiftManager().removeAllGifts();
            sender.sendMessage(MessageFactory.getClearAllGiftMsg(counter));
        }

        @Subcommand("untracked")
        @Description("Clear all untracked gifts")
        public void onUntracked(CommandSender sender)
        {
            int counter = GiftHunting.GetPlugin().getGameInstance().getGiftManager().removeUnTracked();
            sender.sendMessage(MessageFactory.getClearUntrackedGiftMsg(counter));
        }
    }
}
