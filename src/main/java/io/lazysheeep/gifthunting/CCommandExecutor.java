package io.lazysheeep.gifthunting;

import io.lazysheeep.uimanager.Message;
import io.lazysheeep.uimanager.UIManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CCommandExecutor implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(args.length >= 1) switch (args[0])
        {
            case "get" ->  // get ...
            {
                if(args.length >= 2) switch (args[1])
                {
                    case "spawner" ->    // get spawners
                    {
                        sender.sendMessage(Component.text(GiftHunting.plugin.config.getGiftSpawnerLocations().toString()));
                    }
                    case "setter" ->    // get setter
                    {
                        if(sender instanceof Player player)
                        {
                            player.getInventory().addItem(ItemFactory.giftSpawnerSetter);
                        }
                    }
                    default -> { return false; }
                }
                else return false;
            }

            case "spawn" ->    // spawn ...
            {
                if(args.length >= 2) switch (args[1])
                {
                    case "all" ->  // spawn all
                    {
                        for (Location loc : GiftHunting.plugin.config.getGiftSpawnerLocations())
                        {
                            new Gift(loc, Gift.GiftType.NORMAL);
                        }
                        sender.sendMessage(MessageFactory.getSpawnGiftMsg(GiftHunting.plugin.config.getGiftSpawnerLocations().size(), Gift.GiftType.NORMAL));
                    }
                    default -> // spawn <spawn_number>
                    {
                        int spawnAmount = Integer.parseInt(args[1]);
                        List<Location> spawnLocations = Util.randomPick(GiftHunting.plugin.config.getGiftSpawnerLocations(), spawnAmount);
                        for (Location loc : spawnLocations)
                        {
                            new Gift(loc, Gift.GiftType.NORMAL);
                        }
                        sender.sendMessage(MessageFactory.getSpawnGiftMsg(spawnAmount, Gift.GiftType.NORMAL));
                    }
                }
                else return false;
            }
            case "event" -> // event ...
            {
                if(args.length >= 2) switch (args[1])
                {
                    case "start" -> // start the event
                    {
                        if (GiftHunting.plugin.eventStats.state == GiftHunting.EventStats.State.IDLE)
                        {
                            // set event state
                            GiftHunting.plugin.eventStats.state = GiftHunting.EventStats.State.READYING;
                            GiftHunting.plugin.eventStats.timer = 0;
                            // reset all player's score
                            for(OfflinePlayer player : GiftHunting.plugin.getServer().getOfflinePlayers())
                                GiftHunting.plugin.scoreboardObj.getScore(player).resetScore();
                            // flush player UI
                            for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                                UIManager.flush(player, Message.Type.ACTIONBAR_PREFIX, Message.Type.ACTIONBAR_INFIX, Message.Type.ACTIONBAR_SUFFIX);
                            // clear all gifts
                            Gift.clearAll();
                            Gift.clearUnTracked();
                        }
                        else
                            sender.sendMessage(MessageFactory.getEventCantStartMsg());
                    }
                    case "stop" -> // end the event
                    {
                        if (GiftHunting.plugin.eventStats.state == GiftHunting.EventStats.State.IDLE)
                            sender.sendMessage(MessageFactory.getEventCantEndMsg());
                        else
                        {
                            // set event state
                            GiftHunting.plugin.eventStats.state = GiftHunting.EventStats.State.IDLE;
                            GiftHunting.plugin.eventStats.timer = 0;
                        }
                    }
                    case "stats" -> // print event stats
                    {
                        sender.sendMessage(MessageFactory.getEventStatsMsg());
                    }
                    default -> { return false; }
                }
                else return false;
            }

            case "test" ->
            {
                if(sender instanceof Player player)
                {
                    UIManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 0"), Message.LoadMode.REPLACE, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 1"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 2"), Message.LoadMode.WAIT, 20));

                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_PREFIX, Component.text("actionbar_prefix "), Message.LoadMode.REPLACE, -1));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, Component.text("actionbar_infix "), Message.LoadMode.REPLACE, 100));

                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 1"), Message.LoadMode.REPLACE, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 2"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 3"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 4"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 5"), Message.LoadMode.WAIT,20));
                }
            }

            default -> { return false; }
        }
        else return false;
        return true;
    }
}
