package io.lazysheeep.mczjuchristmas;

import net.kyori.adventure.text.Component;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;
import java.util.List;

public class TestListener implements Listener
{
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteract(PlayerInteractEvent event)
    {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK
                && event.hasItem()
                && event.getItem().getType() == Material.STICK
                && event.getItem().getItemMeta().hasDisplayName()
                && event.getItem().getItemMeta().displayName().equals(Component.text("Spawner")))
        {
            List<Location> locations = (List<Location>)MCZJUChristmas.cfg.getList("locations");
            locations.add(event.getPlayer().getTargetBlock(8).getLocation().toCenterLocation().add(new Vector(0.0f, -0.95f, 0.0f)));
            MCZJUChristmas.cfg.set("locations", locations);
            event.getPlayer().sendMessage(Component.text("New Location Added!"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerArmorStandManipulate(PlayerArmorStandManipulateEvent event)
    {
        Entity entity = event.getRightClicked();
        if(entity.getType() == EntityType.ARMOR_STAND
                && entity.customName() != null
                && entity.customName().equals(Component.text("gift")))
        {
            Score score = MCZJUChristmas.scoreboardObj.getScore(event.getPlayer());
            score.setScore(score.getScore() + 1);

            entity.remove();

            event.getPlayer().sendMessage(Component.text("gift +1"));
        }
    }
}