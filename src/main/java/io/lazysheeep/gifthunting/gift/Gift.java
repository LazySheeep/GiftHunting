package io.lazysheeep.gifthunting.gift;


import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.buffs.SilenceBuff;
import io.lazysheeep.gifthunting.buffs.SpeedBuff;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.entity.ItemOrb;
import io.lazysheeep.gifthunting.entity.ScoreOrb;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.RandUtil;
import io.lazysheeep.lazuliui.LazuliUI;
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

import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;


public class Gift
{
    public static final String TagName = "GiftHunting:Gift";

    private final ArmorStand _giftEntity;
    private final GiftType _type;

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

        this._remainingCapacity = type.capacityInFetches;
        this._clicksToNextFetch = type.clicksPerFetch;

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
        if(ghPlayer.hasBuff(SilenceBuff.class))
        {
            return;
        }

        if(ghPlayer.hasBuff(SpeedBuff.class))
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
            this.opened(ghPlayer);
            this._clicksToNextFetch = _type.clicksPerFetch;
        }
    }

    private void opened(GHPlayer ghPlayer)
    {
        if(isEmpty())
        {
            return;
        }

        Player player = ghPlayer.getPlayer();

        // Collect nearby GH players and compute weights
        Map<GHPlayer, Float> weightMap = getLootGHPlayers(ghPlayer);
        if(weightMap.isEmpty())
        {
            weightMap.put(ghPlayer, 1.0f);
        }

        double sumW = 0.0;
        for(float w : weightMap.values()) sumW += w;
        if(sumW <= 0.0) sumW = 1.0;

        // give score
        int totalScore = _type.scorePerFetch;
        if(totalScore < 1) totalScore = 1;
        for(Map.Entry<GHPlayer, Float> e : weightMap.entrySet())
        {
            double fp = totalScore * (e.getValue() / sumW);
            int share = Math.round((float) fp);
            if(share <= 0) continue;
            ghPlayer.getGameInstance().getEntityManager().addEntity(new ScoreOrb(this.getLocation(), null, e.getKey(), share));
        }

        // give loot
        List<ItemStack> loots = _type.lootTable.loot();
        for(ItemStack loot : loots)
        {
            GHPlayer pickedPlayer = RandUtil.PickWeighted(weightMap);
            ghPlayer.getGameInstance().getEntityManager().addEntity(new ItemOrb(this.getLocation(), null, pickedPlayer, loot));
        }

        // send actionbar infix:
        LazuliUI.sendMessage(player, MessageFactory.getGiftFetchedActionbar(this));

        // display particle
        player.getWorld().spawnParticle(Particle.WAX_OFF, this.getLocation(), 4, 0.2f, 0.2f, 0.2f);

        // update capacity
        this._remainingCapacity--;
    }

    void destroy()
    {
        _giftEntity.remove();
    }

    // Helper: collect nearby GH players and return weights using normalized generalized Gaussian
    private Map<GHPlayer, Float> getLootGHPlayers(GHPlayer source)
    {
        Map<GHPlayer, Float> map = new HashMap<>();
        Location loc = this.getLocation();
        double eNeg1 = Math.exp(-1.0);
        for(GHPlayer p : source.getGameInstance().getPlayerManager().getOnlineGHPlayers())
        {
            if(!p.isValid()) continue;
            if(p.getPlayer().getWorld() != loc.getWorld()) continue;
            double d = p.getLocation().distance(loc);
            if(d <= _type.lootRadius)
            {
                double x = Math.clamp(d / _type.lootRadius, 0.0, 1.0);
                double w = (Math.exp(-Math.pow(x, _type.lootWeightShape)) - eNeg1) / (1.0 - eNeg1);
                map.put(p, (float) w);
            }
        }
        return map;
    }
}
