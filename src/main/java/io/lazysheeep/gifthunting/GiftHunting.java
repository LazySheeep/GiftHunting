package io.lazysheeep.gifthunting;

import co.aikar.commands.PaperCommandManager;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.gifthunting.game.GameManager;
import io.lazysheeep.gifthunting.game.PlayerEventListener;
import io.lazysheeep.gifthunting.gift.GiftManager;
import io.lazysheeep.gifthunting.gift.GiftType;
import io.lazysheeep.gifthunting.player.GHPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;

public final class GiftHunting extends JavaPlugin implements Listener
{
    private static GiftHunting _Instance;

    public static GiftHunting GetPlugin()
    {
        return _Instance;
    }

    public static void Log(Level level, String message)
    {
        _Instance.getLogger().log(level, "[t" + _Instance.getServer().getCurrentTick() + "] " + message);
    }

    private GameManager _gameManager;

    public @NotNull GameManager getGameManager()
    {
        return _gameManager;
    }

    private GHPlayerManager _playerManager;

    public GHPlayerManager getPlayerManager()
    {
        return _playerManager;
    }

    private GiftManager _giftManager;

    public @NotNull GiftManager getGiftManager()
    {
        return _giftManager;
    }

    @Override
    public void onEnable()
    {
        // set static reference
        _Instance = this;

        // create managers
        _gameManager = new GameManager();
        _playerManager = new GHPlayerManager();
        _giftManager = new GiftManager();

        // register event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);

        // register commands
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new GiftHuntingCommand());

        // if config folder does not exist, create it and copy the default config
        if(!getDataFolder().exists())
        {
            getDataFolder().mkdir();
            saveResource("gifthunting.conf", false);
        }

        // load configurations
        reloadConfig();

        Log(Level.INFO, "Enabled");
    }

    @Override
    public void onDisable()
    {
        saveConfig();
        _giftManager.removeAllGifts();
    }

    public void reloadConfig()
    {
        loadConfig();
        _gameManager.loadConfig();
        GiftType.LoadConfig();
        _giftManager.loadConfig();
    }

    private HoconConfigurationLoader _configLoader;
    private ConfigurationNode _configRootNode;

    public @NotNull ConfigurationNode getConfigRootNode()
    {
        if(_configRootNode == null)
        {
            loadConfig();
        }
        return _configRootNode;
    }

    private void loadConfig()
    {
        if(_configLoader == null)
        {
            _configLoader = HoconConfigurationLoader.builder().path(Path.of(getDataFolder().getPath(), "gifthunting.conf")).build();
        }
        try
        {
            _configRootNode = _configLoader.load();
            if(_configRootNode.empty())
            {
                Log(Level.SEVERE, "Empty configuration");
            }
        }
        catch (ConfigurateException e)
        {
            Log(Level.SEVERE, "An error occurred while loading configuration at " + e.getMessage());
        }
    }

    public void saveConfig()
    {
        if(_configLoader != null && _configRootNode != null)
        {
            try
            {
                _giftManager.saveConfig();
                _configLoader.save(_configRootNode);
                Log(Level.INFO, "Configuration saved");
            }
            catch (ConfigurateException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    private void onServerTickStart(ServerTickStartEvent event)
    {
        _gameManager.tick();
        _playerManager.tick();
    }
}
