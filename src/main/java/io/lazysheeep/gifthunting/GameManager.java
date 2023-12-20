package io.lazysheeep.gifthunting;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.lazysheeep.lazuliui.LazuliUI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Score;
import org.bukkit.util.Vector;

import java.util.Comparator;
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
                            // clear item
                            clearInventory(player);
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
                            // clear item
                            clearInventory(player);
                            // flush player UI
                            LazuliUI.flush(player);
                            // send message
                            LazuliUI.sendMessage(player, MessageFactory.getGameBackToIdleMsg());
                            // go back to spawn
                            player.teleport(GiftHunting.config.gameSpawn);
                        }

                        // update souvenir
                        List<Player> players = this.getRankedPlayerList();
                        for(int i = 0; i < players.size(); i ++)
                        {
                            ItemFactory.updateSouvenir(players.get(i), i + 1, players.size(), this.getScore(players.get(i)));
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
                        // check whether gameSpawn is set
                        if(GiftHunting.config.gameSpawn != null)
                        {
                            // broadcast message
                            LazuliUI.broadcast(MessageFactory.getGameReadyingMsg());
                            // clear all gifts
                            Gift.clearGifts();
                            Gift.clearUnTracked();
                        }
                        else success = false;
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
                            // clear item
                            clearInventory(player);
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
            // log
            GiftHunting.plugin.logger.log(Level.INFO, "Game state changed: " + lastState.toString() + " -> " + state.toString());
        }

        return success;
    }

    // on server tick
    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerTickStartEvent(ServerTickStartEvent event)
    {
        // always
        for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
        {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 1, false, false, false));
        }

        // game state machine
        switch(state)
        {
            case IDLE, PAUSED ->
            {
                for(Player player : Util.getPlayersWithPermission(Permission.OP.name))
                {
                    if(player.getInventory().getItemInMainHand().isSimilar(ItemFactory.giftSpawnerSetter))
                    {
                        // show gift spawner location
                        for(Location spawnerLocation : GiftHunting.config.getGiftSpawnerLocations())
                        {
                            player.spawnParticle(Particle.COMPOSTER, spawnerLocation.add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                        }
                        // show gift spawner count
                        LazuliUI.sendMessage(player, MessageFactory.getSpawnerCountActionbar());
                    }
                }
            }

            case READYING ->
            {
                // draw actionbar: timer
                if(timer%20 == 0)
                    LazuliUI.broadcast(MessageFactory.getGameReadyingActionbar());
                // 30 seconds before game start
                if(timer == GiftHunting.config.readyStateDuration - 800)
                {
                    // send game intro message
                    LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getGameIntroMsg());
                    // teleport players to game spawn
                    for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
                    {
                        player.teleport(GiftHunting.config.gameSpawn);
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
                for(Player player : Util.getPlayersWithPermission(Permission.PLAYER.name))
                {
                    LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarPrefix());
                }
                // deliver gifts
                for(Map<String, Object> giftBatch : GiftHunting.config.giftBatches)
                {
                    if((Integer) giftBatch.get("time") == timer)
                        deliverGiftBatch(giftBatch);
                }
                // bonus events
                for(Integer bonusEventTime : GiftHunting.config.bonusEvents)
                {
                    if(bonusEventTime == timer)
                        launchBonusEvent();
                }
                // gift location prompts
                if(timer % GiftHunting.config.promptInterval_special == 0)
                {
                    sendGiftLocationPrompt();
                }
                // particle
                if(timer % 10 == 0)
                {
                    for(Gift gift : Gift.getGifts())
                    {
                        if(gift.type == Gift.GiftType.SPECIAL)
                            GiftHunting.plugin.world.spawnParticle(Particle.VILLAGER_HAPPY, gift.getLocation(), 4, 0.5f, 0.5f, 0.5f);
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

    public List<Player> getRankedPlayerList()
    {
        List<Player> players = Util.getPlayersWithPermission(Permission.PLAYER.name);
        players.sort(new Comparator<Player>()
        {
            @Override
            public int compare(Player p1, Player p2)
            {
                return GiftHunting.gameManager.getScore(p2) - GiftHunting.gameManager.getScore(p1);
            }
        });
        return players;
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
                    Gift newGift = new Gift(loc, Gift.GiftType.NORMAL);
                    GiftHunting.plugin.world.spawnParticle(Particle.VILLAGER_HAPPY, newGift.getLocation(), 8, 0.4f, 0.4f, 0.4f);
                }
                GiftHunting.plugin.logger.log(Level.INFO, MessageFactory.getSpawnGiftLog(spawnLocations.size(), Gift.GiftType.NORMAL));
                LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getDeliverGiftMsg(Gift.GiftType.NORMAL));
            }
            case "SPECIAL" ->
            {
                Location spawnLocation = Util.randomPickOne(GiftHunting.config.getGiftSpawnerLocations());
                Gift newGift = new Gift(spawnLocation, Gift.GiftType.SPECIAL);
                GiftHunting.plugin.world.spawnParticle(Particle.VILLAGER_HAPPY, newGift.getLocation(), 32, 0.5f, 0.5f, 0.5f);
                GiftHunting.plugin.logger.log(Level.INFO, MessageFactory.getSpawnGiftLog(1, Gift.GiftType.SPECIAL));
                LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getDeliverGiftMsg(Gift.GiftType.SPECIAL));
            }
            default -> {}
        }
    }

    private void launchBonusEvent()
    {
        LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getBonusEventMsg());
        List<Player> players = getRankedPlayerList();
        for(int i = (int)(players.size()*(1.0f-GiftHunting.config.bonusPercentage)); i < players.size(); i ++)
        {
            Player player = players.get(i);
            player.getInventory().addItem(ItemFactory.stealer);
            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }

    private void sendGiftLocationPrompt()
    {
        for(Gift gift : Gift.getGifts())
        {
            if(gift.type == Gift.GiftType.SPECIAL)
            {
                spawnFirework(gift.getLocation().add(0.0f, 0.5f, 0.0f), 2);
                spawnFirework(gift.getLocation().add(0.0f, 0.5f, 0.0f), 3);
            }
        }
    }

    private void spawnFirework(Location location, int power)
    {
        Firework firework = (Firework) GiftHunting.plugin.world.spawnEntity(location, EntityType.FIREWORK);
        FireworkMeta meta = firework.getFireworkMeta();
        meta.setPower(power);
        meta.addEffect(
                FireworkEffect.builder()
                        .with(FireworkEffect.Type.STAR)
                        .withColor(Color.RED)
                        .withColor(Color.LIME)
                        .withColor(Color.WHITE)
                        .trail(true)
                        .flicker(true)
                        .build()
        );
        firework.setFireworkMeta(meta);
    }

    private void clearInventory(Player player)
    {
        PlayerInventory inventory = player.getInventory();
        for(ItemStack item : inventory)
        {
            if(item!= null && item.getType() != ItemFactory.souvenir.getType())
            {
                inventory.remove(item);
            }
        }
    }
}