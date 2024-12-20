package io.lazysheeep.gifthunting.gift;

import io.lazysheeep.gifthunting.GiftHunting;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class GiftManager
{
    private final List<Location> _normalSpawners = new ArrayList<>();

    public List<Location> getNormalSpawners()
    {
        return Collections.unmodifiableList(_normalSpawners);
    }

    public int getNormalSpawnerCount()
    {
        return _normalSpawners.size();
    }

    public void addNormalSpawner(Location location)
    {
        _normalSpawners.add(location);
    }

    public boolean removeNormalSpawner(Location location)
    {
        return _normalSpawners.remove(location);
    }

    public void clearNormalSpawners()
    {
        _normalSpawners.clear();
    }

    private final List<Location> _specialSpawners = new ArrayList<>();

    public List<Location> getSpecialSpawners()
    {
        return Collections.unmodifiableList(_specialSpawners);
    }

    public int getSpecialSpawnerCount()
    {
        return _specialSpawners.size();
    }

    public void addSpecialSpawner(Location location)
    {
        _specialSpawners.add(location);
    }

    public boolean removeSpecialSpawner(Location location)
    {
        return _specialSpawners.remove(location);
    }

    public void clearSpecialSpawners()
    {
        _specialSpawners.clear();
    }

    public void loadConfig()
    {
        _normalSpawners.clear();
        _specialSpawners.clear();
        for(ConfigurationNode locationNode : GiftHunting.GetPlugin().getConfigRootNode().node("normalSpawners").childrenList())
        {
            double x = locationNode.node("x").getDouble(0);
            double y = locationNode.node("y").getDouble(0);
            double z = locationNode.node("z").getDouble(0);
            _normalSpawners.add(new Location(GiftHunting.GetPlugin().getGameManager().getGameWorld(), x, y, z));
        }
        GiftHunting.Log(Level.INFO, "Loaded " + _normalSpawners.size() + " normal spawners");
        for(ConfigurationNode locationNode : GiftHunting.GetPlugin().getConfigRootNode().node("specialSpawners").childrenList())
        {
            double x = locationNode.node("x").getDouble(0);
            double y = locationNode.node("y").getDouble(0);
            double z = locationNode.node("z").getDouble(0);
            _specialSpawners.add(new Location(GiftHunting.GetPlugin().getGameManager().getGameWorld(), x, y, z));
        }
        GiftHunting.Log(Level.INFO, "Loaded " + _normalSpawners.size() + " special spawners");
    }

    public void saveConfig()
    {
        try
        {
            ConfigurationNode normalSpawnersNode = GiftHunting.GetPlugin().getConfigRootNode().node("normalSpawners");
            normalSpawnersNode.setList(ConfigurationNode.class, List.of());
            for(Location location : _normalSpawners)
            {
                ConfigurationNode locationNode = normalSpawnersNode.appendListNode();
                locationNode.node("x").set(location.getX());
                locationNode.node("y").set(location.getY());
                locationNode.node("z").set(location.getZ());
            }
            GiftHunting.Log(Level.INFO, "Saved " + _normalSpawners.size() + " normal spawners");

            ConfigurationNode specialSpawnersNode = GiftHunting.GetPlugin().getConfigRootNode().node("specialSpawners");
            specialSpawnersNode.setList(ConfigurationNode.class, List.of());
            for(Location location : _specialSpawners)
            {
                ConfigurationNode locationNode = specialSpawnersNode.appendListNode();
                locationNode.node("x").set(location.getX());
                locationNode.node("y").set(location.getY());
                locationNode.node("z").set(location.getZ());
            }
            GiftHunting.Log(Level.INFO, "Saved " + _specialSpawners.size() + " special spawners");
        }
        catch (SerializationException e)
        {
            GiftHunting.Log(Level.SEVERE, e.getMessage());
        }
    }

    private final List<Gift> _gifts = new LinkedList<>();

    public List<Gift> getGifts()
    {
        return _gifts;
    }

    public int getGiftCount()
    {
        return _gifts.size();
    }

    public @Nullable Gift getGift(Entity entity)
    {
        for(var m : entity.getMetadata("GiftHunting:Gift"))
        {
            if(m.getOwningPlugin() == GiftHunting.GetPlugin() && m.value() instanceof Gift gift)
            {
                return gift;
            }
        }
        return null;
    }

    public Gift createGift(GiftType type, Location location)
    {
        Gift gift = new Gift(type, location);
        _gifts.add(gift);
        return gift;
    }

    public void removeGift(Gift gift)
    {
        _gifts.remove(gift);
        gift.destroy();
    }

    public int removeAllGifts()
    {
        int counter = _gifts.size();
        GiftHunting.Log(Level.INFO, "Cleared " + _gifts.size() + " gifts");
        for(Gift gift : _gifts)
        {
            gift.destroy();
        }
        _gifts.clear();
        return counter;
    }

    public int removeUnTracked()
    {
        int counter = 0;
        for(ArmorStand e : GiftHunting.GetPlugin().getGameManager().getGameWorld().getEntitiesByClass(ArmorStand.class))
        {
            if(e.getScoreboardTags().contains(Gift.TagName))
            {
                e.remove();
                counter ++;
            }
        }
        GiftHunting.Log(Level.INFO, "Cleared " + counter + " untracked gifts!");
        return counter;
    }
}
