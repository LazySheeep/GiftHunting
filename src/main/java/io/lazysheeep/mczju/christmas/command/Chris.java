package io.lazysheeep.mczju.christmas.command;

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

public class Chris implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(args.length == 0) return false;
        else if(args[0].equals("get"))  // get ...
        {
            if(args.length < 2) return false;
            else if(args[1].equals("num"))  // get num
            {
                sender.sendMessage(Component.text("the value of \"gift_number\" is " + Christmas.cfg.giftNumber));
            }
            else if(args[1].equals("spawn"))    // get spawn
            {
                sender.sendMessage(Component.text(Christmas.cfg.giftSpawnerLocations.toString()));
            }
            else if(args[1].equals("setter") && sender instanceof Player player)    // get setter
            {
                ItemStack spawnerItem = new ItemStack(Material.STICK);
                spawnerItem.editMeta(itemMeta ->
                {
                    itemMeta.displayName(Component.text("Gift Spawner Setter"));
                });
                player.getInventory().addItem(spawnerItem);
            }
            else return false;
        }
        else if(args[0].equals("set"))  // set ... ...
        {
            if(args.length < 2) return false;
            else if(args[1].equals("num"))  // set num ...
            {
                if(args.length < 3)
                {
                    return false;
                }
                else    // set num <gift_number>
                {
                    Christmas.cfg.giftNumber = Integer.parseInt(args[2]);
                    sender.sendMessage(Component.text("the value of \"gift_number\" is set to " + Christmas.cfg.giftNumber));
                }
            }
            else if(args[1].equals("spawn") && sender instanceof Player player) // set spawn
            {
                Location newLocation = player.getTargetBlock(8).getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f));
                Christmas.cfg.giftSpawnerLocations.add(newLocation);
            }
            else return false;
        }
        else if(args[0].equals("spawn"))    // spawn ...
        {
            if(args.length < 2) return false;
            else if(args[1].equals("all"))  // spawn all
            {
                for(Location loc : Christmas.cfg.giftSpawnerLocations)
                {
                    new Gift(loc, Gift.GiftType.NORMAL);
                }
                sender.sendMessage(Component.text("Spawned " + Christmas.cfg.giftSpawnerLocations.size() + " gifts!"));
            }
            else    // spawn <spawn_number>
            {
                int spawnAmount = Integer.parseInt(args[1]);
                List<Location> spawnLocations = Util.RandomPick(Christmas.cfg.giftSpawnerLocations, spawnAmount);
                for(Location loc : spawnLocations)
                {
                    new Gift(loc, Gift.GiftType.NORMAL);
                }
                sender.sendMessage(Component.text("Spawned " + spawnAmount + " gifts!"));
            }
        }
        else if(args[0].equals("event"))
        {
            if(args.length < 2) return false;
            else if(args[1].equals("start"))
            {
                if(Christmas.eventStats.state == Christmas.EventStats.State.IDLE)
                {
                    Christmas.eventStats.state = Christmas.EventStats.State.READYING;
                    Christmas.eventStats.timer = 0;
                }
                else
                {
                    sender.sendMessage(Component.text("The event has already begun!"));
                }
            }
            else if(args[1].equals("end"))
            {

            }
            else return false;
        }
        else return false;
        return true;
    }
}
