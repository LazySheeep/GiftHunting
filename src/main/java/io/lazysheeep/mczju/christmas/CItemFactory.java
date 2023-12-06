package io.lazysheeep.mczju.christmas;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CItemFactory
{
    static public final ItemStack giftSpawnerSetter;
    static
    {
        giftSpawnerSetter = new ItemStack(Material.STICK, 1);
        Component displayName = Component.text("礼物生成点设定器", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("用于设定礼物生成点", NamedTextColor.AQUA));
        lore.add(Component.text("对方块右键使用", NamedTextColor.YELLOW));
        giftSpawnerSetter.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
        });
    }

    static public final ItemStack booster;
    static
    {
        booster = new ItemStack(Material.SLIME_BALL, 1);
        Component displayName = Component.text("弹力球", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("芜湖，起飞！", NamedTextColor.AQUA));
        lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
        booster.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
        });
    }

    private CItemFactory() {}
}
