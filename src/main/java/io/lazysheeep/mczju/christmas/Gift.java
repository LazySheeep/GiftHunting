package io.lazysheeep.mczju.christmas;


import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.apache.logging.log4j.message.MessageFactory;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scoreboard.Score;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;

import java.util.ArrayList;
import java.util.List;


public class Gift
{
    public enum GiftType
    {
        NORMAL(Christmas.plugin.cfg.clicksPerFetch_normal, Christmas.plugin.cfg.scorePerFetch_normal, Christmas.plugin.cfg.capacityInFetches_normal),
        SPECIAL(Christmas.plugin.cfg.clicksPerFetch_special, Christmas.plugin.cfg.scorePerFetch_special, Christmas.plugin.cfg.capacityInFetches_special);

        private GiftType(int clicksPerFetch, int scorePerFetch, int capacityInFetches)
        {
            this.clicksPerFetch = clicksPerFetch;
            this.scorePerFetch = scorePerFetch;
            this.capacityInFetches = capacityInFetches;
        }

        public final int clicksPerFetch;
        public final int scorePerFetch;
        public final int capacityInFetches;
    }

    private static final List<Gift> giftPool = new ArrayList<Gift>();
    public static int getNumber()
    {
        return giftPool.size();
    }

    public static Gift getGift(Entity entity)
    {
        for(Gift gift : giftPool)
        {
            if(gift.giftEntity == entity)
            {
                return gift;
            }
        }
        return null;
    }

    public static void clearAll()
    {
        for(Gift gift : giftPool)
        {
            gift.giftEntity.remove();
            gift.giftEntity = null;
        }
        Christmas.plugin.getServer().broadcast(Component.text("Cleared " + giftPool.size() + " gifts!"), "christmas.op");
        giftPool.clear();
    }

    public static void clearUnTracked()
    {
        int counter = 0;
        for(ArmorStand e : Christmas.world.getEntitiesByClass(ArmorStand.class))
        {
            if(e.getScoreboardTags().contains("Christmas") && getGift(e) == null)
            {
                e.remove();
                counter ++;
            }
        }
        Christmas.plugin.getServer().broadcast(Component.text("Cleared " + counter + " untracked gifts!"), "christmas.op");
    }


    public ArmorStand giftEntity = null;
    public final int clicksPerFetch;
    public final int scorePerFetch;
    public int capacityInFetches;
    public int clicksToNextFetch;

    public Gift(Location location, GiftType type)
    {
        this.clicksPerFetch = type.clicksPerFetch;
        this.scorePerFetch = type.scorePerFetch;
        this.capacityInFetches = type.capacityInFetches;
        this.clicksToNextFetch = clicksPerFetch;

        location.add(Util.getRandomOffset(0.4f, 0.0f, 0.4f));
        location.setYaw(Util.getRandomFloat(0.0f, 360.0f));
        this.giftEntity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.giftEntity.customName(Component.text("ChristmasGift"));
        this.giftEntity.addScoreboardTag("Christmas");
        this.giftEntity.setCanMove(false);
        this.giftEntity.setInvulnerable(true);
        this.giftEntity.setInvisible(true);

        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD, 1);
        headItem.editMeta(itemMeta ->
        {
            if(itemMeta instanceof SkullMeta skullMeta) switch (type)
            {
                case NORMAL -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Present2"));
                case SPECIAL -> skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Present1"));
            }
        });
        this.giftEntity.setItem(EquipmentSlot.HEAD, headItem);

        giftPool.add(this);
    }

    public void clicked(Player player)
    {
        this.clicksToNextFetch --;
        player.sendActionBar(getClickMessage());

        if(this.clicksToNextFetch <= 0)
        {
            if(this.capacityInFetches > 0) // fetch a gift
            {
                Score score = Christmas.plugin.scoreboardObj.getScore(player);
                score.setScore(score.getScore() + this.scorePerFetch);

                player.sendActionBar(Component.text("开启礼物 分数+" + this.scorePerFetch));

                this.capacityInFetches --;
            }
            if(this.capacityInFetches <= 0) // gift empty
            {
                this.remove();
            }
            else
            {
                this.clicksToNextFetch = clicksPerFetch;
            }
        }
    }

    private Component getClickMessage()
    {
        Component result = Component.text("开启中>>>", NamedTextColor.AQUA);
        result = result.append(Component.text(" [", NamedTextColor.YELLOW));
        int pp = (int)(20*((float)this.clicksToNextFetch/this.clicksPerFetch));
        result = result.append(Component.text("|".repeat(pp), NamedTextColor.GREEN));
        result = result.append(Component.text("|".repeat(20-pp), NamedTextColor.GRAY));
        result = result.append(Component.text("] ", NamedTextColor.YELLOW));
        result = result.append(Component.text("[*]".repeat(this.capacityInFetches-1), NamedTextColor.YELLOW));
        return result;
    }

    public void remove()
    {
        giftEntity.remove();
        giftEntity = null;
        giftPool.remove(this);
    }

}
