package io.lazysheeep.mczju.christmas;

import io.lazysheeep.mczju.christmas.ui.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class CCommand implements CommandExecutor
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
                        sender.sendMessage(Component.text(Christmas.plugin.config.getGiftSpawnerLocations().toString()));
                    }
                    case "setter" ->    // get setter
                    {
                        if(sender instanceof Player player)
                        {
                            player.getInventory().addItem(CItemFactory.giftSpawnerSetter);
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
                        for (Location loc : Christmas.plugin.config.getGiftSpawnerLocations())
                        {
                            new CGift(loc, CGift.GiftType.NORMAL);
                        }
                        sender.sendMessage(CMessageFactory.getSpawnGiftMsg(Christmas.plugin.config.getGiftSpawnerLocations().size(), CGift.GiftType.NORMAL));
                    }
                    default -> // spawn <spawn_number>
                    {
                        int spawnAmount = Integer.parseInt(args[1]);
                        List<Location> spawnLocations = CUtil.randomPick(Christmas.plugin.config.getGiftSpawnerLocations(), spawnAmount);
                        for (Location loc : spawnLocations)
                        {
                            new CGift(loc, CGift.GiftType.NORMAL);
                        }
                        sender.sendMessage(CMessageFactory.getSpawnGiftMsg(spawnAmount, CGift.GiftType.NORMAL));
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
                        if (Christmas.plugin.eventStats.state == Christmas.EventStats.State.IDLE)
                        {
                            // set event state
                            Christmas.plugin.eventStats.state = Christmas.EventStats.State.READYING;
                            Christmas.plugin.eventStats.timer = 0;
                            // reset all player's score
                            for(OfflinePlayer player : Christmas.plugin.getServer().getOfflinePlayers())
                                Christmas.plugin.scoreboardObj.getScore(player).resetScore();
                            // flush player UI
                            for(Player player : Christmas.plugin.getServer().getOnlinePlayers())
                                Christmas.plugin.uiManager.flush(player, Message.Type.ACTIONBAR_PREFIX, Message.Type.ACTIONBAR_INFIX, Message.Type.ACTIONBAR_SUFFIX);
                            // clear all gifts
                            CGift.clearAll();
                            CGift.clearUnTracked();
                        }
                        else
                            sender.sendMessage(CMessageFactory.getEventCantStartMsg());
                    }
                    case "stop" -> // end the event
                    {
                        if (Christmas.plugin.eventStats.state == Christmas.EventStats.State.IDLE)
                            sender.sendMessage(CMessageFactory.getEventCantEndMsg());
                        else
                        {
                            // set event state
                            Christmas.plugin.eventStats.state = Christmas.EventStats.State.IDLE;
                            Christmas.plugin.eventStats.timer = 0;
                        }
                    }
                    case "stats" -> // print event stats
                    {
                        sender.sendMessage(CMessageFactory.getEventStatsMsg());
                    }
                    default -> { return false; }
                }
                else return false;
            }

            case "test" ->
            {
                if(sender instanceof Player player)
                {
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 0"), Message.LoadMode.REPLACE, 20));
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 1"), Message.LoadMode.WAIT, 20));
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 2"), Message.LoadMode.WAIT, 20));

                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_PREFIX, Component.text("actionbar_prefix "), Message.LoadMode.REPLACE, -1));
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, Component.text("actionbar_infix "), Message.LoadMode.REPLACE, 100));

                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 1"), Message.LoadMode.REPLACE, 20));
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 2"), Message.LoadMode.WAIT, 20));
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 3"), Message.LoadMode.WAIT, 20));
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 4"), Message.LoadMode.WAIT, 20));
                    Christmas.plugin.uiManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 5"), Message.LoadMode.WAIT,20));
                }
            }

            default -> { return false; }
        }
        else return false;
        return true;
    }
}
