package io.lazysheeep.gifthunting.player;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.game.GHStates;
import io.lazysheeep.gifthunting.game.GameInstance;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.lazuliui.LazuliUI;
import io.papermc.paper.event.player.PlayerItemFrameChangeEvent;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class GHPlayerManager implements Listener
{
    private final Map<UUID, GHPlayer> _ghPlayers = new HashMap<>();
    private final Map<UUID, GHPlayer> _offlineGHPlayers = new HashMap<>();

    public int getOnlineGHPlayerCount()
    {
        return _ghPlayers.size();
    }

    public int getOfflineGHPlayerCount()
    {
        return _offlineGHPlayers.size();
    }

    public @NotNull List<GHPlayer> getOnlineGHPlayers()
    {
        return new ArrayList<>(_ghPlayers.values());
    }

    public @NotNull List<GHPlayer> getOfflineGHPlayers()
    {
        return new ArrayList<>(_offlineGHPlayers.values());
    }

    public @NotNull List<GHPlayer> getAllGHPlayers()
    {
        List<GHPlayer> allGHPlayers = new ArrayList<>(_ghPlayers.values());
        allGHPlayers.addAll(_offlineGHPlayers.values());
        return allGHPlayers;
    }

    public List<GHPlayer> getAllGHPlayersSorted()
    {
        List<GHPlayer> allGHPlayers = getAllGHPlayers();
        allGHPlayers.sort(new Comparator<GHPlayer>()
        {
            @Override
            public int compare(GHPlayer p1, GHPlayer p2)
            {
                return p2.getScore() - p1.getScore();
            }
        });
        return allGHPlayers;
    }

    private final GameInstance _gameInstance;

    public GHPlayerManager(GameInstance gameInstance)
    {
        _gameInstance = gameInstance;
    }

    private void createGHPlayer(@NotNull Player player)
    {
        GHPlayer ghPlayer = new GHPlayer(player, _gameInstance);
        MCUtil.ClearInventory(player);
        _ghPlayers.put(player.getUniqueId(), ghPlayer);
    }

    private void reconnectGHPlayer(@NotNull GHPlayer offlineGHPlayer, @NotNull Player player)
    {
        offlineGHPlayer.reconnect(player);
        _offlineGHPlayers.remove(player.getUniqueId());
        _ghPlayers.put(player.getUniqueId(), offlineGHPlayer);
    }

    private void destroyGHPlayer(@NotNull GHPlayer ghPlayer)
    {
        LazuliUI.flush(ghPlayer.getPlayer());
        ghPlayer.destroy();
    }

    public @Nullable GHPlayer getGHPlayer(@NotNull Player player)
    {
        return _ghPlayers.get(player.getUniqueId());
    }

    public boolean isGHPlayer(@NotNull Player player)
    {
        return getGHPlayer(player) != null;
    }

    private boolean shouldBeGHPlayer(@NotNull Player player)
    {
        return player.isConnected() && player.getWorld() == _gameInstance.getGameWorld() && player.getGameMode() == GameMode.ADVENTURE;
    }

    public void tick()
    {
        // Remove GHPlayers that should not be GHPlayers
        for(GHPlayer ghPlayer : getOnlineGHPlayers())
        {
            if(!shouldBeGHPlayer(ghPlayer.getPlayer()))
            {
                _ghPlayers.remove(ghPlayer.getUUID());
                _offlineGHPlayers.put(ghPlayer.getUUID(), ghPlayer);
            }
        }
        // Create GHPlayers that should be GHPlayers
        for(Player player : GiftHunting.GetPlugin().getServer().getOnlinePlayers())
        {
            if(shouldBeGHPlayer(player) && !isGHPlayer(player))
            {
                boolean isReconnecting = false;
                for(GHPlayer offlineGHPlayer : getOfflineGHPlayers())
                {
                    if(offlineGHPlayer.getUUID().equals(player.getUniqueId()))
                    {
                        reconnectGHPlayer(offlineGHPlayer, player);
                        isReconnecting = true;
                        break;
                    }
                }
                if(!isReconnecting)
                {
                    createGHPlayer(player);
                }
            }
        }
        // Tick GHPlayers
        if(_gameInstance.getCurrentStateEnum() != GHStates.IDLE)
        {
            for(GHPlayer ghPlayer : getOnlineGHPlayers())
            {
                ghPlayer.tick();
            }
        }
    }

    // player fall damage
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event)
    {
        if(event.getEntity() instanceof Player player)
        {
            if(event.getCause() == EntityDamageEvent.DamageCause.FALL && isGHPlayer(player))
            {
                event.setCancelled(true);
            }
        }
    }

    // protect itemFrame
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event)
    {
        Player player = event.getPlayer();
        if(!player.hasPermission("op") && isGHPlayer(player))
        {
            event.setCancelled(true);
        }
    }
}