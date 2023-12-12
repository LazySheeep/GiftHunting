package io.lazysheeep.gifthunting;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.uimanager.Message;
import io.lazysheeep.uimanager.UIManager;
import org.bukkit.Location;
import org.bukkit.Particle;
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
                        // flush player UI
                        for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                            UIManager.flush(player);
                        // broadcast message
                        UIManager.broadcast(Permission.PLAYER.name, MessageFactory.getGameTerminatedMsg());
                    }
                    // game complete, go back to IDLE
                    case FINISHED ->
                    {
                        // TODO
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
                        for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                        {
                            // flush player UI
                            UIManager.flush(player);
                            // send message
                            UIManager.sendMessage(player, MessageFactory.getGameReadyingMsg());
                        }
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
                            // init actionbar suffix: score
                            UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, MessageFactory.getProgressingActionbarSuffix(0), Message.LoadMode.REPLACE, -1));
                            // init actionbar infix
                            UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getGameStartActionbar(), Message.LoadMode.REPLACE, 20));
                            // send message
                            UIManager.broadcast(Permission.PLAYER.name, MessageFactory.getGameStartMsg());
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
                        UIManager.broadcast(Permission.PLAYER.name, MessageFactory.getGameFinishedMsg());
                        // TODO
                        // set actionbar
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
                        UIManager.broadcast(Permission.PLAYER.name, MessageFactory.getGamePausedMsg());
                        UIManager.broadcast(Permission.PLAYER.name, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getGamePausedActionbar(), Message.LoadMode.REPLACE, -1));
                    }
                    default -> success = false;
                }
            }

            case UNPAUSE ->
            {
                if(state == State.PAUSED)
                {
                    newState = lastState;
                    UIManager.broadcast(Permission.PLAYER.name, MessageFactory.getGameUnpauseMsg());
                    UIManager.broadcast(Permission.PLAYER.name, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getGameUnpauseMsg(), Message.LoadMode.REPLACE, 20));
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
                // timer ++
                timer ++;
                // draw actionbar: timer
                if(timer%20 == 0)
                {
                    for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                    {
                        UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, MessageFactory.getGameReadyingActionbar(), Message.LoadMode.REPLACE, 20));
                    }
                }
                // go PROGRESSING
                if(getTimer() >= GiftHunting.config.readyStateDuration)
                {
                    switchState(State.PROGRESSING);
                }
            }

            case PROGRESSING ->
            {
                // timer ++
                timer ++;
                // draw actionbar prefix: timer
                for(Player player : GiftHunting.plugin.getServer().getOnlinePlayers())
                {
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_PREFIX, MessageFactory.getProgressingActionbarPrefix(), Message.LoadMode.REPLACE, 1));
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
            }

            case FINISHED ->
            {
                // timer ++
                timer ++;
                // go IDLE
                if(timer >= GiftHunting.config.finishedStateDuration)
                {
                    switchState(State.IDLE);
                }
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
