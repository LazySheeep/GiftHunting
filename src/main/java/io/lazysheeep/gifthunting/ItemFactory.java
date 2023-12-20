package io.lazysheeep.gifthunting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

class ItemFactory
{
    static public final ItemStack giftSpawnerSetter;
    static
    {
        giftSpawnerSetter = new ItemStack(Material.BLAZE_ROD, 1);
        Component displayName = Component.text("礼物生成点设定器", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("用于设定礼物生成点", NamedTextColor.AQUA));
        lore.add(Component.text("对方块右键使用", NamedTextColor.YELLOW));
        giftSpawnerSetter.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        });
    }

    static public final ItemStack booster;
    static
    {
        booster = new ItemStack(Material.FIREWORK_STAR, 1);
        Component displayName = Component.text("弹射器", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("将自己向所指的方向弹射", NamedTextColor.AQUA));
        lore.add(Component.text("在助跑起跳时弹射效果最佳", NamedTextColor.AQUA));
        lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
        booster.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        });
    }

    static public final ItemStack club;
    static
    {
        club = new ItemStack(Material.STICK, 1);
        Component displayName = Component.text("木棍", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("虽然看起来只是普通的木棍", NamedTextColor.AQUA));
        lore.add(Component.text("但是能够把人打飞", NamedTextColor.AQUA));
        lore.add(Component.text("那么代价是什么呢", NamedTextColor.GRAY));
        club.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.KNOCKBACK, 5, true);
        });
    }

    static public final ItemStack stealer;
    static
    {
        stealer = new ItemStack(Material.WARPED_FUNGUS_ON_A_STICK, 1);
        Component displayName = Component.text("钓礼物竿", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("可以偷取其他玩家的礼物", NamedTextColor.AQUA));
        lore.add(Component.text("Ste---al!", NamedTextColor.AQUA));
        lore.add(Component.text("对其他玩家右键使用", NamedTextColor.YELLOW));
        stealer.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.DURABILITY, 1, true);
        });
    }

    static public final ItemStack souvenir;
    static
    {
        souvenir = new ItemStack(Material.PLAYER_HEAD, 1);
        Component displayName = Component.text("2023圣诞纪念", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("MCZJU2023年圣诞活动纪念品", NamedTextColor.AQUA));
        lore.add(Component.text("小心别放到地上了！", NamedTextColor.GRAY));
        lore.add(Component.text("游戏记录:", NamedTextColor.AQUA));
        souvenir.editMeta(itemMeta ->
        {
            if(itemMeta instanceof SkullMeta skullMeta)
            {
                skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Present2"));
                itemMeta.displayName(displayName);
                itemMeta.lore(lore);
                itemMeta.addEnchant(Enchantment.LOOT_BONUS_BLOCKS, 1, true);
            }
        });
    }

    static void updateSouvenir(Player player, int ranking, int totalPlayer, int score)
    {
        // if player don't have souvenir, give one
        PlayerInventory inventory = player.getInventory();
        ItemStack playerSouvenir = null;
        for(ItemStack item : inventory)
        {
            if(item != null && item.getType() == souvenir.getType())
            {
                playerSouvenir = item;
                break;
            }
        }
        if(playerSouvenir == null)
        {
            inventory.addItem(souvenir);
            for(ItemStack item : inventory)
            {
                if(item != null && item.getType() == souvenir.getType())
                {
                    playerSouvenir = item;
                    break;
                }
            }
        }
        // update souvenir lore
        if(playerSouvenir != null)
            playerSouvenir.editMeta(itemMeta ->
            {
                List<Component> lore = itemMeta.lore();
                if(lore != null)
                    lore.add(Component.text(player.getName(), NamedTextColor.YELLOW)
                            .append(Component.text(" - 排名: ", NamedTextColor.GOLD))
                            .append(Component.text(ranking + "/" + totalPlayer, NamedTextColor.GREEN))
                            .append(Component.text(", 得分: ", NamedTextColor.GOLD))
                            .append(Component.text(score, NamedTextColor.GREEN))
                    );
                itemMeta.lore(lore);
            });
    }

    private ItemFactory() {}
}
