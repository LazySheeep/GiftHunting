package io.lazysheeep.gifthunting.utils;

import io.lazysheeep.gifthunting.factory.CustomItem;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.util.Vector;

import java.util.*;

public class MCUtil
{
    public static float GetServerTickDeltaTime()
    {
        return 1.0f / Bukkit.getServer().getServerTickManager().getTickRate();
    }

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
        for(int i = 0; i < inventory.getSize(); i ++)
        {
            ItemStack item = inventory.getItem(i);
            if(item != null && CustomItem.checkItem(item) != CustomItem.SOUVENIR)
            {
                inventory.clear(i);
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
        int firstEmpty = inventory.firstEmpty();
        if(firstEmpty >= 0 && firstEmpty <= 8)
        {
            inventory.setHeldItemSlot(inventory.firstEmpty());
        }
        player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
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

    public static void RemoveItem(Player player, CustomItem customItem, int count)
    {
        if(player == null || customItem == null || count <= 0) return;
        PlayerInventory inv = player.getInventory();
        int remaining = count;
        for(int slot = 0; slot < inv.getSize() && remaining > 0; slot++)
        {
            ItemStack it = inv.getItem(slot);
            if(CustomItem.checkItem(it) == customItem)
            {
                int take = Math.min(remaining, it.getAmount());
                it.setAmount(it.getAmount() - take);
                if(it.getAmount() <= 0)
                {
                    inv.setItem(slot, null);
                }
                remaining -= take;
            }
        }
    }
}
