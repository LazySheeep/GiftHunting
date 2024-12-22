package io.lazysheeep.gifthunting.utils;

import io.lazysheeep.gifthunting.factory.ItemFactory;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

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
            if(item!= null && item.getType() != ItemFactory.Souvenir.getType())
            {
                inventory.remove(item);
            }
        }
    }

    public static void SpawnDustLineParticle(Location start, Location end, float step, Color color, float size)
    {
        double distance = start.distance(end);
        Vector stepVector = end.toVector().subtract(start.toVector()).normalize().multiply(step);
        int stepCount = (int)Math.ceil(distance / step);
        Location particleLocation = start.clone();
        for(int i = 0; i < stepCount; i++)
        {
            particleLocation.getWorld().spawnParticle(Particle.DUST, particleLocation, 1, new Particle.DustOptions(color, size));
            particleLocation.add(stepVector);
        }
    }

    public static void GiveItem(Player player, ItemStack itemStack)
    {
        PlayerInventory inventory = player.getInventory();
        inventory.addItem(itemStack);
        inventory.setHeldItemSlot(inventory.firstEmpty());
        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
    }
}
