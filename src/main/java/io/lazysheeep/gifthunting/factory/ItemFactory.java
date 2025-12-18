package io.lazysheeep.gifthunting.factory;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.lazysheeep.gifthunting.GiftHunting;

public class ItemFactory
{
    private static NamespacedKey key(String id) { return new NamespacedKey(GiftHunting.GetPlugin(), id); }

    public static boolean hasTag(ItemStack item, String id)
    {
        if(item == null) return false;
        if(!item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(key(id), PersistentDataType.BYTE);
    }

    public static boolean isNormalGiftSpawnerSetter(ItemStack item) { return hasTag(item, "normal_gift_spawner_setter"); }
    public static boolean isSpecialGiftSpawnerSetter(ItemStack item) { return hasTag(item, "special_gift_spawner_setter"); }
    public static boolean isBooster(ItemStack item) { return hasTag(item, "booster"); }
    public static boolean isClub(ItemStack item) { return hasTag(item, "club"); }
    public static boolean isStealer(ItemStack item) { return hasTag(item, "stealer"); }
    public static boolean isSilencer(ItemStack item) { return hasTag(item, "silencer"); }
    public static boolean isReflector(ItemStack item) { return hasTag(item, "reflector"); }
    public static boolean isRevolution(ItemStack item) { return hasTag(item, "revolution"); }
    public static boolean isSpeedUp(ItemStack item) { return hasTag(item, "speed_up"); }

    static public final ItemStack NormalGiftSpawnerSetter;
    static
    {
        NormalGiftSpawnerSetter = new ItemStack(Material.BLAZE_ROD, 1);
        Component displayName = Component.text("礼物生成点设定器", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("用于设定礼物生成点", NamedTextColor.AQUA));
        lore.add(Component.text("对方块右键使用", NamedTextColor.YELLOW));
        NormalGiftSpawnerSetter.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.getPersistentDataContainer().set(key("normal_gift_spawner_setter"), PersistentDataType.BYTE, (byte)1);
        });
    }

    static public final ItemStack SpecialGiftSpawnerSetter;
    static
    {
        SpecialGiftSpawnerSetter = new ItemStack(Material.BLAZE_ROD, 1);
        Component displayName = Component.text("特殊礼物生成点设定器", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("用于设定特殊礼物生成点", NamedTextColor.AQUA));
        lore.add(Component.text("对方块右键使用", NamedTextColor.YELLOW));
        SpecialGiftSpawnerSetter.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.getPersistentDataContainer().set(key("special_gift_spawner_setter"), PersistentDataType.BYTE, (byte)1);
        });
    }

    static public final ItemStack Booster;
    static
    {
        Booster = new ItemStack(Material.WIND_CHARGE, 1);
        Component displayName = Component.text("弹射", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("将自己向所指的方向弹射", NamedTextColor.AQUA));
        lore.add(Component.text("在助跑起跳时弹射效果最佳", NamedTextColor.AQUA));
        lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
        lore.add(Component.text("飞起来！", NamedTextColor.GRAY));
        Booster.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.getPersistentDataContainer().set(key("booster"), PersistentDataType.BYTE, (byte)1);
        });
    }

    static public final ItemStack Club;
    static
    {
        Club = new ItemStack(Material.STICK, 1);
        Component displayName = Component.text("木棍", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("看起来只是普通的木棍", NamedTextColor.AQUA));
        lore.add(Component.text("但是能够把人打飞", NamedTextColor.AQUA));
        lore.add(Component.text("那么代价是什么呢", NamedTextColor.GRAY));
        Club.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.getPersistentDataContainer().set(key("club"), PersistentDataType.BYTE, (byte)1);
        });
    }

    static public final ItemStack Stealer;
    static
    {
        Stealer = new ItemStack(Material.FISHING_ROD, 1);
        Component displayName = Component.text("钓礼物竿", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("可以偷取其他玩家的礼物", NamedTextColor.AQUA));
        lore.add(Component.text("钩中其他玩家后收杆", NamedTextColor.YELLOW));
        lore.add(Component.text("这是俺拾滴", NamedTextColor.GRAY));
        Stealer.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.FORTUNE, 1, true);
            itemMeta.getPersistentDataContainer().set(key("stealer"), PersistentDataType.BYTE, (byte)1);
        });
    }

    static public final ItemStack Silencer;
    static
    {
        Silencer = new ItemStack(Material.BONE_MEAL, 1);
        Component displayName = Component.text("沉默", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("使周围玩家无法开启礼物和使用道具", NamedTextColor.AQUA));
        lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
        lore.add(Component.text("为什么不说话，是不喜欢吗", NamedTextColor.GRAY));
        Silencer.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.getPersistentDataContainer().set(key("silencer"), PersistentDataType.BYTE, (byte)1);
        });
    }

    static public final ItemStack Reflector;
    static
    {
        Reflector = new ItemStack(Material.ENDER_EYE, 1);
        Component displayName = Component.text("识破", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("能够在 2 秒内", NamedTextColor.AQUA));
        lore.add(Component.text("反弹一次他人对你使用的技能", NamedTextColor.AQUA));
        lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
        lore.add(Component.text("接下来，时机很重要", NamedTextColor.GRAY));
        Reflector.editMeta(itemMeta ->
                          {
                              itemMeta.displayName(displayName);
                              itemMeta.lore(lore);
                              itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                              // tag
                              itemMeta.getPersistentDataContainer().set(key("reflector"), PersistentDataType.BYTE, (byte)1);
                          });
    }

    public static final ItemStack Revolution;
    static
    {
        Revolution = new ItemStack(Material.RED_DYE, 1);
        Component displayName = Component.text("革命", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("广播分数最高的玩家", NamedTextColor.AQUA));
        lore.add(Component.text("降低其移动速度", NamedTextColor.AQUA));
        lore.add(Component.text("并持续标记其一段时间", NamedTextColor.AQUA));
        lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
        lore.add(Component.text("也要做好被革命的觉悟", NamedTextColor.GRAY));
        Revolution.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.getPersistentDataContainer().set(key("revolution"), PersistentDataType.BYTE, (byte)1);
        });
    }

    public static final ItemStack SpeedUp;
    static
    {
        SpeedUp = new ItemStack(Material.SUGAR, 1);
        Component displayName = Component.text("迅捷", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("一段时间内提升移动速度", NamedTextColor.AQUA));
        lore.add(Component.text("以及礼物开启速度", NamedTextColor.AQUA));
        lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
        lore.add(Component.text("是暴风吸入的时候了", NamedTextColor.GRAY));
        SpeedUp.editMeta(itemMeta ->
        {
            itemMeta.displayName(displayName);
            itemMeta.lore(lore);
            itemMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
            itemMeta.getPersistentDataContainer().set(key("speed_up"), PersistentDataType.BYTE, (byte)1);
        });
    }

    static public final ItemStack Souvenir;
    static
    {
        Souvenir = new ItemStack(Material.PLAYER_HEAD, 1);
        Component displayName = Component.text("2024圣诞纪念", NamedTextColor.LIGHT_PURPLE);
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("MCZJU2024年圣诞活动纪念品", NamedTextColor.AQUA));
        lore.add(Component.text("游戏记录:", NamedTextColor.AQUA));
        lore.add(Component.text("圣诞活动纪念品，不会被清除"));
        Souvenir.editMeta(itemMeta ->
        {
            if(itemMeta instanceof SkullMeta skullMeta)
            {
                skullMeta.displayName(displayName);
                skullMeta.lore(lore);
                skullMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                PlayerProfile headProfile = Bukkit.createProfile(UUID.randomUUID());
                headProfile.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ3YTlmNmVkMDhkZDIxN2ZkZjA5ZjQ2NTJiZjZiN2FmNjIxZTFkNWY4OTYzNjA1MzQ5ZGE3Mzk5OGE0NDMifX19"));
                skullMeta.setPlayerProfile(headProfile);
            }
        });
    }

    public static void UpdateSouvenir(Player player, int rank, int totalPlayer, int score)
    {
        // if player don't have souvenir, give one
        PlayerInventory inventory = player.getInventory();
        ItemStack playerSouvenir = null;
        for(ItemStack item : inventory)
        {
            if(item != null && item.getType() == Souvenir.getType())
            {
                playerSouvenir = item;
                break;
            }
        }
        if(playerSouvenir == null)
        {
            inventory.addItem(Souvenir);
            for(ItemStack item : inventory)
            {
                if(item != null && item.getType() == Souvenir.getType())
                {
                    playerSouvenir = item;
                    break;
                }
            }
        }
        // update souvenir lore
        if(playerSouvenir != null)
        {
            playerSouvenir.editMeta(itemMeta ->
            {
                List<Component> lore = itemMeta.lore();
                if(lore != null)
                {
                    lore.removeLast();
                    lore.add(Component.text(player.getName(), NamedTextColor.YELLOW)
                        .append(Component.text(" - 排名: ", NamedTextColor.GOLD))
                        .append(Component.text(rank + "/" + totalPlayer, NamedTextColor.GREEN))
                        .append(Component.text(", 得分: ", NamedTextColor.GOLD))
                        .append(Component.text(score, NamedTextColor.GREEN)));
                    lore.add(Component.text("圣诞活动纪念品，不会被清除"));
                }
                itemMeta.lore(lore);
            });
        }
    }

    private ItemFactory() {}
}
