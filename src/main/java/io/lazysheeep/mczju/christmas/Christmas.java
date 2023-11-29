package io.lazysheeep.mczju.christmas;

import io.lazysheeep.mczju.christmas.command.Chris;
import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Christmas extends JavaPlugin
{
    public static Cfg cfg;
    public static Objective scoreboardObj;


    public static class EventStats
    {
        public enum State
        {
            IDLE, READYING, PROGRESSING, PAUSED, FINISHED
        }
        public State state;
        public int timer;
    }
    public static EventStats eventStats = new EventStats();

    @Override
    public void onEnable()
    {
        // register events
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);

        // register commands
        this.getCommand("christmas").setExecutor(new Chris());

        // get scoreboard
        Scoreboard scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        scoreboardObj = scoreboard.getObjective("Christmas");
        if(scoreboardObj == null)
        {
            scoreboardObj = scoreboard.registerNewObjective("Christmas", Criteria.DUMMY, Component.text("Christmas"));
        }

        // load cfg
        cfg = new Cfg(this);
        cfg.load();

        // init event stats
        eventStats.state = EventStats.State.IDLE;
        eventStats.timer = 0;
    }

    @Override
    public void onDisable()
    {
        cfg.save();
    }
}
