package io.lazysheeep.mczjuchristmas;

import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class TestListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerMove(PlayerMoveEvent event)
    {
        event.getPlayer().sendMessage(Component.text("Hello " + event.getPlayer().getName()));
    }
}