package io.lazysheeep.mczju.christmas.ui;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class UIManager implements Listener
{
    private final Plugin plugin;

    public UIManager(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        UI ui = new UI(player);
        player.setMetadata("UI", new FixedMetadataValue(plugin, ui));
        plugin.getServer().getPluginManager().registerEvents(ui, plugin);
    }

    public UI getPlayerUI(Player player)
    {
        for(MetadataValue metaData : player.getMetadata("UI"))
        {
            if(metaData.getOwningPlugin() == plugin && metaData.value() instanceof UI ui)
                return ui;
        }
        return null;
    }

    public void sendMessage(Player player, Message message)
    {
        getPlayerUI(player).sendMessage(message);
    }

}
