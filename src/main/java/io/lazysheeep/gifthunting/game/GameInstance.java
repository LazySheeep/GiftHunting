package io.lazysheeep.gifthunting.game;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.gift.GiftManager;
import io.lazysheeep.gifthunting.orbs.OrbManager;
import io.lazysheeep.gifthunting.player.GHPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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

    private final GHPlayerManager _playerManager;
    private final GiftManager _giftManager;
    private final SkillManager _skillManager;
    private final OrbManager _orbManager;

    public GHPlayerManager getPlayerManager()
    {
        return _playerManager;
    }

    public GiftManager getGiftManager()
    {
        return _giftManager;
    }

    public OrbManager getOrbManager()
    {
        return _orbManager;
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

        _playerManager = new GHPlayerManager(this);
        _giftManager = new GiftManager(this);
        _skillManager = new SkillManager(this);
        _orbManager = new OrbManager();

        Bukkit.getPluginManager().registerEvents(_playerManager, GiftHunting.GetPlugin());
        Bukkit.getPluginManager().registerEvents(_giftManager, GiftHunting.GetPlugin());
        Bukkit.getPluginManager().registerEvents(_skillManager, GiftHunting.GetPlugin());
    }

    public void tick()
    {
        _playerManager.tick();
        _giftManager.tick();
        _orbManager.tick();

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
        _gameSpawn = new Location(_gameWorld, gameSpawnNode.node("x").getDouble(0.0),
                                  gameSpawnNode.node("y").getDouble(0.0),
                                  gameSpawnNode.node("z").getDouble(0.0),
                                  gameSpawnNode.node("yaw").getFloat(0.0f),
                                  gameSpawnNode.node("pitch").getFloat(0.0f));
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
        _skillManager.loadConfig(configNode);
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
}