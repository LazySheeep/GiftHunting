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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.lazysheeep.gifthunting.GiftHunting;
import org.jetbrains.annotations.Nullable;

public enum CustomItem
{
    NORMAL_GIFT_SPAWNER_SETTER("normal_gift_spawner_setter")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BLAZE_ROD, 1);
            Component displayName = Component.text("礼物生成点设定器", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("用于设定礼物生成点", NamedTextColor.AQUA));
            lore.add(Component.text("对方块右键使用", NamedTextColor.YELLOW));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SPECIAL_GIFT_SPAWNER_SETTER("special_gift_spawner_setter")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BLAZE_ROD, 1);
            Component displayName = Component.text("特殊礼物生成点设定器", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("用于设定特殊礼物生成点", NamedTextColor.AQUA));
            lore.add(Component.text("对方块右键使用", NamedTextColor.YELLOW));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    BOOSTER("booster")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.WIND_CHARGE, 1);
            Component displayName = Component.text("弹射", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("将自己向所指的方向弹射", NamedTextColor.AQUA));
            lore.add(Component.text("在助跑起跳时弹射效果最佳", NamedTextColor.AQUA));
            lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
            lore.add(Component.text("飞起来！", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    CLUB("club")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.STICK, 1);
            Component displayName = Component.text("木棍", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("看起来只是普通的木棍", NamedTextColor.AQUA));
            lore.add(Component.text("但是能够把人打飞", NamedTextColor.AQUA));
            lore.add(Component.text("那么代价是什么呢", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    STEALER("stealer")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.FISHING_ROD, 1);
            Component displayName = Component.text("钓礼物竿", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("可以偷取其他玩家的礼物", NamedTextColor.AQUA));
            lore.add(Component.text("钩中其他玩家后收杆", NamedTextColor.YELLOW));
            lore.add(Component.text("这是俺拾滴", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.FORTUNE, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SILENCER("silencer")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BONE_MEAL, 1);
            Component displayName = Component.text("沉默", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("使周围玩家无法开启礼物和使用道具", NamedTextColor.AQUA));
            lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
            lore.add(Component.text("为什么不说话，是不喜欢吗", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    COUNTER("counter")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.ENDER_EYE, 1);
            Component displayName = Component.text("识破", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("能够在 2 秒内", NamedTextColor.AQUA));
            lore.add(Component.text("反弹一次他人对你使用的技能", NamedTextColor.AQUA));
            lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
            lore.add(Component.text("接下来，时机很重要", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    REVOLUTION("revolution")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.RED_DYE, 1);
            Component displayName = Component.text("革命", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("广播分数最高的玩家", NamedTextColor.AQUA));
            lore.add(Component.text("降低其移动速度", NamedTextColor.AQUA));
            lore.add(Component.text("并持续标记其一段时间", NamedTextColor.AQUA));
            lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
            lore.add(Component.text("也要做好被革命的觉悟", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SPEED("speed")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.SUGAR, 1);
            Component displayName = Component.text("迅捷", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("一段时间内提升移动速度", NamedTextColor.AQUA));
            lore.add(Component.text("以及礼物开启速度", NamedTextColor.AQUA));
            lore.add(Component.text("右键使用", NamedTextColor.YELLOW));
            lore.add(Component.text("是暴风吸入的时候了", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SOUVENIR("souvenir")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.PLAYER_HEAD, 1);
            Component displayName = Component.text("2025圣诞纪念", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("MCZJU2025年圣诞活动纪念品", NamedTextColor.AQUA));
            lore.add(Component.text("游戏记录:", NamedTextColor.AQUA));
            lore.add(Component.text("纪念物品不会被清除", NamedTextColor.GRAY));
            it.editMeta(meta -> {
                if(meta instanceof SkullMeta skullMeta) {
                    skullMeta.displayName(displayName);
                    skullMeta.lore(lore);
                    skullMeta.addEnchant(Enchantment.UNBREAKING, 1, true);
                    PlayerProfile headProfile = Bukkit.createProfile(UUID.randomUUID());
                    headProfile.setProperty(new ProfileProperty("textures", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ3YTlmNmVkMDhkZDIxN2ZkZjA5ZjQ2NTJiZjZiN2FmNjIxZTFkNWY4OTYzNjA1MzQ5ZGE3Mzk5OGE0NDMifX19"));
                    skullMeta.setPlayerProfile(headProfile);
                    setTypeTag(skullMeta, this);
                }
            });
            return it;
        }
    },
    BIND("bind")
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.LEAD, 1);
            Component displayName = Component.text("束缚", NamedTextColor.LIGHT_PURPLE);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("使目标在一段时间内无法移动", NamedTextColor.AQUA));
            lore.add(Component.text("右键玩家使用", NamedTextColor.YELLOW));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    };

    public final String id;

    CustomItem(String id) { this.id = id; }

    public static @Nullable CustomItem fromId(String id)
    {
        if(id == null) return null;
        for(CustomItem item : values()) if(item.id.equals(id)) return item;
        return null;
    }

    public ItemStack create() { return new ItemStack(Material.STONE); }

    public static @Nullable CustomItem checkItem(ItemStack item)
    {
        if(item == null || !item.hasItemMeta()) return null;
        String id = item.getItemMeta().getPersistentDataContainer().get(ITEM_KEY, PersistentDataType.STRING);
        return CustomItem.fromId(id);
    }

    public static void UpdateSouvenir(Player player, int rank, int totalPlayer, int score)
    {
        // if player don't have souvenir, give one
        PlayerInventory inventory = player.getInventory();
        ItemStack playerSouvenir = null;
        for(ItemStack item : inventory)
        {
            if(item != null && checkItem(item) == CustomItem.SOUVENIR)
            {
                playerSouvenir = item;
                break;
            }
        }
        if(playerSouvenir == null)
        {
            inventory.addItem(CustomItem.SOUVENIR.create());
            for(ItemStack item : inventory)
            {
                if(item != null && checkItem(item) == CustomItem.SOUVENIR)
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
                    lore.add(Component.text("纪念物品不会被清除", NamedTextColor.GRAY));
                }
                itemMeta.lore(lore);
            });
        }
    }

    private static final NamespacedKey ITEM_KEY = new NamespacedKey(GiftHunting.GetPlugin(), "gifthunting_item");

    private static void setTypeTag(ItemMeta meta, CustomItem type)
    {
        meta.getPersistentDataContainer().set(ITEM_KEY, PersistentDataType.STRING, type.id);
    }
}
