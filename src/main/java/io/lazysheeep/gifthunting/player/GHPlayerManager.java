package io.lazysheeep.gifthunting.player;

import io.lazysheeep.gifthunting.GiftHunting;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class GHPlayerManager
{
    private final List<GHPlayer> _ghPlayers = new LinkedList<>();

    public @NotNull List<GHPlayer> getAllGHPlayers()
    {
        return _ghPlayers;
    }

    public List<GHPlayer> getSortedGHPlayers()
    {
        _ghPlayers.sort(new Comparator<GHPlayer>()
        {
            @Override
            public int compare(GHPlayer p1, GHPlayer p2)
            {
                return p2.getScore() - p1.getScore();
            }
        });
        return _ghPlayers;
    }

    private @NotNull GHPlayer createGHPlayer(@NotNull Player player)
    {
        GHPlayer ghPlayer = new GHPlayer(player);
        player.setMetadata("GHPlayer", new FixedMetadataValue(GiftHunting.GetPlugin(), ghPlayer));
        _ghPlayers.add(ghPlayer);
        return ghPlayer;
    }

    private void destroyGHPlayer(@NotNull GHPlayer ghPlayer)
    {
        ghPlayer.getPlayer().removeMetadata("GHPlayer", GiftHunting.GetPlugin());
        ghPlayer.destroy();
    }

    public @Nullable GHPlayer getGHPlayer(@NotNull Player player)
    {
        for(var meta : player.getMetadata("GHPlayer"))
        {
            if(meta.getOwningPlugin() == GiftHunting.GetPlugin() && meta.value() instanceof GHPlayer ghPlayer)
            {
                return ghPlayer;
            }
        }
        return null;
    }

    public boolean isGHPlayer(@NotNull Player player)
    {
        return getGHPlayer(player) != null;
    }

    private boolean shouldBeGHPlayer(@NotNull Player player)
    {
        return player.isOnline() && player.getWorld() == GiftHunting.GetPlugin().getGameManager().getGameWorld();
    }

    public void tick()
    {
        // Remove GHPlayers that should not be GHPlayers
        for(GHPlayer ghPlayer : _ghPlayers)
        {
            if(!shouldBeGHPlayer(ghPlayer.getPlayer()))
            {
                destroyGHPlayer(ghPlayer);
            }
        }
        // Create GHPlayers that should be GHPlayers
        for(Player player : GiftHunting.GetPlugin().getServer().getOnlinePlayers())
        {
            if(shouldBeGHPlayer(player) && !isGHPlayer(player))
            {
                createGHPlayer(player);
            }
        }
        // Remove invalid GHPlayers
        _ghPlayers.removeIf(ghPlayer -> !ghPlayer.isValid());
        // Tick GHPlayers
        for(GHPlayer ghPlayer : _ghPlayers)
        {
            ghPlayer.tick();
        }
    }
}