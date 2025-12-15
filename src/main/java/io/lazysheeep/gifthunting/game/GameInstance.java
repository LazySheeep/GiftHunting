package io.lazysheeep.gifthunting.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.gift.GiftManager;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.player.GHPlayerManager;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.logging.Level;

public class GameInstance extends StateMachine<GameInstance, GHStates> implements Listener
{
    private World _gameWorld;
    private Location _gameSpawn;
    private int _readyStateDuration;
    private int _progressStateMaxDuration;
    private int _finishedStateDuration;
    private int _victoryScore;
    private int _spawnGiftCountPerPlayer;
    private float _newGiftBatchThreshold;
    private int _newGiftBatchDelay;
    private int _specialGiftSpawnInterval;
    private float _stealerGiveWhenHighestScore;
    private int _stealerGiveInterval;

    private GHPlayerManager _playerManager;
    private GiftManager _giftManager;

    public GHPlayerManager getPlayerManager()
    {
        return _playerManager;
    }

    public GiftManager getGiftManager()
    {
        return _giftManager;
    }

    public World getGameWorld()
    {
        return _gameWorld;
    }

    public Location getGameSpawn()
    {
        return _gameSpawn;
    }

    public int getVictoryScore()
    {
        return _victoryScore;
    }

    public int getSpawnGiftCountPerPlayer()
    {
        return _spawnGiftCountPerPlayer;
    }

    public float getNewGiftBatchThreshold()
    {
        return _newGiftBatchThreshold;
    }

    public int getNewGiftBatchDelay()
    {
        return _newGiftBatchDelay;
    }

    public int getSpecialGiftSpawnInterval()
    {
        return _specialGiftSpawnInterval;
    }

    public float getStealerGiveWhenHighestScore()
    {
        return _stealerGiveWhenHighestScore;
    }

    public int getStealerGiveInterval()
    {
        return _stealerGiveInterval;
    }

    public GameInstance()
    {
        super(GHStates.IDLE);

        _playerManager = new GHPlayerManager();
        _giftManager = new GiftManager();

        Bukkit.getPluginManager().registerEvents(_playerManager, GiftHunting.GetPlugin());
        Bukkit.getPluginManager().registerEvents(_giftManager, GiftHunting.GetPlugin());
    }

    public void tick()
    {
        _playerManager.tick();
        _giftManager.tick();

        super.tick();
    }

    public void loadConfig(ConfigurationNode configNode)
    {
        _gameWorld = Bukkit.getWorld(configNode.node("gameWorld").getString("world"));
        if (_gameWorld == null)
        {
            GiftHunting.Log(Level.SEVERE, "Game world not found!");
        }
        ConfigurationNode gameSpawnNode = configNode.node("gameSpawn");
        _gameSpawn = new Location(_gameWorld, gameSpawnNode.node("x").getDouble(0.0), gameSpawnNode.node("y")
                                                                                                   .getDouble(0.0), gameSpawnNode.node("z")
                                                                                                                                 .getDouble(0.0), gameSpawnNode.node("yaw")
                                                                                                                                                               .getFloat(0.0f), gameSpawnNode.node("pitch")
                                                                                                                                                                                             .getFloat(0.0f));
        _readyStateDuration = configNode.node("readyStateDuration").getInt(0);
        _progressStateMaxDuration = configNode.node("progressStateMaxDuration").getInt(Integer.MAX_VALUE);
        _finishedStateDuration = configNode.node("finishedStateDuration").getInt(0);
        _victoryScore = configNode.node("victoryScore").getInt(Integer.MAX_VALUE);
        _spawnGiftCountPerPlayer = configNode.node("spawnGiftCountPerPlayer").getInt(0);
        _newGiftBatchThreshold = configNode.node("newGiftBatchThreshold").getFloat(0.0f);
        _newGiftBatchDelay = configNode.node("newGiftBatchDelay").getInt(Integer.MAX_VALUE);
        _specialGiftSpawnInterval = configNode.node("specialGiftSpawnInterval").getInt(Integer.MAX_VALUE);
        _stealerGiveWhenHighestScore = configNode.node("stealerGiveWhenHighestScore").getFloat(0.0f);
        _stealerGiveInterval = configNode.node("stealerGiveInterval").getInt(Integer.MAX_VALUE);

        _giftManager.loadConfig(configNode);
    }

    public void saveConfig(ConfigurationNode configNode)
    {
        _giftManager.saveConfig(configNode);
    }

    @Override
    protected void onBeforeSwitch(GHStates newState, GHStates oldState)
    {

    }

    @Override
    protected void onAfterSwitch(GHStates oldState, GHStates newState)
    {
        GiftHunting.Log(Level.INFO, "Game state changed: " + oldState + " -> " + newState);
    }

    @Override
    protected State<GameInstance, GHStates> createState(GHStates state)
    {
        return switch (state)
        {
            case IDLE -> new IdleState();
            case READYING -> new ReadyingState(_readyStateDuration);
            case PROGRESSING -> new ProgressingState();
            case FINISHED -> new FinishedState(_finishedStateDuration);
        };
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onServerTickStart(ServerTickStartEvent event)
    {
        tick();
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

        if (item != null && clickedBlock != null && player.hasPermission("op") && GiftHunting.GetPlugin().getGameInstance().getCurrentStateEnum() == GHStates.IDLE)
        {
            // use giftSpawnerSetter to set or remove a spawner
            if (item.isSimilar(ItemFactory.NormalGiftSpawnerSetter))
            {
                if (action == Action.RIGHT_CLICK_BLOCK)
                {
                    Location newLocation = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    _giftManager.addNormalSpawner(newLocation);
                    LazuliUI.sendMessage(player, MessageFactory.getAddGiftSpawnerActionbar());
                }
                else if (action == Action.LEFT_CLICK_BLOCK)
                {
                    Location location = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    if (_giftManager.removeNormalSpawner(location))
                        LazuliUI.sendMessage(player, MessageFactory.getRemoveGiftSpawnerActionbar());
                    event.setCancelled(true);
                }
            }
            else if (item.isSimilar(ItemFactory.SpecialGiftSpawnerSetter))
            {
                if (action == Action.RIGHT_CLICK_BLOCK)
                {
                    Location newLocation = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    _giftManager.addSpecialSpawner(newLocation);
                    LazuliUI.sendMessage(player, MessageFactory.getAddGiftSpawnerActionbar());
                }
                else if (action == Action.LEFT_CLICK_BLOCK)
                {
                    Location location = clickedBlock.getLocation().toCenterLocation().add(0.0f, -0.95f, 0.0f);
                    if (_giftManager.removeSpecialSpawner(location))
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
        Gift gift = _giftManager.getGift(event.getRightClicked());
        GHPlayer ghPlayer = _playerManager.getGHPlayer(event.getPlayer());
        if(gift != null && ghPlayer != null)
        {
            int currentTick = GiftHunting.GetPlugin().getServer().getCurrentTick();
            if(GiftHunting.GetPlugin().getGameInstance().getCurrentStateEnum() == GHStates.PROGRESSING && currentTick - ghPlayer.lastClickGiftTime >= 4)
            {
                gift.clicked(ghPlayer);
                ghPlayer.lastClickGiftTime = currentTick;
            }
            event.setCancelled(true);
        }
    }
}