package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.ItemFactory;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.Util;
import io.lazysheeep.gifthunting.gift.Gift;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.logging.Level;

public class GameManager
{

    private World _gameWorld;
    private Location _gameSpawn;
    private int _readyStateDuration;
    private int _progressStateMaxDuration;
    private int _finishedStateDuration;

    public World getGameWorld()
    {
        return _gameWorld;
    }

    public int getReadyStateDuration()
    {
        return _readyStateDuration;
    }

    public int getFinishedStateDuration()
    {
        return _finishedStateDuration;
    }

    public void loadConfig()
    {
        ConfigurationNode configNode = GiftHunting.GetPlugin().getConfigRootNode();
        _gameWorld = Bukkit.getWorld(configNode.node("gameWorld").getString("world"));
        if(_gameWorld == null)
        {
            GiftHunting.Log(Level.SEVERE, "Game world not found!");
        }
        ConfigurationNode gameSpawnNode = configNode.node("gameSpawn");
        _gameSpawn = new Location(_gameWorld,
                                  gameSpawnNode.node("x").getDouble(0.0),
                                  gameSpawnNode.node("y").getDouble(0.0),
                                  gameSpawnNode.node("z").getDouble(0.0),
                                  gameSpawnNode.node("yaw").getFloat(0.0f),
                                  gameSpawnNode.node("pitch").getFloat(0.0f));
        _readyStateDuration = configNode.node("readyStateDuration").getInt(0);
        _progressStateMaxDuration = configNode.node("progressStateMaxDuration").getInt(0);
        _finishedStateDuration = configNode.node("finishedStateDuration").getInt(0);
    }

    private GameState state;
    private GameState lastState;
    private int timer;

    public GameState getState()
    {
        return state;
    }

    public int getTimer()
    {
        return timer;
    }

    public GameManager()
    {
        this.state = GameState.IDLE;
        this.lastState = null;
        this.timer = 0;
    }

    public boolean switchState(GameState newState)
    {
        boolean success = true;
        switch(newState)
        {
            case IDLE ->
            {
                switch(state)
                {
                    // the game terminated by op
                    case READYING, PROGRESSING ->
                    {
                        // clear gifts
                        GiftHunting.GetPlugin().getGiftManager().removeAllGifts();

                        for(GHPlayer ghPlayer : GiftHunting.GetPlugin().getPlayerManager().getAllGHPlayers())
                        {
                            Player player = ghPlayer.getPlayer();
                            // clear item
                            Util.ClearInventory(player);
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
                        GiftHunting.GetPlugin().getGiftManager().removeAllGifts();

                        for(GHPlayer ghPlayer : GiftHunting.GetPlugin().getPlayerManager().getAllGHPlayers())
                        {
                            Player player = ghPlayer.getPlayer();
                            // clear item
                            Util.ClearInventory(player);
                            // flush player UI
                            LazuliUI.flush(player);
                            // send message
                            LazuliUI.sendMessage(player, MessageFactory.getGameBackToIdleMsg());
                            // go back to spawn
                            player.teleport(_gameSpawn);
                        }

                        // update souvenir
                        List<GHPlayer> ghPlayers = GiftHunting.GetPlugin().getPlayerManager().getSortedGHPlayers();
                        for(int i = 0; i < ghPlayers.size(); i ++)
                        {
                            GHPlayer ghPlayer = ghPlayers.get(i);
                            ItemFactory.UpdateSouvenir(ghPlayer.getPlayer(), i + 1, ghPlayers.size(), ghPlayer.getScore());
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
                        GiftHunting.GetPlugin().getGiftManager().removeAllGifts();
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
                        for(GHPlayer ghPlayer : GiftHunting.GetPlugin().getPlayerManager().getAllGHPlayers())
                        {
                            Player player = ghPlayer.getPlayer();
                            // reset score
                            ghPlayer.setScore(0);
                            // clear item
                            Util.ClearInventory(player);
                            // send message
                            LazuliUI.sendMessage(player, MessageFactory.getGameStartMsg());
                            // init actionbar suffix: score
                            LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffix(ghPlayer));
                            // init actionbar infix
                            LazuliUI.sendMessage(player, MessageFactory.getGameStartActionbar());
                            // teleport to game spawn
                            ghPlayer.getPlayer().teleport(_gameSpawn);
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
                        for(GHPlayer ghPlayer : GiftHunting.GetPlugin().getPlayerManager().getAllGHPlayers())
                        {
                            Player player = ghPlayer.getPlayer();
                            // send message
                            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedMsg(ghPlayer));
                            // set actionbar
                            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedActionbarInfix());
                            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedActionbarSuffix(ghPlayer));
                        }
                    }
                    default -> success = false;
                }
            }
        }

        if(success)
        {
            // set state and timer
            lastState = state;
            state = newState;
            timer = 0;
            // log
            GiftHunting.Log(Level.INFO, "Game state changed: " + lastState.toString() + " -> " + state.toString());
        }

        return success;
    }

    public void tick()
    {
        // always
        for(GHPlayer ghPlayer : GiftHunting.GetPlugin().getPlayerManager().getAllGHPlayers())
        {
            ghPlayer.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 1, false, false, false));
        }

        // game state machine
        switch(state)
        {
            case IDLE ->
            {
                for(Player player : Util.GetPlayersWithPermission("op"))
                {
                    if(player.getInventory().getItemInMainHand().isSimilar(ItemFactory.normalGiftSpawnerSetter))
                    {
                        // show gift spawner location
                        for(Location spawnerLocation : GiftHunting.GetPlugin().getGiftManager().getNormalSpawners())
                        {
                            player.getWorld().spawnParticle(Particle.COMPOSTER, spawnerLocation.clone().add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                        }
                        // show gift spawner count
                        LazuliUI.sendMessage(player, MessageFactory.getNormalSpawnerCountActionbar());
                    }
                    else if(player.getInventory().getItemInMainHand().isSimilar(ItemFactory.specialGiftSpawnerSetter))
                    {
                        // show gift spawner location
                        for(Location spawnerLocation : GiftHunting.GetPlugin().getGiftManager().getSpecialSpawners())
                        {
                            player.getWorld().spawnParticle(Particle.WAX_ON, spawnerLocation.clone().add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                        }
                        // show gift spawner count
                        LazuliUI.sendMessage(player, MessageFactory.getSpecialSpawnerCountActionbar());
                    }
                }
            }

            case READYING ->
            {
                // draw actionbar: timer
                if(timer%20 == 0)
                    LazuliUI.broadcast(MessageFactory.getGameReadyingActionbar());
                // 30 seconds before game start
                if(timer == _readyStateDuration - 800)
                {
                    // send game intro message
                    LazuliUI.broadcast(MessageFactory.getGameIntroMsg());

                }
                // go PROGRESSING
                if(getTimer() >= _readyStateDuration)
                {
                    switchState(GameState.PROGRESSING);
                }
                // timer ++
                timer ++;
            }

            case PROGRESSING ->
            {
                // draw actionbar prefix: timer
                for(GHPlayer ghPlayer : GiftHunting.GetPlugin().getPlayerManager().getAllGHPlayers())
                {
                    LazuliUI.sendMessage(ghPlayer.getPlayer(), MessageFactory.getProgressingActionbarPrefix());
                }
                // deliver gifts
                // TODO

                // bonus events
                // TODO

                // particle
                if(timer % 20 == 0)
                {
                    for(Gift gift : GiftHunting.GetPlugin().getGiftManager().getGifts())
                    {
                        GiftHunting.GetPlugin().getGameManager().getGameWorld().spawnParticle(Particle.HAPPY_VILLAGER, gift.getLocation(), 1, 0.5f, 0.5f, 0.5f);
                    }
                }
                // go FINISHED
                if(timer >= _progressStateMaxDuration)
                {
                    switchState(GameState.FINISHED);
                }
                // timer ++
                timer ++;
            }

            case FINISHED ->
            {
                // go IDLE
                if(timer >= _finishedStateDuration)
                {
                    switchState(GameState.IDLE);
                }
                // timer ++
                timer ++;
            }
        }
    }

    /*
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
                    GiftHunting.instance.world.spawnParticle(Particle.VILLAGER_HAPPY, newGift.getLocation(), 8, 0.4f, 0.4f, 0.4f);
                }
                GiftHunting.instance.logger.log(Level.INFO, MessageFactory.getSpawnGiftLog(spawnLocations.size(), Gift.GiftType.NORMAL));
                LazuliUI.broadcast(Permission.PLAYER.name, MessageFactory.getDeliverGiftMsg(Gift.GiftType.NORMAL));
            }
            case "SPECIAL" ->
            {
                Location spawnLocation = Util.randomPickOne(GiftHunting.config.getGiftSpawnerLocations());
                Gift newGift = new Gift(spawnLocation, Gift.GiftType.SPECIAL);
                GiftHunting.instance.world.spawnParticle(Particle.VILLAGER_HAPPY, newGift.getLocation(), 32, 0.5f, 0.5f, 0.5f);
                GiftHunting.instance.logger.log(Level.INFO, MessageFactory.getSpawnGiftLog(1, Gift.GiftType.SPECIAL));
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
        Firework firework = (Firework) GiftHunting.instance.world.spawnEntity(location, EntityType.FIREWORK);
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
    */
}