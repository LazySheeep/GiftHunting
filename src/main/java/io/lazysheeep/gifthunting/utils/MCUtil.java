package io.lazysheeep.gifthunting.utils;

import io.lazysheeep.gifthunting.factory.ItemFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class MCUtil
{
    public static List<Player> GetPlayersWithPermission(String permission)
    {
        List<Player> result = new ArrayList<>();
        for(Player player : Bukkit.getServer().getOnlinePlayers())
        {
            if(player.hasPermission(permission))
                result.add(player);
        }
        return result;
    }

    public static void RemovePlayerItem(Player player, ItemStack itemToRemove)
    {
        PlayerInventory inventory = player.getInventory();
        for(ItemStack item : inventory)
        {
            if(item != null && item.isSimilar(itemToRemove))
                inventory.remove(item);
        }
    }

    public static int GetFirstEmptyAndNotHeldSlot(PlayerInventory inventory)
    {
        for(int i = 0; i < inventory.getSize(); i ++)
        {
            if(inventory.getItem(i) == null && i != inventory.getHeldItemSlot())
            {
                return i;
            }
        }
        return -1;
    }

    public static void ClearInventory(Player player)
    {
        PlayerInventory inventory = player.getInventory();
        for(ItemStack item : inventory)
        {
            if(item!= null && item.getType() != ItemFactory.souvenir.getType())
            {
                inventory.remove(item);
            }
        }
    }
}
