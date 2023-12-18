package io.lazysheeep.gifthunting;

import io.lazysheeep.lazuliui.LazuliUI;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class GiftHunting extends JavaPlugin
{
    public static GiftHunting plugin;
    static GameManager gameManager;
    static Config config;
    Logger logger;
    World world;
    Objective scoreboardObj;


    @Override
    public void onEnable()
    {
        // set static reference
        plugin = this;
        logger = this.getLogger();
        gameManager = new GameManager();

        // load cfg
        config = new Config(this);
        config.load();

        // get world
        world = Bukkit.getServer().getWorld(config.worldName);
        if(world == null)
        {
            StringBuilder log = new StringBuilder("World \"" + config.worldName + "\" not found, please check!\nWorld list:\n");
            for(World world : Bukkit.getServer().getWorlds())
            {
                log.append(world.getName()).append(" ");
            }
            logger.log(Level.SEVERE, log.toString());
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // register event listener
        Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), this);
        Bukkit.getPluginManager().registerEvents(gameManager, this);

        // register commands
        PluginCommand command = this.getCommand("gifthunting");
        if(command != null)
            command.setExecutor(new CCommandExecutor());
        else
            logger.log(Level.SEVERE, "Can't get command! There must be something wrong!");

        // get scoreboard
        String scoreboardName = "GiftHunting";
        Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        this.scoreboardObj = scoreboard.getObjective(scoreboardName);
        if(scoreboardObj == null)
        {
            this.scoreboardObj = scoreboard.registerNewObjective(scoreboardName, Criteria.DUMMY, Component.text(scoreboardName));
            logger.log(Level.INFO, "Scoreboard \"" + scoreboardName + "\" not found, created one");
        }

        // set UI width
        LazuliUI.setActionbarInfixWidth(32);

        logger.log(Level.INFO, "Enabled");
    }

    @Override
    public void onDisable()
    {
        config.save();
    }
}
