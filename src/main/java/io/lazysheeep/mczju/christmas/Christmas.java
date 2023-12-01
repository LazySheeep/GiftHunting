package io.lazysheeep.mczju.christmas;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class Christmas extends JavaPlugin
{
    public static Christmas plugin;
    public static World world;
    public Cfg cfg;
    public Objective scoreboardObj;

    public static class EventStats
    {
        public enum State
        {
            IDLE, READYING, PROGRESSING, PAUSED, FINISHED
        }
        public State state;
        public int timer;
    }
    public EventStats eventStats = new EventStats();

    @Override
    public void onEnable()
    {
        // load cfg
        this.cfg = new Cfg(this);
        this.cfg.load();

        // set static reference
        plugin = this;
        world = this.getServer().getWorld(cfg.worldName);
        if(world == null) this.getServer().sendMessage(Component.text("World \"" + cfg.worldName + "\" not found!"));

        // register events
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);

        // register commands
        this.getCommand("christmas").setExecutor(new Cmd());

        // get scoreboard
        Scoreboard scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        this.scoreboardObj = scoreboard.getObjective("Christmas");
        if(scoreboardObj == null)
        {
            this.scoreboardObj = scoreboard.registerNewObjective("Christmas", Criteria.DUMMY, Component.text("Christmas"));
        }

        // init event stats
        this.eventStats.state = EventStats.State.IDLE;
        this.eventStats.timer = 0;
    }

    @Override
    public void onDisable()
    {
        cfg.save();
    }
}
