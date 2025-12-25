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
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;


public class Gift
{
    public static final String TagName = "GiftHunting:Gift";

    private final ItemDisplay _displayEntity;
    private final Interaction _interactionEntity;
    private final GiftType _type;

    private int _remainingCapacity;
    private int _clicksToNextFetch;

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

        PlayerProfile headProfile = Bukkit.createProfile(UUID.randomUUID());
        headProfile.setProperty(new ProfileProperty("textures", type.texture));
        ItemStack headItem = new ItemStack(Material.PLAYER_HEAD, 1);
        headItem.editMeta(itemMeta -> ((SkullMeta) itemMeta).setPlayerProfile(headProfile));

        float scale = (float) (type.size * 2.0);
        float halfSize = (float) (type.size * 0.5);

        Location displayLoc = location.clone().add(0.0, halfSize, 0.0);
        this._displayEntity = (ItemDisplay) displayLoc.getWorld().spawnEntity(displayLoc, EntityType.ITEM_DISPLAY);
        this._displayEntity.setItemStack(headItem);
        Transformation t = new Transformation(new Vector3f(0f, 0f, 0f), new AxisAngle4f(0f, 0f, 0f, 1f), new Vector3f(scale, scale, scale), new AxisAngle4f(0f, 0f, 0f, 1f));
        this._displayEntity.setTransformation(t);
        this._displayEntity.setItemDisplayTransform(ItemDisplay.ItemDisplayTransform.FIXED);
        this._displayEntity.addScoreboardTag(TagName);
        this._displayEntity.setMetadata(TagName, new FixedMetadataValue(GiftHunting.GetPlugin(), this));

        Location interactionLoc = location.clone();
        this._interactionEntity = (Interaction) interactionLoc.getWorld().spawnEntity(interactionLoc, EntityType.INTERACTION);
        float box = (float) Math.max(0.2, (float) type.size);
        this._interactionEntity.setInteractionWidth(box);
        this._interactionEntity.setInteractionHeight(box);
        this._interactionEntity.setResponsive(true);
        this._interactionEntity.customName(MessageFactory.getGiftNameComponent(this));
        this._interactionEntity.addScoreboardTag(TagName);
        this._interactionEntity.setMetadata(TagName, new FixedMetadataValue(GiftHunting.GetPlugin(), this));
    }

    public Location getLocation()
    {
        return this._displayEntity.getLocation();
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
        _interactionEntity.customName(MessageFactory.getGiftNameComponent(this));
        ghPlayer.getPlayer().playSound(_displayEntity, Sound.BLOCK_WOOL_HIT, SoundCategory.MASTER, 1.0f, 1.0f);

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

        Map<GHPlayer, Float> weightMap = getLootGHPlayers(ghPlayer);
        if(weightMap.isEmpty())
        {
            weightMap.put(ghPlayer, 1.0f);
        }

        double sumW = 0.0;
        for(float w : weightMap.values()) sumW += w;
        if(sumW <= 0.0) sumW = 1.0;

        int totalScore = _type.scorePerFetch;
        if(totalScore < 1) totalScore = 1;
        for(Map.Entry<GHPlayer, Float> e : weightMap.entrySet())
        {
            double fp = totalScore * (e.getValue() / sumW);
            int share = Math.round((float) fp);
            if(share <= 0) continue;
            ghPlayer.getGameInstance().getEntityManager().addEntity(new ScoreOrb(this.getLocation(), null, e.getKey(), share));
        }

        List<ItemStack> loots = _type.lootTable.loot();
        for(ItemStack loot : loots)
        {
            GHPlayer pickedPlayer = RandUtil.PickWeighted(weightMap);
            ghPlayer.getGameInstance().getEntityManager().addEntity(new ItemOrb(this.getLocation(), null, pickedPlayer, loot));
        }

        _displayEntity.getWorld().playSound(_displayEntity, Sound.ITEM_BUNDLE_DROP_CONTENTS, SoundCategory.MASTER, 1.0f, 1.0f);
        _displayEntity.getWorld().spawnParticle(Particle.WAX_OFF, this.getLocation(), 4, 0.2f, 0.2f, 0.2f);

        this._remainingCapacity--;
    }

    void destroy()
    {
        _displayEntity.remove();
        _interactionEntity.remove();
    }

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
