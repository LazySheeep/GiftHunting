package io.lazysheeep.mczju.christmas;

import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class Christmas extends JavaPlugin
{
    public static Christmas plugin;
    public World world;
    public CConfig config;
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
        this.config = new CConfig(this);
        this.config.load();

        // set static reference
        plugin = this;
        world = this.getServer().getWorld(config.worldName);
        if(world == null)
            this.getServer().sendMessage(Component.text("[ERROR] World \"" + config.worldName + "\" not found!"));

        // register events
        this.getServer().getPluginManager().registerEvents(new CEventListener(), this);

        // register commands
        PluginCommand command = this.getCommand("christmas");
        if(command != null)
            command.setExecutor(new CCommand());
        else
            this.getServer().broadcast(Component.text("[ERROR] Something Wrong!"));

        // get scoreboard
        Scoreboard scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        this.scoreboardObj = scoreboard.getObjective("Christmas");
        if(scoreboardObj == null)
        {
            this.scoreboardObj = scoreboard.registerNewObjective("Christmas", Criteria.DUMMY, Component.text("Christmas"));
            this.getServer().broadcast(Component.text("Scoreboard \"Christmas\" not found, created one"));
        }

        // init event stats
        this.eventStats.state = EventStats.State.IDLE;
        this.eventStats.timer = 0;
    }

    @Override
    public void onDisable()
    {
        config.save();
    }
}
