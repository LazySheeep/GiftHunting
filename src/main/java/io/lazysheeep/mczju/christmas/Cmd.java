package io.lazysheeep.mczju.christmas;

import io.lazysheeep.mczju.christmas.Christmas;
import io.lazysheeep.mczju.christmas.Gift;
import io.lazysheeep.mczju.christmas.Util;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
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
                        sender.sendMessage(Component.text("Spawned " + Christmas.plugin.cfg.giftSpawnerLocations.size() + " gifts!"));
                    }
                    default -> // spawn <spawn_number>
                    {
                        int spawnAmount = Integer.parseInt(args[1]);
                        List<Location> spawnLocations = Util.RandomPick(Christmas.plugin.cfg.giftSpawnerLocations, spawnAmount);
                        for (Location loc : spawnLocations)
                        {
                            new Gift(loc, Gift.GiftType.NORMAL);
                        }
                        sender.sendMessage(Component.text("Spawned " + spawnAmount + " gifts!"));
                    }
                }
                else return false;
            }
            case "event" ->
            {
                if(args.length >= 2) switch (args[1])
                {
                    case "start" ->
                    {
                        if (Christmas.plugin.eventStats.state == Christmas.EventStats.State.IDLE)
                        {
                            Christmas.plugin.eventStats.state = Christmas.EventStats.State.READYING;
                            Christmas.plugin.eventStats.timer = 0;
                        }
                        else
                            sender.sendMessage(Component.text("The event has already begun!"));
                    }
                    case "end" ->
                    {
                        if (Christmas.plugin.eventStats.state == Christmas.EventStats.State.IDLE)
                            sender.sendMessage(Component.text("The event is not in progress!"));
                        else
                        {
                            Christmas.plugin.eventStats.state = Christmas.EventStats.State.IDLE;
                            Christmas.plugin.eventStats.timer = 0;
                        }
                    }
                    case "stats" ->
                    {
                        String msg = "";
                        msg += "state: " + Christmas.plugin.eventStats.state.toString() + "\n";
                        msg += "timer: " + Christmas.plugin.eventStats.timer + "\n";
                        msg += "giftSpawners: " + Christmas.plugin.cfg.giftSpawnerLocations.size() + "\n";
                        msg += "trackedGifts: " + Gift.getNumber() + "\n";
                        sender.sendMessage(Component.text(msg));
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
