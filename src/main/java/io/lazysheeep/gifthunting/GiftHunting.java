package io.lazysheeep.gifthunting;

import co.aikar.commands.PaperCommandManager;
import io.lazysheeep.gifthunting.game.GHStates;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.game.SkillManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
import java.util.logging.Level;

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

    private GameInstance _gameInstance;
    private SkillManager _skillManager;
    private Objective _scoreObjective;

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

        // create managers
        _skillManager = new SkillManager();
        _skillManager.loadConfig(loadConfigRootNode());
        Bukkit.getPluginManager().registerEvents(_skillManager, this);

        // register commands
        PaperCommandManager commandManager = new PaperCommandManager(this);
        commandManager.registerCommand(new GiftHuntingCommand());

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
        _gameInstance.switchState(GHStates.IDLE);
        saveGHConfig();
    }

    public void loadGameInstance()
    {
        if(_gameInstance != null)
        {
            Log(Level.WARNING, "Game instance already exists");
            return;
        }
        _gameInstance = new GameInstance();
        _gameInstance.loadConfig(loadConfigRootNode());
        Bukkit.getPluginManager().registerEvents(_gameInstance, this);
    }

    public void unloadGameInstance()
    {

    }

    public void saveGHConfig()
    {
        ConfigurationNode configRootNode = loadConfigRootNode();
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
    }

    private HoconConfigurationLoader _configLoader;

    private ConfigurationNode loadConfigRootNode()
    {
        ConfigurationNode configRootNode = null;
        if(_configLoader == null)
        {
            _configLoader = HoconConfigurationLoader.builder().path(Path.of(getDataFolder().getPath(), "gifthunting.conf")).build();
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
