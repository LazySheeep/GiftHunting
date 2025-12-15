package io.lazysheeep.gifthunting.gift;


import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.gifthunting.utils.RandUtil;
import io.lazysheeep.lazuliui.LazuliUI;
import io.lazysheeep.lazuliui.Message;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.UUID;


public class Gift
{
    public static final String TagName = "GiftHunting:Gift";

    private final ArmorStand _giftEntity;
    private final GiftType _type;

    public final int clicksPerFetch;
    public final int scorePerFetch;
    public final int scoreVariation;
    public final int capacityInFetches;

    private int _remainingCapacity;
    private int _clicksToNextFetch;

    public ArmorStand getEntity()
    {
        return _giftEntity;
    }

    public GiftType getType()
    {
        return _type;
    }

    public int getRemainingCapacity()
    {
        return _remainingCapacity;
    }

    public int getClicksToNextFetch()
    {
        return _clicksToNextFetch;
    }

    public boolean isEmpty()
    {
        return _remainingCapacity <= 0;
    }

    Gift(GiftType type, Location location)
    {
        this._type = type;

        this.clicksPerFetch = type.clicksPerFetch;
        this.scorePerFetch = type.scorePerFetch;
        this.scoreVariation = type.scoreVariation;
        this.capacityInFetches = type.capacityInFetches;

        this._remainingCapacity = capacityInFetches;
        this._clicksToNextFetch = clicksPerFetch;

        this._giftEntity = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this._giftEntity.customName(Component.text(TagName));
        this._giftEntity.addScoreboardTag(TagName);
        this._giftEntity.setCanMove(false);
        this._giftEntity.setInvulnerable(true);
        this._giftEntity.setInvisible(true);

        PlayerProfile headProfile = Bukkit.createProfile(UUID.randomUUID());
        headProfile.setProperty(new ProfileProperty("textures", type.texture));
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
        if(ghPlayer.silenceTimer > 0)
        {
            return;
        }

        if(ghPlayer.speedUpTimer > 0)
        {
            this._clicksToNextFetch -= 2;
            if(this._clicksToNextFetch < 0)
            {
                this._clicksToNextFetch = 0;
            }
        }
        else
        {
            this._clicksToNextFetch--;
        }
        LazuliUI.sendMessage(ghPlayer.getPlayer(), MessageFactory.getGiftClickedActionbar(this));
        ghPlayer.getPlayer().playSound(_giftEntity, Sound.BLOCK_WOOL_HIT, SoundCategory.MASTER, 1.0f, 1.0f);

        if(this._clicksToNextFetch == 0)
        {
            this.fetched(ghPlayer);
            this._clicksToNextFetch = clicksPerFetch;
        }
    }

    private void fetched(GHPlayer ghPlayer)
    {
        if(isEmpty())
        {
            return;
        }

        Player player = ghPlayer.getPlayer();

        // calculate score
        int score = scorePerFetch + RandUtil.nextInt(-scoreVariation, scoreVariation);

        // send actionbar infix:
        LazuliUI.sendMessage(player, MessageFactory.getGiftFetchedActionbar(this));
        // send actionbar suffix list:
        // score: value +increment
        LazuliUI.flush(player, Message.Type.ACTIONBAR_SUFFIX);
        LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffixWhenScoreChanged(ghPlayer, score));

        // play sound to the player
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, SoundCategory.MASTER, 1.0f, 1.0f);
        // display particle
        player.getWorld().spawnParticle(Particle.WAX_OFF, this.getLocation(), 4, 0.2f, 0.2f, 0.2f);

        // give item
        if(RandUtil.nextBool(_type.lootProbability_club))
        {
            MCUtil.GiveItem(player, ItemFactory.Club);
        }
        if(RandUtil.nextBool(_type.lootProbability_booster))
        {
            MCUtil.GiveItem(player, ItemFactory.Booster);
        }
        if(RandUtil.nextBool(_type.lootProbability_silencer))
        {
            MCUtil.GiveItem(player, ItemFactory.Silencer);
        }
        if(RandUtil.nextBool(_type.lootProbability_reflector))
        {
            MCUtil.GiveItem(player, ItemFactory.Reflector);
        }
        if(RandUtil.nextBool(_type.lootProbability_revolution))
        {
            MCUtil.GiveItem(player, ItemFactory.Revolution);
        }
        if(RandUtil.nextBool(_type.lootProbability_speedUp))
        {
            MCUtil.GiveItem(player, ItemFactory.SpeedUp);
        }

        // score increase
        ghPlayer.addScore(score);
        // update capacity
        this._remainingCapacity--;
    }

    void destroy()
    {
        _giftEntity.remove();
    }
}
