package io.lazysheeep.gifthunting;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import javax.swing.*;
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

    public int getScore(Player player)
    {
        return GiftHunting.plugin.scoreboardObj.getScore(player).getScore();
    }

    public void setScore(Player player, int value)
    {
        GiftHunting.plugin.scoreboardObj.getScore(player).setScore(value);
    }

    public void addScore(Player player, int value)
    {
        Score score = GiftHunting.plugin.scoreboardObj.getScore(player);
        score.setScore(score.getScore() + value);
    }

    public void resetScore(Player player)
    {
        GiftHunting.plugin.scoreboardObj.getScore(player).resetScore();
    }

    public void resetScore()
    {
        for(OfflinePlayer player : Bukkit.getOfflinePlayers())
        {
            GiftHunting.plugin.scoreboardObj.getScore(player).resetScore();
        }
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

                        for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
                        {
                            // clear player item
                            player.getInventory().remove(ItemFactory.club);
                            // flush player UI
                            LazuliUI.flush(player);
                            // send message
                            LazuliUI.sendMessage(player, MessageFactory.getGameTerminatedMsg());
                        }
                    }
                    // game complete, go back to IDLE
                    case FINISHED ->
                    {
                        // clear gifts
                        Gift.clearGifts();

                        for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
                        {
                            // clear player item
                            player.getInventory().remove(ItemFactory.club);
                            // flush player UI
                            LazuliUI.flush(player);
                        }
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
                        // broadcast message
                        LazuliUI.broadcast(MessageFactory.getGameReadyingMsg());
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
                        // reset all score
                        GiftHunting.gameManager.resetScore();

                        for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
                        {
                            // give club
                            player.getInventory().remove(ItemFactory.club);
                            player.getInventory().addItem(ItemFactory.club);
                            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
                            // send message
                            LazuliUI.sendMessage(player, MessageFactory.getGameStartMsg());
                            // init actionbar suffix: score
                            LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffix(player));
                            // init actionbar infix
                            LazuliUI.sendMessage(player, MessageFactory.getGameStartActionbar());
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
                        for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
                        {
                            // send message
                            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedMsg(player));
                            // set actionbar
                            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedActionbarInfix());
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
                for(Player player : Util.getPlayersWithPermission(Permission.OP.name))
                {
                    if(player.getInventory().getItemInMainHand().isSimilar(ItemFactory.giftSpawnerSetter))
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
                    LazuliUI.broadcast(MessageFactory.getGameReadyingActionbar());
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
                for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
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
                LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getDeliverGiftMsg(Gift.GiftType.NORMAL));
            }
            case "SPECIAL" ->
            {
                Location spawnLocation = Util.randomPickOne(GiftHunting.config.getGiftSpawnerLocations());
                new Gift(spawnLocation, Gift.GiftType.SPECIAL);
                GiftHunting.plugin.logger.log(Level.INFO, MessageFactory.getSpawnGiftLog(1, Gift.GiftType.SPECIAL));
                LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getDeliverGiftMsg(Gift.GiftType.SPECIAL));
            }
            default -> {}
        }
    }
}
