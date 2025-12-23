package io.lazysheeep.gifthunting.game;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.buffs.DawnBuff;
import io.lazysheeep.gifthunting.factory.CustomItem;
import io.lazysheeep.gifthunting.factory.MessageFactory;
import io.lazysheeep.gifthunting.player.GHPlayer;
import io.lazysheeep.gifthunting.skills.Skill;
import io.lazysheeep.gifthunting.utils.MCUtil;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.util.Vector;

import java.util.List;

public enum GHStates
{
    IDLE, READYING, PROGRESSING, FINISHED,
}

class IdleState extends State<GameInstance, GHStates>
{
    @Override
    public void onEnter(GameInstance gameInstance)
    {
        // set scoreboard display
        GiftHunting.GetPlugin().getScoreObjective().setDisplaySlot(null);
    }

    @Override
    public void onUpdate(GameInstance gameInstance)
    {
        for(Player player : MCUtil.GetPlayersWithPermission("op"))
        {
            if(CustomItem.checkItem(player.getInventory().getItemInMainHand()) == CustomItem.NORMAL_GIFT_SPAWNER_SETTER)
            {
                for(Location spawnerLocation : gameInstance.getGiftManager().getNormalSpawners())
                {
                    player.spawnParticle(Particle.COMPOSTER, spawnerLocation.clone().add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                }
                LazuliUI.sendMessage(player, MessageFactory.getNormalSpawnerCountActionbar(gameInstance.getGiftManager().getNormalSpawnerCount()));
            }
            else if(CustomItem.checkItem(player.getInventory().getItemInMainHand()) == CustomItem.SPECIAL_GIFT_SPAWNER_SETTER)
            {
                for(Location spawnerLocation : gameInstance.getGiftManager().getSpecialSpawners())
                {
                    player.spawnParticle(Particle.WAX_ON, spawnerLocation.clone().add(new Vector(0.0f, 1.95f, 0.0f)), 1, 0.0f, 0.0f, 0.0f);
                }
                LazuliUI.sendMessage(player, MessageFactory.getSpecialSpawnerCountActionbar(gameInstance.getGiftManager().getSpecialSpawnerCount()));
            }
        }
    }

    @Override
    public void onExit(GameInstance gameInstance)
    {

    }
}

class ReadyingState extends State<GameInstance, GHStates>
{
    private final int _duration;
    private int _timer;

    public ReadyingState(int duration)
    {
        _duration = duration;
    }

    @Override
    public void onEnter(GameInstance gameInstance)
    {
        _timer = -1;
        LazuliUI.broadcast(MessageFactory.getGameReadyingMsg(_duration));
        gameInstance.getGiftManager().removeAllGifts();
        GiftHunting.GetPlugin().getScoreObjective().setDisplaySlot(DisplaySlot.BELOW_NAME);
    }

    @Override
    public void onUpdate(GameInstance gameInstance)
    {
        _timer++;

        if(_timer % 20 == 0)
            LazuliUI.broadcast(MessageFactory.getGameReadyingActionbar(_duration, _timer));

        if(_timer == _duration - 200)
        {
            LazuliUI.broadcast(MessageFactory.getGameIntroMsg(gameInstance.getVictoryScore()));
            for (GHPlayer ghPlayer : gameInstance.getPlayerManager().getOnlineGHPlayers())
                ghPlayer.getPlayer().teleport(gameInstance.getGameSpawn());
        }

        if(_timer >= _duration)
        {
            gameInstance.switchState(GHStates.PROGRESSING);
        }
    }

    @Override
    public void onExit(GameInstance gameInstance)
    {

    }
}

class ProgressingState extends State<GameInstance, GHStates>
{
    private int _timer;
    private int _normalGiftSpawnTimer = -1;
    private int _specialGiftSpawnTimer = -1;
    private int _discipleBirthTimer = -1;
    private int _stealerGiveTimer = -1;
    private boolean _hasDawnBuff = false;

    public boolean anyPlayerHasDawnBuff()
    {
        return _hasDawnBuff;
    }

    @Override
    public void onEnter(GameInstance gameInstance)
    {
        _timer = -1;
        _normalGiftSpawnTimer = 60;
        _specialGiftSpawnTimer = gameInstance.getSpecialGiftSpawnInterval();
        _discipleBirthTimer = -1;
        _stealerGiveTimer = -1;
        for (GHPlayer ghPlayer : gameInstance.getPlayerManager().getOnlineGHPlayers())
        {
            Player player = ghPlayer.getPlayer();
            ghPlayer.reset();
            MCUtil.ClearInventory(player);
            ghPlayer.enableSkill(Skill.BOOST);
            ghPlayer.enableSkill(Skill.COUNTER);
            LazuliUI.sendMessage(player, MessageFactory.getGameStartMsg());
            LazuliUI.sendMessage(player, MessageFactory.getProgressingActionbarSuffix(ghPlayer));
            LazuliUI.sendMessage(player, MessageFactory.getGameStartActionbar());
        }
    }

    @Override
    public void onUpdate(GameInstance gameInstance)
    {
        _timer++;

        // update player actionbar
        if(_timer % 20 == 0)
        {
            for(GHPlayer ghPlayer : gameInstance.getPlayerManager().getOnlineGHPlayers())
                LazuliUI.sendMessage(ghPlayer.getPlayer(), MessageFactory.getProgressingActionbarPrefix(_timer));
        }
        // spawn normal gift
        if(_normalGiftSpawnTimer == -1)
        {
            int threshold = (int)(gameInstance.getSpawnGiftCountPerPlayer() * gameInstance.getPlayerManager().getOnlineGHPlayerCount() * gameInstance.getNewGiftBatchThreshold());
            if(gameInstance.getGiftManager().getNormalGiftCount() < threshold)
                _normalGiftSpawnTimer = gameInstance.getNewGiftBatchDelay();
        }
        else
        {
            _normalGiftSpawnTimer--;
            if(_normalGiftSpawnTimer == 0)
            {
                int count = gameInstance.getSpawnGiftCountPerPlayer() * gameInstance.getPlayerManager().getOnlineGHPlayerCount();
                gameInstance.getGiftManager().spawnNormalGifts(count);
                LazuliUI.broadcast(MessageFactory.getDeliverNormalGiftMsg());
                _normalGiftSpawnTimer = -1;
            }
        }
        // spawn special gift
        if(_specialGiftSpawnTimer == -1)
        {
            if(!gameInstance.getGiftManager().hasSpecialGift())
                _specialGiftSpawnTimer = gameInstance.getSpecialGiftSpawnInterval();
        }
        else
        {
            _specialGiftSpawnTimer--;
            if(_specialGiftSpawnTimer == 0)
            {
                gameInstance.getGiftManager().spawnSpecialGift();
                LazuliUI.broadcast(MessageFactory.getDeliverSpecialGiftMsg());
                _specialGiftSpawnTimer = -1;
                _discipleBirthTimer = 60;
            }
        }

        /*if(_discipleBirthTimer != -1)
        {
            _discipleBirthTimer--;
            if(_discipleBirthTimer == 0)
            {
                List<GHPlayer> ghPlayers = gameInstance.getPlayerManager().getOnlineGHPlayers();
                if(!ghPlayers.isEmpty())
                {
                    GHPlayer disciple = RandUtil.Pick(ghPlayers);
                    disciple.isDisciple = true;
                    LazuliUI.broadcast(MessageFactory.getDiscipleBirthMsg(disciple));
                    disciple.getPlayer().getWorld().playSound(disciple.getPlayer(), Sound.ENTITY_EVOKER_PREPARE_SUMMON, SoundCategory.MASTER, 1.0f, 0.8f);
                }
                _discipleBirthTimer = -1;
            }
        }*/

        // give stealer
        int stealerGiveScore = (int)(gameInstance.getVictoryScore() * gameInstance.getStealerGiveWhenHighestScore());
        if(_stealerGiveTimer == -1)
        {
            List<GHPlayer> ghPlayers = gameInstance.getPlayerManager().getAllGHPlayersSorted();
            if(!ghPlayers.isEmpty())
            {
                GHPlayer highestScorePlayer = ghPlayers.getFirst();
                if(highestScorePlayer.getScore() >= stealerGiveScore)
                    _stealerGiveTimer = gameInstance.getStealerGiveInterval();
            }
        }
        else
        {
            _stealerGiveTimer--;
            if(_stealerGiveTimer == 0)
            {
                for(GHPlayer ghPlayer : gameInstance.getPlayerManager().getOnlineGHPlayers())
                {
                    if(ghPlayer.getScore() < stealerGiveScore)
                        MCUtil.GiveItem(ghPlayer.getPlayer(), CustomItem.STEALER.create());
                }
                LazuliUI.broadcast(MessageFactory.getGiveStealerMsg(stealerGiveScore));
                _stealerGiveTimer = -1;
            }
        }

        // update hasDawnBuff
        if(_timer % 20 == 0)
        {
            _hasDawnBuff = false;
            for(var gh : gameInstance.getPlayerManager().getOnlineGHPlayers())
            {
                if(gh.hasBuff(DawnBuff.class))
                {
                    _hasDawnBuff = true;
                    break;
                }
            }
        }

        // check victory condition
        List<GHPlayer> ghPlayers = gameInstance.getPlayerManager().getAllGHPlayersSorted();
        if(!ghPlayers.isEmpty() && ghPlayers.getFirst().getScore() >= gameInstance.getVictoryScore())
            gameInstance.switchState(GHStates.FINISHED);
    }

    @Override
    public void onExit(GameInstance gameInstance)
    {
        for(var gh : gameInstance.getPlayerManager().getOnlineGHPlayers()) gh.disableAllSkills();
        // clear gifts
        gameInstance.getGiftManager().removeAllGifts();
    }
}

class FinishedState extends State<GameInstance, GHStates>
{
    private final int _duration;
    private int _timer;

    public FinishedState(int duration)
    {
        _duration = duration;
    }

    @Override
    public void onEnter(GameInstance gameInstance)
    {
        _timer = -1;
        for (GHPlayer ghPlayer : gameInstance.getPlayerManager().getOnlineGHPlayers())
        {
            Player player = ghPlayer.getPlayer();
            MCUtil.ClearInventory(player);
            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedMsg(ghPlayer, gameInstance.getPlayerManager().getAllGHPlayersSorted()));
            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedActionbarInfix(_duration));
            LazuliUI.sendMessage(player, MessageFactory.getGameFinishedActionbarSuffix(ghPlayer, _duration));
        }
    }

    @Override
    public void onUpdate(GameInstance gameInstance)
    {
        _timer ++;

        if(_timer >= _duration)
            gameInstance.switchState(GHStates.IDLE);
    }

    @Override
    public void onExit(GameInstance gameInstance)
    {
        for(GHPlayer ghPlayer : gameInstance.getPlayerManager().getOnlineGHPlayers())
        {
            Player player = ghPlayer.getPlayer();
            MCUtil.ClearInventory(player);
            LazuliUI.flush(player);
            LazuliUI.sendMessage(player, MessageFactory.getGameBackToIdleMsg());
            player.teleport(gameInstance.getGameSpawn());
        }
        List<GHPlayer> ghPlayers = gameInstance.getPlayerManager().getAllGHPlayersSorted();
        for(int i = 0; i < ghPlayers.size(); i ++)
        {
            GHPlayer ghPlayer = ghPlayers.get(i);
            Player player = ghPlayer.getPlayer();
            CustomItem.UpdateSouvenir(player, i + 1, ghPlayers.size(), ghPlayer.getScore());
            player.playSound(player, Sound.ENTITY_ITEM_PICKUP, SoundCategory.MASTER, 1.0f, 1.0f);
        }
    }
}
