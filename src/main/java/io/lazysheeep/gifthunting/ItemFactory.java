package io.lazysheeep.gifthunting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;

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
        lore.add(Component.text("可以击退别人", NamedTextColor.AQUA));
        club.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.KNOCKBACK, 3, true);
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

    private ItemFactory() {}
}
