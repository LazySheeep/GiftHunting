package io.lazysheeep.mczjuchristmas;

import org.bukkit.plugin.java.JavaPlugin;

public final class MCZJUChristmas extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(new TestListener(), this);

        this.getCommand("testCmd").setExecutor(new CMDTestcmd());
    }

    @Override
    public void onDisable()
    {
        // Plugin shutdown logic
    }
}
