package io.lazysheeep.mczjuchristmas.command;

import io.lazysheeep.mczjuchristmas.Gift;
import io.lazysheeep.mczjuchristmas.MCZJUChristmas;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Testcmd implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(args.length == 0)
        {
            return false;
        }

        if(args[0].equals("getnum"))
        {
            sender.sendMessage(Component.text("the value of \"gift_number\" is " + MCZJUChristmas.cfg.getInt("gift_number")));
        }
        else if(args[0].equals("setnum"))
        {
            int value = Integer.parseInt(args[1]);
            MCZJUChristmas.cfg.set("gift_number", value);
            sender.sendMessage(Component.text("the value of \"gift_number\" is set to " + value));
        }
        else if(args[0].equals("getloc"))
        {
            List<Location> locations = (List<Location>)MCZJUChristmas.cfg.getList("locations");
            sender.sendMessage(Component.text(locations.toString()));
        }
        else if(args[0].equals("addloc") && sender instanceof Player player)
        {
            List<Location> locations = (List<Location>)MCZJUChristmas.cfg.getList("locations");
            locations.add(player.getTargetBlock(8).getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f)));
            MCZJUChristmas.cfg.set("locations", locations);
        }
        else if(args[0].equals("spawn"))
        {
            List<Location> locations = (List<Location>)MCZJUChristmas.cfg.getList("locations");
            for(Location loc : locations)
            {
                new Gift(loc);
            }
        }
        else if(args[0].equals("remove"))
        {
            Gift.RemoveAll();
        }
        else if(args[0].equals("getspawner") && sender instanceof Player player)
        {
            ItemStack spawnerItem = new ItemStack(Material.STICK);
            spawnerItem.editMeta(itemMeta ->
            {
                itemMeta.displayName(Component.text("Spawner"));
            });
            player.getInventory().addItem(spawnerItem);
        }
        else
        {
            sender.sendMessage(Component.text("Invalid arguments"));
            return false;
        }
        return true;
    }
}
