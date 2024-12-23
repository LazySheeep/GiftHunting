package io.lazysheeep.gifthunting;

import co.aikar.commands.PaperCommandManager;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.gifthunting.game.GameManager;
import io.lazysheeep.gifthunting.game.GameState;
import io.lazysheeep.gifthunting.game.SkillManager;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.gift.GiftManager;
import io.lazysheeep.gifthunting.gift.GiftType;
import io.lazysheeep.gifthunting.player.GHPlayerManager;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import java.nio.file.Path;
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

    private SkillManager _skillManager;

    public @NotNull SkillManager getSkillManager()
    {
        return _skillManager;
    }

    private Objective _scoreObjective;
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
        _gameManager = new GameManager();
        _playerManager = new GHPlayerManager();
        _giftManager = new GiftManager();
        _skillManager = new SkillManager();

        // register event listener
        Bukkit.getPluginManager().registerEvents(this, this);
        Bukkit.getPluginManager().registerEvents(_giftManager, this);
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

        // load configurations
        reloadConfig();

        Log(Level.INFO, "Enabled");
    }

    @Override
    public void onDisable()
    {
        _gameManager.switchState(GameState.IDLE);
        saveConfig();
    }

    public void reloadConfig()
    {
        loadConfig();
        _gameManager.loadConfig();
        GiftType.LoadConfig();
        Gift.LoadConfig();
        _giftManager.loadConfig();
        _skillManager.loadConfig();
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
        _playerManager.tick();
        _gameManager.tick();
    }

    // player fall damage
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL && GiftHunting.GetPlugin().getPlayerManager().isGHPlayer(player))
            {
                event.setCancelled(true);
            }
        }
    }

    // protect itemFrame
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event)
    {
        Player player = event.getPlayer();
        if(!player.hasPermission("op") && GiftHunting.GetPlugin().getPlayerManager().isGHPlayer(player))
        {
            event.setCancelled(true);
        }
    }
}
