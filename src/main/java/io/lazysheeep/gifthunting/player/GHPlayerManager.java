package io.lazysheeep.gifthunting.player;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.factory.MessageFactory;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
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
        for(GHPlayer ghPlayer : getOnlineGHPlayers())
        {
            if(!shouldBeGHPlayer(ghPlayer.getPlayer()))
            {
                _ghPlayers.remove(ghPlayer.getUUID());
                _offlineGHPlayers.put(ghPlayer.getUUID(), ghPlayer);
                ghPlayer.onDisconnect(_gameInstance);
                LazuliUI.sendMessage(ghPlayer.getPlayer(), MessageFactory.getOnNoLongerGHPlayerMsg());
            }
        }
        for(Player player : GiftHunting.GetPlugin().getServer().getOnlinePlayers())
        {
            if(shouldBeGHPlayer(player) && !isGHPlayer(player))
            {
                boolean isReconnecting = false;
                for(GHPlayer offlineGHPlayer : getOfflineGHPlayers())
                {
                    // reconnect
                    if(offlineGHPlayer.getUUID().equals(player.getUniqueId()))
                    {
                        _offlineGHPlayers.remove(player.getUniqueId());
                        _ghPlayers.put(player.getUniqueId(), offlineGHPlayer);
                        offlineGHPlayer.onConnect(_gameInstance, player);
                        LazuliUI.sendMessage(player, MessageFactory.getOnReconnectGHPlayerMsg());
                        isReconnecting = true;
                        break;
                    }
                }
                // create
                if(!isReconnecting)
                {
                    GHPlayer ghPlayer = new GHPlayer();
                    MCUtil.ClearInventory(player);
                    ghPlayer.onConnect(_gameInstance, player);
                    _ghPlayers.put(player.getUniqueId(), ghPlayer);
                    LazuliUI.sendMessage(player, MessageFactory.getOnBecomeGHPlayerMsg());
                }
            }
        }
        if(_gameInstance.getCurrentStateEnum() != GHStates.IDLE)
        {
            for(GHPlayer ghPlayer : getOnlineGHPlayers())
            {
                ghPlayer.tick();
            }
        }
    }

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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerItemFrameChange(PlayerItemFrameChangeEvent event)
    {
        Player player = event.getPlayer();
        if(!player.hasPermission("op") && isGHPlayer(player))
        {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event)
    {
        if(!(event.getWhoClicked() instanceof Player player)) return;
        GHPlayer gh = getGHPlayer(player);
        if(gh == null) return;
        int slot = event.getSlot();
        for(CustomItem ci : CustomItem.values())
        {
            if(ci.lockedSlot >= 0 && ci.lockedSlot == slot)
            {
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerDropItem(PlayerDropItemEvent event)
    {
        Player player = event.getPlayer();
        GHPlayer gh = getGHPlayer(player);
        if(gh == null) return;
        ItemStack stack = event.getItemDrop().getItemStack();
        if(CustomItem.checkItem(stack) != null)
        {
            event.setCancelled(true);
        }
    }
}