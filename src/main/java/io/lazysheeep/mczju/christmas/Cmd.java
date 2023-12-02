package io.lazysheeep.mczju.christmas;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Cmd implements CommandExecutor
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
                        sender.sendMessage(Component.text(Christmas.plugin.cfg.giftSpawnerLocations.toString()));
                    }
                    case "setter" ->    // get setter
                    {
                        if(sender instanceof Player player)
                        {
                            ItemStack spawnerItem = new ItemStack(Material.STICK);
                            spawnerItem.editMeta(itemMeta ->
                            {
                                itemMeta.displayName(Component.text("Gift Spawner Setter"));
                            });
                            player.getInventory().addItem(spawnerItem);
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
                        for (Location loc : Christmas.plugin.cfg.giftSpawnerLocations)
                        {
                            new Gift(loc, Gift.GiftType.NORMAL);
                        }
                        sender.sendMessage(MessageFactory.getSpawnGiftMsg(Christmas.plugin.cfg.giftSpawnerLocations.size(), Gift.GiftType.NORMAL));
                    }
                    default -> // spawn <spawn_number>
                    {
                        int spawnAmount = Integer.parseInt(args[1]);
                        List<Location> spawnLocations = Util.randomPick(Christmas.plugin.cfg.giftSpawnerLocations, spawnAmount);
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
                        if (Christmas.plugin.eventStats.state == Christmas.EventStats.State.IDLE)
                        {
                            // set event state
                            Christmas.plugin.eventStats.state = Christmas.EventStats.State.READYING;
                            Christmas.plugin.eventStats.timer = 0;
                            // reset player's score
                            for(Player player : Christmas.plugin.getServer().getOnlinePlayers())
                            {
                                Christmas.plugin.scoreboardObj.getScore(player).setScore(0);
                            }
                            // clear untracked gifts
                            Gift.clearUnTracked();
                        }
                        else
                            sender.sendMessage(MessageFactory.getEventCantStartMsg());
                    }
                    case "end" -> // end the event
                    {
                        if (Christmas.plugin.eventStats.state == Christmas.EventStats.State.IDLE)
                            sender.sendMessage(MessageFactory.getEventCantEndMsg());
                        else
                        {
                            // set event state
                            Christmas.plugin.eventStats.state = Christmas.EventStats.State.IDLE;
                            Christmas.plugin.eventStats.timer = 0;
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
            default -> { return false; }
        }
        else return false;
        return true;
    }
}
