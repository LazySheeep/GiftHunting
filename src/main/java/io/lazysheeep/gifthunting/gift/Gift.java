package io.lazysheeep.gifthunting.gift;


import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.Util;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.lazuliui.LazuliUI;
import io.lazysheeep.lazuliui.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.UUID;


public class Gift
{
    private static float LootProbability_club;
    private static float LootProbability_booster;

    public static final String TagName = "GiftHunting:Gift";

    public static void LoadConfig(ConfigurationNode configurationNode)
    {
        LootProbability_club = configurationNode.node("club").getFloat();
        LootProbability_booster = configurationNode.node("booster").getFloat();
    }

    private final ArmorStand _giftEntity;
    private final GiftType _type;

    private int _remainingCapacity;
    private int _clicksToNextFetch;

    public int getClicksPerFetch()
    {
        return _type.getClicksPerFetch();
    }

    public int getScorePerFetch()
    {
        return _type.getScorePerFetch();
    }

    public int getCapacityInFetches()
    {
        return _type.getCapacityInFetches();
    }

    public int getRemainingCapacity()
    {
        return _remainingCapacity;
    }

    public int getClicksToNextFetch()
    {
        return _clicksToNextFetch;
    }

    Gift(GiftType type, Location location)
    {
        this._type = type;
        this._remainingCapacity = type.getCapacityInFetches();
        this._clicksToNextFetch = type.getClicksPerFetch();

        location.add(Util.getRandomOffset(0.4f, 0.0f, 0.4f));
        location.setYaw(Util.getRandomFloat(0.0f, 360.0f));
        this._giftEntity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this._giftEntity.customName(Component.text(TagName));
        this._giftEntity.addScoreboardTag(TagName);
        this._giftEntity.setCanMove(false);
        this._giftEntity.setInvulnerable(true);
        this._giftEntity.setInvisible(true);

        PlayerProfile headProfile = Bukkit.createProfile(UUID.randomUUID());
        headProfile.setProperty(new ProfileProperty("textures", type.getTexture()));
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD, 1);
        headItem.editMeta(itemMeta -> ((SkullMeta) itemMeta).setPlayerProfile(headProfile));
        this._giftEntity.setItem(EquipmentSlot.HEAD, headItem);

        this._giftEntity.setMetadata(TagName, new FixedMetadataValue(GiftHunting.GetPlugin(), this));
    }

    public Location getLocation()
    {
        return this._giftEntity.getLocation().add(new Vector(0.0f, 1.95f, 0.0f));
    }

    public void clicked(GHPlayer ghPlayer)
    {
        this._clicksToNextFetch--;
        LazuliUI.sendMessage(ghPlayer.getPlayer(), MessageFactory.getGiftClickedActionbar(this));
        ghPlayer.getPlayer().playSound(_giftEntity, Sound.BLOCK_WOOL_HIT, SoundCategory.MASTER, 1.0f, 1.0f);

        if(this._clicksToNextFetch <= 0)
        {
            if(this._remainingCapacity > 0) // fetch a gift
            {
                this.fetched(ghPlayer);
            }
            if(this._remainingCapacity <= 0) // gift empty
                this.destroy();
            else
                this._clicksToNextFetch = _type.getClicksPerFetch();
        }
    }

    private void fetched(GHPlayer ghPlayer)
    {
        Player player = ghPlayer.getPlayer();
        // send actionbar infix:
        LazuliUI.sendMessage(player, MessageFactory.getGiftFetchedActionbar(this));
        // send actionbar suffix list:
        // score: value +increment
        LazuliUI.flush(player, Message.Type.ACTIONBAR_SUFFIX);
        LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(ghPlayer, _type.getScorePerFetch()));

        // play sound to the player
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER, 1.0f, 1.0f);
        // display particle
        player.spawnParticle(Particle.WAX_OFF, this.getLocation(), 8, 0.4f, 0.4f, 0.4f);

        // randomly give item
        if(Util.getRandomBool(LootProbability_club))
        {
            player.getInventory().addItem(ItemFactory.club);
            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
        }
        if(Util.getRandomBool(LootProbability_booster))
        {
            PlayerInventory inventory = player.getInventory();
            inventory.addItem(ItemFactory.booster);
            if(inventory.first(ItemFactory.booster) == inventory.getHeldItemSlot())
                inventory.setHeldItemSlot(inventory.firstEmpty());
            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
        }

        // score increase
        ghPlayer.addScore(_type.getScorePerFetch());
        // update capacity
        this._remainingCapacity--;
    }

    void destroy()
    {
        _giftEntity.remove();
    }
}
