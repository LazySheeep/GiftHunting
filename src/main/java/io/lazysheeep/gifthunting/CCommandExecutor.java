package io.lazysheeep.gifthunting;

import io.lazysheeep.uimanager.Message;
import io.lazysheeep.uimanager.UIManager;
import net.kyori.adventure.text.Component;
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
            case "get" ->   // get item
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
                            UIManager.sendMessage(player, new Message(Message.Type.CHAT, MessageFactory.getClearAllGiftLog(counter), Message.LoadMode.IMMEDIATE, 1));
                    }
                    case "untracked" ->
                    {
                        int counter = Gift.clearUnTracked();
                        if(sender instanceof Player player)
                            UIManager.sendMessage(player, new Message(Message.Type.CHAT, MessageFactory.getClearUntrackedGiftLog(counter), Message.LoadMode.IMMEDIATE, 1));
                    }
                    default -> { return false; }
                }
                else return false;
            }

            case "game" ->  // manage game state
            {
                if(args.length >= 2) switch (args[1])
                {
                    case "start" -> // start the game
                    {
                        if (!GiftHunting.gameManager.switchState(GameManager.State.READYING))
                            sender.sendMessage(MessageFactory.getEventCantStartMsg());
                    }
                    case "end" -> // end the game
                    {
                        if (!GiftHunting.gameManager.switchState(GameManager.State.IDLE))
                            sender.sendMessage(MessageFactory.getEventCantEndMsg());
                    }
                    case "pause" ->
                    {
                        if(!GiftHunting.gameManager.switchState(GameManager.State.PAUSED))
                            sender.sendMessage(MessageFactory.getEventCantPauseMsg());
                    }
                    case "unpause" ->
                    {
                        if(!GiftHunting.gameManager.switchState(GameManager.State.UNPAUSE))
                            sender.sendMessage(MessageFactory.getEventCantUnpauseMsg());
                    }
                    case "stats" -> // print event stats
                    {
                        sender.sendMessage(MessageFactory.getGameStatsMsg());
                    }
                    default -> { return false; }
                }
                else return false;
            }

            case "test" ->
            {
                if(sender instanceof Player player)
                {
                    UIManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 0"), Message.LoadMode.REPLACE, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 1"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.CHAT, Component.text("test chat message 2"), Message.LoadMode.WAIT, 20));

                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_PREFIX, Component.text("actionbar_prefix "), Message.LoadMode.REPLACE, -1));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_INFIX, Component.text("actionbar_infix "), Message.LoadMode.REPLACE, 100));

                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 1"), Message.LoadMode.REPLACE, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 2"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 3"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 4"), Message.LoadMode.WAIT, 20));
                    UIManager.sendMessage(player, new Message(Message.Type.ACTIONBAR_SUFFIX, Component.text("actionbar_suffix 5"), Message.LoadMode.WAIT,20));
                }
            }

            default -> { return false; }
        }
        else return false;
        return true;
    }
}
