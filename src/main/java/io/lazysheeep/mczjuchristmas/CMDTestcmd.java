package io.lazysheeep.mczjuchristmas;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CMDTestcmd implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(sender instanceof Player player)
        {
            ItemStack diamondStack = new ItemStack(Material.DIAMOND, 16);
            player.getInventory().addItem(diamondStack);
        }

        return true;
    }
}
