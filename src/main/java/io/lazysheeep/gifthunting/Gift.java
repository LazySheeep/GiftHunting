package io.lazysheeep.gifthunting;


import io.lazysheeep.lazuliui.LazuliUI;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
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
import java.util.logging.Level;


class Gift
{
    enum GiftType
    {
        NORMAL(GiftHunting.config.clicksPerFetch_normal, GiftHunting.config.scorePerFetch_normal, GiftHunting.config.capacityInFetches_normal),
        SPECIAL(GiftHunting.config.clicksPerFetch_special, GiftHunting.config.scorePerFetch_special, GiftHunting.config.capacityInFetches_special);

        GiftType(int clicksPerFetch, int scorePerFetch, int capacityInFetches)
        {
            this.clicksPerFetch = clicksPerFetch;
            this.scorePerFetch = scorePerFetch;
            this.capacityInFetches = capacityInFetches;
        }

        public final int clicksPerFetch;
        public final int scorePerFetch;
        public final int capacityInFetches;
    }

    private static final List<Gift> giftPool = new ArrayList<>();
    public static int getGiftCount()
    {
        return giftPool.size();
    }
    static final String tagName = "GiftHunting";

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

    public static int clearGifts()
    {
        int counter = giftPool.size();
        for(Gift gift : giftPool)
        {
            gift.giftEntity.remove();
            gift.giftEntity = null;
        }
        GiftHunting.plugin.logger.log(Level.INFO, "Cleared" + giftPool.size() + " gifts!");
        giftPool.clear();
        return counter;
    }

    public static int clearUnTracked()
    {
        int counter = 0;
        for(ArmorStand e : GiftHunting.plugin.world.getEntitiesByClass(ArmorStand.class))
        {
            if(e.getScoreboardTags().contains(tagName) && getGift(e) == null)
            {
                e.remove();
                counter ++;
            }
        }
        GiftHunting.plugin.logger.log(Level.INFO, "Cleared" + counter + " untracked gifts!");
        return counter;
    }


    public ArmorStand giftEntity;
    public final int clicksPerFetch;
    public final int scorePerFetch;
    public final int capacityInFetches;
    public int remainingCapacity;
    public int clicksToNextFetch;

    public Gift(Location location, GiftType type)
    {
        this.clicksPerFetch = type.clicksPerFetch;
        this.scorePerFetch = type.scorePerFetch;
        this.capacityInFetches = type.capacityInFetches;
        this.remainingCapacity = this.capacityInFetches;
        this.clicksToNextFetch = this.clicksPerFetch;

        location.add(Util.getRandomOffset(0.4f, 0.0f, 0.4f));
        location.setYaw(Util.getRandomFloat(0.0f, 360.0f));
        this.giftEntity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.giftEntity.customName(Component.text("GiftHuntingGift"));
        this.giftEntity.addScoreboardTag(tagName);
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
        LazuliUI.sendMessage(player, MessageFactory.getGiftClickedActionbar(this));
        giftEntity.getWorld().playSound(giftEntity, Sound.BLOCK_WOOL_HIT, SoundCategory.MASTER, 1.0f, 1.0f);

        if(this.clicksToNextFetch <= 0)
        {
            if(this.remainingCapacity > 0) // fetch a gift
            {
                this.fetched(player);
            }
            if(this.remainingCapacity <= 0) // gift empty
                this.remove();
            else
                this.clicksToNextFetch = clicksPerFetch;
        }
    }

    private void fetched(Player player)
    {
        // send actionbar infix:
        LazuliUI.sendMessage(player, MessageFactory.getGiftFetchedActionbar(this));
        // send actionbar suffix list:
        // score: value +increment
        LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreIncreased(player, this.scorePerFetch));
        // play sound
        player.getWorld().playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER, 1.0f, 1.0f);
        // score increase
        GiftHunting.gameManager.addScore(player, this.scorePerFetch);
        // randomly give item
        if(Util.getRandomBool(0.5f))
        {
            player.getInventory().addItem(ItemFactory.booster);
            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
        }
        // update capacity
        this.remainingCapacity--;
    }

    public void remove()
    {
        giftEntity.remove();
        giftEntity = null;
        giftPool.remove(this);
    }
}
