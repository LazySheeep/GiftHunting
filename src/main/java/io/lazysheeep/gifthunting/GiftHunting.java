package io.lazysheeep.gifthunting;

import co.aikar.commands.PaperCommandManager;
import io.lazysheeep.gifthunting.game.GHStates;
import io.lazysheeep.gifthunting.game.GameInstance;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.io.File;
import java.nio.file.Path;
import java.util.logging.Level;

import io.lazysheeep.gifthunting.factory.CustomItem;

public final class GiftHunting extends JavaPlugin
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

    private @Nullable GameInstance _gameInstance;
    private Objective _scoreObjective;
    private @Nullable String _currentConfigName;

    public @Nullable GameInstance getGameInstance()
    {
        return _gameInstance;
    }

    public @NotNull Objective getScoreObjective()
    {
        return _scoreObjective;
    }

    @Override
    public void onEnable()
    {
        // set static reference
        _Instance = this;

        // register commands
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new GiftHuntingCommand());
        commandManager.getCommandCompletions().registerCompletion("custom_item_ids", c -> {
            java.util.ArrayList<String> ids = new java.util.ArrayList<>();
            for(CustomItem it : CustomItem.values()) ids.add(it.id);
            return ids;
        });
        commandManager.getCommandCompletions().registerCompletion("config_names", c -> {
            java.util.ArrayList<String> names = new java.util.ArrayList<>();
            File dir = getDataFolder();
            if(dir.exists())
            {
                File[] list = dir.listFiles((d, name) -> name.endsWith(".conf"));
                if(list != null)
                {
                    for(File f : list)
                    {
                        String n = f.getName();
                        if(n.endsWith(".conf")) n = n.substring(0, n.length()-5);
                        names.add(n);
                    }
                }
            }
            if(names.isEmpty()) names.add("gifthunting");
            return names;
        });

        // if config folder does not exist, create it and copy the default config
        if(!getDataFolder().exists())
        {
            getDataFolder().mkdir();
            saveResource("gifthunting.conf", false);
        }

        // init scoreboard
        _scoreObjective = Bukkit.getScoreboardManager().getMainScoreboard().getObjective("GiftHuntingScore");
        if(_scoreObjective == null)
        {
            _scoreObjective = Bukkit.getScoreboardManager()
                                    .getMainScoreboard()
                                    .registerNewObjective("GiftHuntingScore", Criteria.DUMMY, Component.text("🎁").color(NamedTextColor.GOLD));
            _scoreObjective.setDisplaySlot(null);
        }

        Log(Level.INFO, "Enabled");
    }

    @Override
    public void onDisable()
    {
        if(_gameInstance != null)
        {
            _gameInstance.switchState(GHStates.IDLE);
            saveGHConfig();
        }
    }

    public boolean loadGameInstance(String configName)
    {
        if(_gameInstance != null)
        {
            return false;
        }
        _currentConfigName = (configName == null || configName.isEmpty()) ? "gifthunting" : configName;
        _gameInstance = new GameInstance();
        _gameInstance.loadConfig(loadConfigRootNode(_currentConfigName));
        Bukkit.getPluginManager().registerEvents(_gameInstance, this);
        return true;
    }

    public boolean unloadGameInstance()
    {
        if(_gameInstance == null)
        {
            return false;
        }
        _gameInstance.onDestroy();
        _gameInstance = null;
        HandlerList.unregisterAll(this);
        return true;
    }

    public boolean saveGHConfig()
    {
        if(_gameInstance == null || _currentConfigName == null) return false;
        ConfigurationNode configRootNode = loadConfigRootNode(_currentConfigName);
        try
        {
            _gameInstance.saveConfig(configRootNode);
            _configLoader.save(configRootNode);
            Log(Level.INFO, "Configuration saved");
        }
        catch (ConfigurateException e)
        {
            throw new RuntimeException(e);
        }
        return true;
    }

    private HoconConfigurationLoader _configLoader;

    private ConfigurationNode loadConfigRootNode(String configName)
    {
        ConfigurationNode configRootNode = null;
        if(_configLoader == null)
        {
            _configLoader = HoconConfigurationLoader.builder().path(Path.of(getDataFolder().getPath(), configName + ".conf")).build();
        }
        try
        {
            configRootNode = _configLoader.load();
            if(configRootNode.empty())
            {
                Log(Level.SEVERE, "Empty configuration");
            }
        }
        catch (ConfigurateException e)
        {
            Log(Level.SEVERE, "An error occurred while loading configuration at " + e.getMessage());
        }

        return configRootNode;
    }
}
