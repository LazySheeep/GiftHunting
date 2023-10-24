package io.lazysheeep.mczjuchristmas;


import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Skull;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class Gift
{
    private static final List<Gift> giftPool = new ArrayList<Gift>();
    public ArmorStand entity;

    public Gift(Location location)
    {
        entity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        entity.customName(Component.text("gift"));
        entity.setCanMove(false);

        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD, 1);
        headItem.editMeta(itemMeta ->
        {
            if(itemMeta instanceof SkullMeta skullMeta) skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Present2"));
        });
        entity.setItem(EquipmentSlot.HEAD, headItem);

        giftPool.add(this);
    }

    public static void RemoveAll()
    {
        for(Gift g : giftPool)
        {
            g.entity.remove();
        }
        giftPool.clear();
    }
}
