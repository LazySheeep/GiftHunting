package io.lazysheeep.gifthunting;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class GameManager implements Listener
{
    public enum State
    {
        IDLE, READYING, PROGRESSING, FINISHED, PAUSED, UNPAUSE,
    }
    private State state;
    private State lastState;
    private int timer;

    public GameManager()
    {
        this.state = State.IDLE;
        this.lastState = null;
        this.timer = 0;
    }

    public State getState()
    {
        return state;
    }

    public int getTimer()
    {
        return timer;
    }

    public boolean switchState(State newState)
    {
        boolean success = true;
        switch(newState)
        {
            case IDLE ->
            {
                switch(state)
                {
                    // the game terminated by op
                    case READYING, PROGRESSING, PAUSED ->
                    {
                        // clear gifts
                        Gift.clearGifts();
                        // clear player item
                        for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                        {
                            player.getInventory().remove(ItemFactory.club);
                        }
                        // flush player UI
                        LazuliUI.flush(Permission.PLAYER.name);
                        // broadcast message
                        LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameTerminatedMsg());
                    }
                    // game complete, go back to IDLE
                    case FINISHED ->
                    {
                        // clear gifts
                        Gift.clearGifts();
                        // clear player item
                        for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                        {
                            player.getInventory().remove(ItemFactory.club);
                        }
                        // flush player UI
                        LazuliUI.flush(Permission.PLAYER.name);
                    }
                    default -> success = false;
                }
            }

            case READYING ->
            {
                switch(state)
                {
                    // op start the game
                    case IDLE ->
                    {
                        // flush player UI
                        LazuliUI.flush(Permission.PLAYER.name);
                        // send message
                        LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameReadyingMsg());
                        // clear all gifts
                        Gift.clearGifts();
                        Gift.clearUnTracked();
                    }
                    default -> success = false;
                }
            }

            case PROGRESSING ->
            {
                switch(state)
                {
                    // the game proceed from READYING to PROGRESSING
                    case READYING ->
                    {
                        for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                        {
                            // reset score
                            GiftHunting.plugin.scoreboardObj.getScore(player).resetScore();
                            // give club
                            player.getInventory().remove(ItemFactory.club);
                            player.getInventory().addItem(ItemFactory.club);
                            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
                            // init actionbar suffix: score
                            LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffix(player));
                            // init actionbar infix
                            LazuliUI.sendMessage(player, MessageFactory.getGameStartActionbar());
                            // send message
                            LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameStartMsg());
                        }
                    }
                    default -> success = false;
                }

            }

            case FINISHED ->
            {
                switch(state)
                {
                    // the game proceed from PROGRESSING to FINISHED
                    case PROGRESSING ->
                    {
                        // send message
                        LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameFinishedMsg());
                        // set actionbar
                        LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameFinishedActionbarInfix());
                        for(Player player : GiftHunting.plugin.world.getPlayers())
                        {
                            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedActionbarSuffix(player));
                        }
                    }
                    default -> success = false;
                }
            }

            case PAUSED ->
            {
                switch(state)
                {
                    // op paused the game
                    case READYING, PROGRESSING ->
                    {
                        LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGamePausedMsg());
                        LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGamePausedActionbar());
                    }
                    default -> success = false;
                }
            }

            case UNPAUSE ->
            {
                if(state == State.PAUSED)
                {
                    newState = lastState;
                    LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameUnpauseMsg());
                    LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameUnPauseActionbar());
                }
                else success = false;
            }
        }

        if(success)
        {
            // set state and timer
            lastState = state;
            state = newState;
            if(state != State.PAUSED && lastState != State.PAUSED) timer = 0;
        }

        return success;
    }

    // on server tick
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerTickStartEvent(ServerTickStartEvent event)
    {
        // game state machine
        switch(state)
        {
            case IDLE, PAUSED ->
            {
                // show gift spawner location
                for(Player player : GiftHunting.plugin.world.getPlayers())
                {
                    if(player.hasPermission(Permission.OP.name) && player.getInventory().getItemInMainHand().isSimilar(ItemFactory.giftSpawnerSetter))
                        for(Location spawnerLocation : GiftHunting.config.getGiftSpawnerLocations())
                        {
                            player.spawnParticle(Particle.COMPOSTER, spawnerLocation.add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                        }
                }
            }

            case READYING ->
            {
                // draw actionbar: timer
                if(timer%20 == 0)
                {
                    for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                    {
                        LazuliUI.sendMessage(player, MessageFactory.getGameReadyingActionbar());
                    }
                }
                // go PROGRESSING
                if(getTimer() >= GiftHunting.config.readyStateDuration)
                {
                    switchState(State.PROGRESSING);
                }
                // timer ++
                timer ++;
            }

            case PROGRESSING ->
            {
                // draw actionbar prefix: timer
                for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                {
                    LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarPrefix());
                }
                // deliver gifts
                for(Map<String, Object> giftBatch : GiftHunting.config.giftBatches)
                {
                    if((Integer) giftBatch.get("time") == timer)
                    {
                        deliverGiftBatch(giftBatch);
                    }
                }
                // go FINISHED
                if(timer >= GiftHunting.config.progressStateDuration)
                {
                    switchState(State.FINISHED);
                }
                // timer ++
                timer ++;
            }

            case FINISHED ->
            {
                // go IDLE
                if(timer >= GiftHunting.config.finishedStateDuration)
                {
                    switchState(State.IDLE);
                }
                // timer ++
                timer ++;
            }
        }
    }

    private void deliverGiftBatch(Map<String, Object> giftBatch)
    {
        String type = (String)giftBatch.get("type");
        switch (type)
        {
            case "NORMAL" ->
            {
                int amount = (Integer)giftBatch.get("amount");
                List<Location> spawnLocations = Util.randomPick(GiftHunting.config.getGiftSpawnerLocations(), amount);
                for (Location loc : spawnLocations)
                {
                    new Gift(loc, Gift.GiftType.NORMAL);
                }
                GiftHunting.plugin.logger.log(Level.INFO, MessageFactory.getSpawnGiftLog(amount, Gift.GiftType.NORMAL));
            }
            case "SPECIAL" ->
            {
                Location spawnLocation = Util.randomPickOne(GiftHunting.config.getGiftSpawnerLocations());
                new Gift(spawnLocation, Gift.GiftType.SPECIAL);
                GiftHunting.plugin.logger.log(Level.INFO, MessageFactory.getSpawnGiftLog(1, Gift.GiftType.SPECIAL));
            }
            default -> {}
        }
    }
}
