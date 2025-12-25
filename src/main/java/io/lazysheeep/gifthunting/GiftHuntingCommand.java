package io.lazysheeep.gifthunting;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import io.lazysheeep.gifthunting.buffs.BindBuff;
import io.lazysheeep.gifthunting.buffs.BlewUpBuff;
import io.lazysheeep.gifthunting.buffs.Buff;
import io.lazysheeep.gifthunting.buffs.CounteringBuff;
import io.lazysheeep.gifthunting.buffs.DawnBuff;
import io.lazysheeep.gifthunting.buffs.OathBuff;
import io.lazysheeep.gifthunting.buffs.SilenceBuff;
import io.lazysheeep.gifthunting.buffs.SpeedBuff;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.game.GHStates;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.player.GHPlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

import static io.lazysheeep.gifthunting.factory.MessageFactory.*;

@CommandAlias("gifthunting")
@CommandPermission("op")
public class GiftHuntingCommand extends BaseCommand
{
    @Default
    @Description("GiftHunting default command")
    public void onDefault(CommandSender sender)
    {

    }

    @Subcommand("info")
    public class InfoCommand extends BaseCommand
    {
        @Subcommand("game")
        @Description("Get game info")
        public void onGame(CommandSender sender)
        {
            sender.sendMessage(MessageFactory.getGameStatsText(GiftHunting.GetPlugin().getGameInstance()));
        }

        @Subcommand("buff")
        public class BuffCommand extends BaseCommand
        {
            @Subcommand("current")
            @Description("List your current buffs")
            @CommandPermission("")
            public void onBuffCurrent(Player sender)
            {
                GameInstance gameInstance = GiftHunting.GetPlugin().getGameInstance();
                if (gameInstance == null)
                {
                    sender.sendMessage(Component.text("当前没有正在进行的游戏", COLOR_TEXT_VITAL));
                    return;
                }
                GHPlayer ghPlayer = GiftHunting.GetPlugin().getGameInstance().getPlayerManager().getGHPlayer(sender);
                if (ghPlayer == null)
                {
                    sender.sendMessage(Component.text("你不在当前游戏中", COLOR_TEXT_VITAL));
                    return;
                }
                List<Buff> list = ghPlayer.getBuffs();
                if (list.isEmpty())
                {
                    sender.sendMessage(Component.text("当前没有任何 Buff", COLOR_TEXT_VITAL));
                    return;
                }
                TextComponent.Builder b = Component.text().content("当前拥有的 Buff:\n").color(COLOR_TEXT_NORMAL);
                for (int i = 0; i < list.size(); i++)
                {
                    Buff bf = list.get(i);
                    b.append(bf.getDisplayName())
                     .append(Component.text(": ", COLOR_TEXT_NORMAL))
                     .append(Component.text(bf.getDescription(), COLOR_TEXT_NORMAL));
                    if (i < list.size() - 1)
                        b.append(Component.text("\n"));
                }
                sender.sendMessage(b.build());
            }

            private final List<Buff> _allBuffs = List.of(new SpeedBuff(), new SilenceBuff(), new BindBuff(), new DawnBuff(), new OathBuff());

            @Subcommand("all")
            @Description("List all buff types and descriptions")
            @CommandPermission("")
            public void onBuffAll(Player sender)
            {
                TextComponent.Builder b = Component.text().content("所有 Buff:\n").color(COLOR_TEXT_NORMAL);
                for (int i = 0; i < _allBuffs.size(); i++)
                {
                    Buff bf = _allBuffs.get(i);
                    b.append(bf.getDisplayName())
                     .append(Component.text(": ", COLOR_TEXT_NORMAL))
                     .append(Component.text(bf.getDescription(), COLOR_TEXT_NORMAL));
                    if (i < _allBuffs.size() - 1)
                        b.append(Component.text("\n"));
                }
                sender.sendMessage(b.build());
            }
        }
    }

    @Subcommand("game")
    public class GameCommand extends BaseCommand
    {
        @Subcommand("load")
        @Description("Load game")
        @CommandCompletion("@config_names")
        public void onLoad(CommandSender sender, String configName)
        {
            if (GiftHunting.GetPlugin().loadGameInstance(configName))
            {
                sender.sendMessage(Component.text("Loaded game instance", COLOR_INFO_GOOD));
            }
            else
            {
                sender.sendMessage(Component.text("Failed to load game instance", COLOR_TEXT_VITAL));
            }
        }

        @Subcommand("unload")
        @Description("Unload current game")
        public void onUnload(CommandSender sender)
        {
            if (GiftHunting.GetPlugin().unloadGameInstance())
            {
                sender.sendMessage(Component.text("Unloaded game instance", COLOR_INFO_GOOD));
            }
            else
            {
                sender.sendMessage(Component.text("No game instance loaded", COLOR_TEXT_VITAL));
            }
        }

        @Subcommand("setSpawn")
        @Description("Set game spawn point")
        public void onSetSpawn(Player senderPlayer)
        {
            GameInstance gameInstance = GiftHunting.GetPlugin().getGameInstance();
            if (gameInstance != null)
            {
                gameInstance.setGameSpawn(senderPlayer.getLocation());
                senderPlayer.sendMessage(Component.text("Game spawn set!", COLOR_INFO_GOOD));
            }
            else
            {
                senderPlayer.sendMessage(Component.text("No game instance loaded", COLOR_TEXT_VITAL));
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
                sender.sendMessage(Component.text("Game started!", COLOR_INFO_GOOD));
            }
            else
            {
                sender.sendMessage(Component.text("No game instance loaded", COLOR_TEXT_VITAL));
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
                sender.sendMessage(Component.text("Game stopped!", COLOR_TEXT_CAUTION));
            }
            else
            {
                sender.sendMessage(Component.text("No game instance loaded", COLOR_TEXT_VITAL));
            }
        }

        @Subcommand("save")
        @Description("Save config to file")
        public void onSave(CommandSender sender)
        {
            if (GiftHunting.GetPlugin().saveGHConfig())
            {
                sender.sendMessage(Component.text("Config saved!", COLOR_INFO_GOOD));
            }
            else
            {
                sender.sendMessage(Component.text("No game instance loaded", COLOR_TEXT_VITAL));
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
                sender.sendMessage(Component.text("Cleared " + counter + " gifts!", COLOR_TEXT_CAUTION));
            }

            @Subcommand("untracked")
            @Description("Clear all untracked gifts")
            public void onUntracked(CommandSender sender)
            {
                int counter = GiftHunting.GetPlugin().getGameInstance().getGiftManager().removeUnTracked();
                sender.sendMessage(Component.text("Cleared " + counter + " untracked gifts!", COLOR_TEXT_CAUTION));
            }
        }
    }

    @Subcommand("item")
    @CommandCompletion("@custom_item_ids")
    public void onItem(Player senderPlayer, String itemID)
    {
        CustomItem item = CustomItem.fromId(itemID);
        if (item == null)
        {
            senderPlayer.sendMessage(Component.text("Unknown item id: " + itemID, COLOR_TEXT_VITAL));
            return;
        }
        senderPlayer.getInventory().addItem(item.create());
    }
}
