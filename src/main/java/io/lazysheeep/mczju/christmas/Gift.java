package io.lazysheeep.mczju.christmas;


import net.kyori.adventure.text.Component;
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

import java.util.ArrayList;
import java.util.List;


public class Gift
{
    public enum GiftType
    {
        NORMAL(5, 10, 1),
        SPECIAL(20, 10, 10);

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
        giftPool.clear();
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

        this.giftEntity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.giftEntity.customName(Component.text("gift"));
        this.giftEntity.setCanMove(false);
        this.giftEntity.setInvulnerable(true);
        this.giftEntity.setInvisible(true);

        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD, 1);
        headItem.editMeta(itemMeta ->
        {
            if(itemMeta instanceof SkullMeta skullMeta) skullMeta.setOwningPlayer(Bukkit.getOfflinePlayer("MHF_Present2"));
        });
        this.giftEntity.setItem(EquipmentSlot.HEAD, headItem);

        giftPool.add(this);
    }

    public void clicked(Player player)
    {
        this.clicksToNextFetch --;
        player.sendActionBar(Component.text("clicksToNextFetch: " + this.clicksToNextFetch));

        if(this.clicksToNextFetch <= 0)
        {
            if(this.capacityInFetches > 0)
            {
                Score score = Christmas.scoreboardObj.getScore(player);
                score.setScore(score.getScore() + this.scorePerFetch);

                player.sendMessage(Component.text("score +" + this.scorePerFetch));

                this.capacityInFetches --;
            }
            if(this.capacityInFetches <= 0)
            {
                this.remove();
            }
            else
            {
                this.clicksToNextFetch = clicksPerFetch;
            }
        }

    }

    public void remove()
    {
        giftEntity.remove();
        giftEntity = null;
        giftPool.remove(this);
    }

}
