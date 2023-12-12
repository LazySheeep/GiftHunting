package io.lazysheeep.gifthunting;


import io.lazysheeep.uimanager.Message;
import io.lazysheeep.uimanager.UIManager;
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
        GiftHunting.plugin.logger.log(Level.INFO, MessageFactory.getClearAllGiftLog(giftPool.size()));
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
        GiftHunting.plugin.logger.log(Level.INFO, MessageFactory.getClearUntrackedGiftLog(counter));
        return counter;
    }


    public ArmorStand giftEntity;
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
        UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getGiftClickedActionbar(this), Message.LoadMode.REPLACE, 10));

        if(this.clicksToNextFetch <= 0)
        {
            if(this.capacityInFetches > 0) // fetch a gift
            {
                this.fetched(player);
            }
            if(this.capacityInFetches <= 0) // gift empty
                this.remove();
            else
                this.clicksToNextFetch = clicksPerFetch;
        }
    }

    private void fetched(Player player)
    {
        Score score = GiftHunting.plugin.scoreboardObj.getScore(player);

        UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, MessageFactory.getProgressingActionbarSuffixWhenScoreIncreased(score.getScore(), this.scorePerFetch), Message.LoadMode.REPLACE, 20));

        score.setScore(score.getScore() + this.scorePerFetch);

        UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, MessageFactory.getProgressingActionbarSuffix(score.getScore()), Message.LoadMode.WAIT, -1));

        if(Util.getRandomBool(0.2f))
            player.getInventory().addItem(ItemFactory.booster);

        this.capacityInFetches --;
    }

    public void remove()
    {
        giftEntity.remove();
        giftEntity = null;
        giftPool.remove(this);
    }
}
