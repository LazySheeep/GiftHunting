package io.lazysheeep.gifthunting;

import io.lazysheeep.gifthunting.ui.UIManager;
import net.kyori.adventure.text.Component;
import org.bukkit.World;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Criteria;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public final class GiftHunting extends JavaPlugin
{
    public static GiftHunting plugin;
    public Config config;
    public UIManager uiManager = new UIManager(this);
    public World world;
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
        config = new Config(this);
        this.config.load();

        // set static reference
        plugin = this;
        world = this.getServer().getWorld(config.worldName);
        if(world == null)
            this.getServer().sendMessage(Component.text("[ERROR] World \"" + config.worldName + "\" not found!"));

        // register event listener
        this.getServer().getPluginManager().registerEvents(new EventListener(), this);
        this.getServer().getPluginManager().registerEvents(uiManager, this);

        // register commands
        PluginCommand command = this.getCommand("gifthunting");
        if(command != null)
            command.setExecutor(new CCommandExecutor());
        else
            this.getServer().broadcast(Component.text("[ERROR] Something Wrong!"));

        // get scoreboard
        Scoreboard scoreboard = this.getServer().getScoreboardManager().getMainScoreboard();
        this.scoreboardObj = scoreboard.getObjective("GiftHunting");
        if(scoreboardObj == null)
        {
            this.scoreboardObj = scoreboard.registerNewObjective("GiftHunting", Criteria.DUMMY, Component.text("GiftHunting"));
            this.getServer().broadcast(Component.text("Scoreboard \"GiftHunting\" not found, created one"));
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
