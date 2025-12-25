package io.lazysheeep.gifthunting.factory;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.lazysheeep.gifthunting.skills.Skill;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import io.lazysheeep.gifthunting.GiftHunting;
import org.jetbrains.annotations.Nullable;

import static io.lazysheeep.gifthunting.factory.MessageFactory.*;

public enum CustomItem
{
    SOUVENIR("souvenir", Material.PLAYER_HEAD)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.PLAYER_HEAD, 1);
            Component displayName = Component.text("2025圣诞纪念", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("MCZJU2025年圣诞活动纪念品", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("游戏记录:", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("纪念物品不会被清除", COLOR_HINT));
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
    NORMAL_GIFT_SPAWNER_SETTER("normal_gift_spawner_setter", Material.BLAZE_ROD)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BLAZE_ROD, 1);
            Component displayName = Component.text("礼物生成点设定器", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("用于设定礼物生成点", COLOR_ITEM_DESCRIPTION));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SPECIAL_GIFT_SPAWNER_SETTER("special_gift_spawner_setter", Material.BLAZE_ROD)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BLAZE_ROD, 1);
            Component displayName = Component.text("特殊礼物生成点设定器", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("用于设定特殊礼物生成点", COLOR_ITEM_DESCRIPTION));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    PLACEHOLDER("placeholder", Material.GRAY_STAINED_GLASS_PANE)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.GRAY_STAINED_GLASS_PANE, 1);
            it.editMeta(meta -> {
                meta.displayName(Component.text("锁定栏位", COLOR_HINT));
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SKILL_BOOSTER("skill_item_booster", Material.WIND_CHARGE, 0)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.WIND_CHARGE, 1);
            Component displayName = Component.text("技能: 弹射", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("冷却: ", COLOR_TITLE).append(Component.text(String.format("%.1fs", Skill.BOOST.cooldownDuration / 20f), COLOR_VALUE)));
            lore.add(Component.text("最大次数: ", COLOR_TITLE).append(Component.text(Skill.BOOST.maxCharges, COLOR_VALUE)));
            lore.add(Component.text("右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("将自己向所指方向弹射", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("在助跑起跳时弹射效果最佳", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("飞起来!", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SKILL_COUNTER("skill_item_counter", Material.ENDER_EYE, 1)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.ENDER_EYE, 1);
            Component displayName = Component.text("技能: 识破", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("冷却: ", COLOR_TITLE).append(Component.text(String.format("%.1fs", Skill.COUNTER.cooldownDuration / 20f), COLOR_VALUE)));
            lore.add(Component.text("持续时间: ", COLOR_TITLE).append(Component.text(String.format("%.1fs", Skill.COUNTER.activeDuration / 20f), COLOR_VALUE)));
            lore.add(Component.text("最大次数: ", COLOR_TITLE).append(Component.text(Skill.COUNTER.maxCharges, COLOR_VALUE)));
            lore.add(Component.text("右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("短时间内识破一次他人对自己使用的道具或技能", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("木棍: 反弹道具效果, 并夺取道具", COLOR_HINT));
            lore.add(Component.text("沉默: 反弹道具效果", COLOR_HINT));
            lore.add(Component.text("束缚: 反弹道具效果, 并夺取道具", COLOR_HINT));
            lore.add(Component.text("钓礼物杆: 反弹道具效果, 并夺取道具", COLOR_HINT));
            lore.add(Component.text("猎杀黎明: 免疫技能效果, 并获得誓约Buff", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SKILL_DAWN_BOW("skill_dawn_bow", Material.BOW, 2)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BOW, 1);
            Component displayName = Component.text("技能: 猎杀黎明", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            String cd = String.format("%.1fs", Skill.DAWN.cooldownDuration / 20f);
            lore.add(Component.text("冷却: ", COLOR_TITLE).append(Component.text(cd, COLOR_VALUE)));
            lore.add(Component.text("最大次数: ", COLOR_TITLE).append(Component.text(Skill.DAWN.maxCharges, COLOR_VALUE)));
            lore.add(Component.text("当场上存在有决胜Buff的玩家时解锁", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("且仅对有决胜Buff的玩家生效", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("击中目标时使其掉落一定比例的分数", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("○○を射ち堕とした日", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                meta.setUnbreakable(true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SKILL_DAWN_ARROW("skill_dawn_arrow", Material.ARROW, 29)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.ARROW, 1);
            Component displayName = Component.text("猎杀黎明之箭", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("用于技能: 猎杀黎明", COLOR_ITEM_DESCRIPTION));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SKILL_DETECT("skill_item_detect", Material.COMPASS, 8)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.COMPASS, 1);
            Component displayName = Component.text("技能: 探测", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            String cd = String.format("%.1fs", Skill.DETECT.cooldownDuration / 20f);
            String dur = String.format("%.1fs", Skill.DETECT.activeDuration / 20f);
            lore.add(Component.text("冷却: ", COLOR_TITLE).append(Component.text(cd, COLOR_VALUE)));
            lore.add(Component.text("持续时间: ", COLOR_TITLE).append(Component.text(dur, COLOR_VALUE)));
            lore.add(Component.text("最大次数: ", COLOR_TITLE).append(Component.text(Skill.DETECT.maxCharges, COLOR_VALUE)));
            lore.add(Component.text("右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("在持续时间内指向最近的礼物", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("优先指向超级礼物", COLOR_ITEM_DESCRIPTION));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
                if(meta instanceof CompassMeta compassMeta)
                {
                    compassMeta.setLodestoneTracked(false);
                    compassMeta.setLodestone(null);
                }
            });
            return it;
        }
    },
    STICK("stick", Material.STICK)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.STICK, 1);
            Component displayName = Component.text("木棍", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("看起来只是普通的木棍", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("但是能够把人打飞", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("集齐10个合成超级木棍", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SUPER_STICK("super_stick", Material.BREEZE_ROD)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BREEZE_ROD, 1);
            Component displayName = Component.text("超级木棍", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("用普通木棍合成的超级木棍", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("能真的把人打飞", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("请求起飞", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    STEALER("stealer", Material.FISHING_ROD)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.FISHING_ROD, 1);
            Component displayName = Component.text("钓礼物竿", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("钩中其它玩家后收杆", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("可以偷取其他玩家的礼物", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("这是俺拾滴", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.FORTUNE, 1, true);
                meta.setUnbreakable(true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SILENCER("silencer", Material.BONE_MEAL)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.BONE_MEAL, 1);
            Component displayName = Component.text("沉默", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("使周围玩家无法开启礼物和使用道具", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("为什么不说话，是不喜欢吗", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    SPEED("speed", Material.SUGAR)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.SUGAR, 1);
            Component displayName = Component.text("迅捷", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("一段时间内提升移动速度", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("以及礼物开启速度", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("是暴风吸入的时候了", COLOR_HINT));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    BIND("bind", Material.LEAD)
    {
        @Override public ItemStack create() {
            ItemStack it = new ItemStack(Material.LEAD, 1);
            Component displayName = Component.text("束缚", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("对其他玩家右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("使目标在一段时间内无法移动", COLOR_ITEM_DESCRIPTION));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    ABSORB("absorb", Material.HEART_OF_THE_SEA)
    {
        @Override public ItemStack create() {
            var it = new ItemStack(Material.HEART_OF_THE_SEA, 1);
            var displayName = Component.text("吸取", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("吸引周围所有礼物以及道具球", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("使得它们的目标成为自己", COLOR_ITEM_DESCRIPTION));
            it.editMeta(meta -> {
                meta.displayName(displayName);
                meta.lore(lore);
                meta.addEnchant(Enchantment.UNBREAKING, 1, true);
                setTypeTag(meta, this);
            });
            return it;
        }
    },
    BOMB_DRONE("bomb_drone", Material.FIREWORK_ROCKET)
    {
        @Override public ItemStack create() {
            var it = new ItemStack(Material.FIREWORK_ROCKET, 1);
            var displayName = Component.text("炸弹无人机", COLOR_ITEM_NAME);
            List<Component> lore = new ArrayList<>();
            lore.add(Component.text("右键使用", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("释放后自动飞向分数最高的玩家", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("并撞击目标产生爆炸", COLOR_ITEM_DESCRIPTION));
            lore.add(Component.text("使得范围内所有玩家掉落一些礼物", COLOR_ITEM_DESCRIPTION));
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
    public final Material material;
    public final int lockedSlot;

    CustomItem(String id, Material material) { this(id, material, -1); }
    CustomItem(String id, Material material, int lockedSlot) { this.id = id; this.material = material; this.lockedSlot = lockedSlot; }

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
                    lore.add(Component.text(player.getName(), COLOR_PLAYER_NAME)
                        .append(Component.text(" - ", COLOR_MISC))
                        .append(Component.text("排名: ", COLOR_TITLE))
                        .append(Component.text(rank + "/" + totalPlayer, COLOR_VALUE))
                        .append(Component.text(", 得分: ", COLOR_TITLE))
                        .append(Component.text(score, COLOR_VALUE)));
                    lore.add(Component.text("纪念物品不会被清除", COLOR_HINT));
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
