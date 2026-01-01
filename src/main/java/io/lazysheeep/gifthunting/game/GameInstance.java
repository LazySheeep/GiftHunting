package io.lazysheeep.gifthunting.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.gift.GiftManager;
import io.lazysheeep.gifthunting.entity.GHEntityManager;
import io.lazysheeep.gifthunting.player.GHPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import java.util.logging.Level;

public class GameInstance extends StateMachine<GameInstance, GHStates> implements Listener
{
    private Location _gameSpawn;
    private int _readyStateDuration;
    private int _finishedStateDuration;
    private int _victoryScore;
    private int _spawnGiftCountPerPlayer;
    private float _newGiftBatchThreshold;
    private int _newGiftBatchDelay;
    private int _specialGiftSpawnInterval;
    private float _stealerGiveWhenHighestScore;
    private int _stealerGiveInterval;

    private final GHPlayerManager _playerManager;
    private final GiftManager _giftManager;
    private final SkillManager _skillManager;
    private final GHEntityManager _GHEntityManager;

    public GHPlayerManager getPlayerManager()
    {
        return _playerManager;
    }

    public GiftManager getGiftManager()
    {
        return _giftManager;
    }

    public GHEntityManager getEntityManager()
    {
        return _GHEntityManager;
    }

    public World getGameWorld()
    {
        return _gameSpawn.getWorld();
    }

    public Location getGameSpawn()
    {
        return _gameSpawn;
    }

    public void setGameSpawn(Location location)
    {
        _gameSpawn = location;
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

        _playerManager = new GHPlayerManager(this);
        _giftManager = new GiftManager(this);
        _skillManager = new SkillManager(this);
        _GHEntityManager = new GHEntityManager();

        Bukkit.getPluginManager().registerEvents(_playerManager, GiftHunting.GetPlugin());
        Bukkit.getPluginManager().registerEvents(_giftManager, GiftHunting.GetPlugin());
        Bukkit.getPluginManager().registerEvents(_skillManager, GiftHunting.GetPlugin());
    }

    public void tick()
    {
        _playerManager.tick();
        _giftManager.tick();
        _GHEntityManager.tick(this);

        super.tick();
    }

    public void loadConfig(ConfigurationNode configNode)
    {
        ConfigurationNode gameSpawnNode = configNode.node("gameSpawn");
        World gameWorld = Bukkit.getWorld(gameSpawnNode.node("world").getString("world"));
        if (gameWorld == null)
        {
            GiftHunting.Log(Level.SEVERE, "Game world not found!");
        }
        _gameSpawn = new Location(gameWorld, gameSpawnNode.node("x").getDouble(0.0),
                                  gameSpawnNode.node("y").getDouble(0.0),
                                  gameSpawnNode.node("z").getDouble(0.0),
                                  gameSpawnNode.node("yaw").getFloat(0.0f),
                                  gameSpawnNode.node("pitch").getFloat(0.0f));
        _readyStateDuration = configNode.node("readyStateDuration").getInt(0);
        _finishedStateDuration = configNode.node("finishedStateDuration").getInt(0);
        _victoryScore = configNode.node("victoryScore").getInt(Integer.MAX_VALUE);
        _spawnGiftCountPerPlayer = configNode.node("spawnGiftCountPerPlayer").getInt(0);
        _newGiftBatchThreshold = configNode.node("newGiftBatchThreshold").getFloat(0.0f);
        _newGiftBatchDelay = configNode.node("newGiftBatchDelay").getInt(Integer.MAX_VALUE);
        _specialGiftSpawnInterval = configNode.node("specialGiftSpawnInterval").getInt(Integer.MAX_VALUE);
        _stealerGiveWhenHighestScore = configNode.node("stealerGiveWhenHighestScore").getFloat(0.0f);
        _stealerGiveInterval = configNode.node("stealerGiveInterval").getInt(Integer.MAX_VALUE);

        _giftManager.loadConfig(configNode.node("gifts"));
        _skillManager.loadConfig(configNode.node("skills"));
    }

    public void saveConfig(ConfigurationNode configNode) throws SerializationException
    {
        ConfigurationNode gameSpawnNode = configNode.node("gameSpawn");
        gameSpawnNode.node("world").set(_gameSpawn.getWorld().getName());
        gameSpawnNode.node("x").set(_gameSpawn.getX());
        gameSpawnNode.node("y").set(_gameSpawn.getY());
        gameSpawnNode.node("z").set(_gameSpawn.getZ());
        gameSpawnNode.node("yaw").set(_gameSpawn.getYaw());
        gameSpawnNode.node("pitch").set(_gameSpawn.getPitch());

        _giftManager.saveConfig(configNode.node("gifts"));
    }

    public void onDestroy()
    {
        switchState(GHStates.IDLE);

        HandlerList.unregisterAll(_playerManager);
        HandlerList.unregisterAll(_giftManager);
        HandlerList.unregisterAll(_skillManager);

        _playerManager.onDestroy();
        _giftManager.onDestroy();
        _GHEntityManager.onDestroy();
    }

    public boolean anyPlayerHasDawnBuff()
    {
        if(_currentState instanceof ProgressingState progressingState)
        {
            return progressingState.anyPlayerHasDawnBuff();
        }
        return false;
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
}