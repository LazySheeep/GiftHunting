package io.lazysheeep.gifthunting.gift;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.game.GHStates;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.utils.RandUtil;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;

public class GiftManager implements Listener
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

    public GiftType GIFT_NORMAL;
    public GiftType GIFT_SPECIAL;

    private final GameInstance _gameInstance;

    public GiftManager(GameInstance gameInstance)
    {
        _gameInstance = gameInstance;
    }

    public void loadConfig(ConfigurationNode configNode)
    {
        GIFT_NORMAL = new GiftType(configNode.node("normalGift"));
        GIFT_SPECIAL = new GiftType(configNode.node("specialGift"));

        _normalSpawners.clear();
        _specialSpawners.clear();
        for(ConfigurationNode locationNode : configNode.node("normalSpawners").childrenList())
        {
            double x = locationNode.node("x").getDouble(0);
            double y = locationNode.node("y").getDouble(0);
            double z = locationNode.node("z").getDouble(0);
            _normalSpawners.add(new Location(_gameInstance.getGameWorld(), x, y, z));
        }
        GiftHunting.Log(Level.INFO, "Loaded " + _normalSpawners.size() + " normal spawners");
        for(ConfigurationNode locationNode : configNode.node("specialSpawners").childrenList())
        {
            double x = locationNode.node("x").getDouble(0);
            double y = locationNode.node("y").getDouble(0);
            double z = locationNode.node("z").getDouble(0);
            _specialSpawners.add(new Location(_gameInstance.getGameWorld(), x, y, z));
        }
        GiftHunting.Log(Level.INFO, "Loaded " + _specialSpawners.size() + " special spawners");
    }

    public void saveConfig(ConfigurationNode configNode) throws SerializationException
    {
        ConfigurationNode normalSpawnersNode = configNode.node("normalSpawners");
        normalSpawnersNode.setList(ConfigurationNode.class, List.of());
        for(Location location : _normalSpawners)
        {
            ConfigurationNode locationNode = normalSpawnersNode.appendListNode();
            locationNode.node("x").set(location.getX());
            locationNode.node("y").set(location.getY());
            locationNode.node("z").set(location.getZ());
        }
        GiftHunting.Log(Level.INFO, "Saved " + _normalSpawners.size() + " normal spawners");

        ConfigurationNode specialSpawnersNode = configNode.node("specialSpawners");
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

    private final List<Gift> _normalGifts = new LinkedList<>();
    private Gift _specialGift = null;

    public List<Gift> getNormalGifts()
    {
        return _normalGifts;
    }

    public int getNormalGiftCount()
    {
        return _normalGifts.size();
    }

    public @Nullable Gift getSpecialGift()
    {
        return _specialGift;
    }

    public boolean hasSpecialGift()
    {
        return _specialGift != null;
    }

    public void tick()
    {
        for(Gift gift : new ArrayList<>(_normalGifts))
        {
            if(gift.isEmpty())
            {
                removeGift(gift);
            }
        }
        if(_specialGift != null && _specialGift.isEmpty())
        {
            removeGift(_specialGift);
        }
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

    private void createNormalGift(Location location)
    {
        Location newLocation = location.clone();
        newLocation.add(RandUtil.nextVector(0.3f, 0.0f, 0.3f));
        newLocation.setYaw(RandUtil.nextFloat(0.0f, 360.0f));
        Gift gift = new Gift(GIFT_NORMAL, newLocation);
        _normalGifts.add(gift);
    }

    private void createSpecialGift(Location location)
    {
        if(_specialGift == null)
        {
            _specialGift = new Gift(GIFT_SPECIAL, location);
        }
        else
        {
            GiftHunting.Log(Level.WARNING, "Special gift already exists");
        }
    }

    public void removeGift(Gift gift)
    {
        if(gift.getType() == GIFT_NORMAL)
        {
            _normalGifts.remove(gift);
        }
        else if(gift.getType() == GIFT_SPECIAL)
        {
            _specialGift = null;
        }
        gift.destroy();
    }

    public int removeAllGifts()
    {
        int counter = _normalGifts.size();
        for(Gift gift : _normalGifts)
        {
            gift.destroy();
        }
        _normalGifts.clear();
        if(_specialGift != null)
        {
            _specialGift.destroy();
            _specialGift = null;
            counter ++;
        }
        GiftHunting.Log(Level.INFO, "Cleared " + counter + " gifts");
        return counter;
    }

    public int removeUnTracked()
    {
        int counter = 0;
        for(ArmorStand e : _gameInstance.getGameWorld().getEntitiesByClass(ArmorStand.class))
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

    public void spawnNormalGifts(int count)
    {
        for(Location location : RandUtil.Pick(_normalSpawners, count))
        {
            createNormalGift(location);
            location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 8, 0.4f, 0.4f, 0.4f);
            location.getWorld().playSound(location, Sound.BLOCK_WOOL_PLACE, SoundCategory.MASTER, 1.0f, 1.0f);
        }
        GiftHunting.Log(Level.INFO, "Spawned " + count + " normal gifts");
    }

    public void spawnSpecialGift()
    {
        Location location = RandUtil.Pick(_specialSpawners);
        createSpecialGift(location);
        location.getWorld().spawnParticle(Particle.HAPPY_VILLAGER, location, 16, 0.6f, 0.6f, 0.6f);
        location.getWorld().playSound(location, Sound.BLOCK_WOOL_PLACE, SoundCategory.MASTER, 1.0f, 1.0f);
        GiftHunting.Log(Level.INFO, "Spawned special gift");
    }

    // spawn setter
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        // get event attributes
        Player player = event.getPlayer();
        Action action = event.getAction();
        ItemStack item = event.getItem();
        Block clickedBlock = event.getClickedBlock();

        if (item != null && clickedBlock != null && player.hasPermission("op") && _gameInstance.getCurrentStateEnum() == GHStates.IDLE)
        {
            // use giftSpawnerSetter to set or remove a spawner
            if (item.isSimilar(ItemFactory.NormalGiftSpawnerSetter))
            {
                if (action == Action.RIGHT_CLICK_BLOCK)
                {
                    Location newLocation = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    addNormalSpawner(newLocation);
                    LazuliUI.sendMessage(player, MessageFactory.getAddGiftSpawnerActionbar());
                }
                else if (action == Action.LEFT_CLICK_BLOCK)
                {
                    Location location = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    if (removeNormalSpawner(location))
                        LazuliUI.sendMessage(player, MessageFactory.getRemoveGiftSpawnerActionbar());
                    event.setCancelled(true);
                }
            }
            else if (item.isSimilar(ItemFactory.SpecialGiftSpawnerSetter))
            {
                if (action == Action.RIGHT_CLICK_BLOCK)
                {
                    Location newLocation = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    addSpecialSpawner(newLocation);
                    LazuliUI.sendMessage(player, MessageFactory.getAddGiftSpawnerActionbar());
                }
                else if (action == Action.LEFT_CLICK_BLOCK)
                {
                    Location location = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    if (removeSpecialSpawner(location))
                        LazuliUI.sendMessage(player, MessageFactory.getRemoveGiftSpawnerActionbar());
                    event.setCancelled(true);
                }
            }
        }
    }

    // player click gift
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        Gift gift = getGift(event.getRightClicked());
        GHPlayer ghPlayer = _gameInstance.getPlayerManager().getGHPlayer(event.getPlayer());
        if(gift != null && ghPlayer != null)
        {
            int currentTick = GiftHunting.GetPlugin().getServer().getCurrentTick();
            if(_gameInstance.getCurrentStateEnum() == GHStates.PROGRESSING && currentTick - ghPlayer.lastClickGiftTime >= 4)
            {
                gift.clicked(ghPlayer);
                ghPlayer.lastClickGiftTime = currentTick;
            }
            event.setCancelled(true);
        }
    }
}
