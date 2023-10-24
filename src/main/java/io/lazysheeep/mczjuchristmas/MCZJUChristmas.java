package io.lazysheeep.mczjuchristmas;

import io.lazysheeep.mczjuchristmas.command.Testcmd;
import net.kyori.adventure.text.Component;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;

public class MCZJUChristmas extends JavaPlugin
{
    public static FileConfiguration cfg;
    public static Scoreboard scoreboard;
    public static Objective scoreboardObj;

    @Override
    public void onEnable()
    {
        // get config
        saveDefaultConfig();
        cfg = this.getConfig();

        // get scoreboard
        scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        scoreboardObj = scoreboard.getObjective("Christmas");
        if(scoreboardObj == null)
        {
            scoreboardObj = scoreboard.registerNewObjective("Christmas", Criteria.DUMMY, Component.text("Christmas"));
        }

        // register events
        getServer().getPluginManager().registerEvents(new TestListener(), this);

        // register commands
        this.getCommand("testCmd").setExecutor(new Testcmd());
    }

    @Override
    public void onDisable()
    {
        try {
            File file = new File(this.getDataFolder(), "config.yml");
            cfg.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
