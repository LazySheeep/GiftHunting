package io.lazysheeep.gifthunting;

import io.lazysheeep.lazuliui.Message;
import io.lazysheeep.lazuliui.LazuliUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CCommandExecutor implements CommandExecutor
{
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args)
    {
        if(args.length >= 1) switch (args[0])
        {
            // get items...
            case "get" ->
            {
                if(args.length >= 2) switch (args[1])
                {
                    case "setter" ->
                    {
                        if(sender instanceof Player player)
                            player.getInventory().addItem(ItemFactory.giftSpawnerSetter);
                    }
                    case "booster" ->
                    {
                        if(sender instanceof Player player)
                            player.getInventory().addItem(ItemFactory.booster);
                    }
                    case "club" ->
                    {
                        if(sender instanceof Player player)
                            player.getInventory().addItem(ItemFactory.club);
                    }
                    default -> { return false; }
                }
                else return false;
            }
            // clear things...
            case "clear" ->
            {
                if(args.length >= 2) switch (args[1])
                {
                    case "spawner" ->
                    {
                        GiftHunting.config.clearGiftSpawners();
                    }
                    case "gift" ->
                    {
                        int counter = Gift.clearGifts();
                        if(sender instanceof Player player)
                            LazuliUI.sendMessage(player, new Message(Message.Type.CHAT, MessageFactory.getClearAllGiftMsg(counter), Message.LoadMode.IMMEDIATE, 1));
                    }
                    case "untracked" ->
                    {
                        int counter = Gift.clearUnTracked();
                        if(sender instanceof Player player)
                            LazuliUI.sendMessage(player, new Message(Message.Type.CHAT, MessageFactory.getClearUntrackedGiftMsg(counter), Message.LoadMode.IMMEDIATE, 1));
                    }
                    default -> { return false; }
                }
                else return false;
            }
            // start the game
            case "start" ->
            {
                if (!GiftHunting.gameManager.switchState(GameManager.State.READYING))
                    sender.sendMessage(MessageFactory.getEventCantStartText());
            }
            // terminate the game
            case "end" ->
            {
                if (!GiftHunting.gameManager.switchState(GameManager.State.IDLE))
                    sender.sendMessage(MessageFactory.getEventCantEndText());
            }
            // pause the game
            case "pause" ->
            {
                if(!GiftHunting.gameManager.switchState(GameManager.State.PAUSED))
                    sender.sendMessage(MessageFactory.getEventCantPauseText());
            }
            // unpause the game
            case "unpause" ->
            {
                if(!GiftHunting.gameManager.switchState(GameManager.State.UNPAUSE))
                    sender.sendMessage(MessageFactory.getEventCantUnpauseText());
            }
            // print event stats
            case "stats" ->
            {
                sender.sendMessage(MessageFactory.getGameStatsText());
            }
            // set game spawn
            case "setspawn" ->
            {
                if(sender instanceof Player player && GiftHunting.gameManager.getState() == GameManager.State.IDLE)
                {
                    GiftHunting.config.gameSpawn = player.getLocation();
                    LazuliUI.sendMessage(player, MessageFactory.getSetGameSpawnMsg());
                }
            }
            default -> { return false; }
        }
        else return false;
        return true;
    }
}
