package io.lazysheeep.mczju.christmas.ui;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class UI implements Listener
{
    private final Player player;
    private final LinkedList<Message> messageList = new LinkedList<>();
    private final Message[] messagesOnStage = new Message[Message.Type.values().length];
    private int actionbarInfixWidth = 0;

    public UI(Player player)
    {
        this.player = player;
    }

    public void setActionbarInfixWidth(int width)
    {
        this.actionbarInfixWidth = width;
    }

    public void sendMessage(Message message)
    {
        this.messageList.offerLast(message);
    }

    public void flush(Message.Type... types)
    {
        for(Message.Type type : types)
        {
            messagesOnStage[type.ordinal()] = null;
            messageList.removeIf(message -> message.type == type);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerTickStartEvent(ServerTickStartEvent event)
    {
        // load message from messageList
        for(Iterator<Message> it = this.messageList.iterator(); it.hasNext(); )
        {
            Message message = it.next();
            // load
            if(this.messagesOnStage[message.type.ordinal()] == null || message.loadMode == Message.LoadMode.REPLACE)
            {
                this.messagesOnStage[message.type.ordinal()] = message;
                it.remove();
            }
            // clear IMMEDIATE messages that can't be load
            else if(message.loadMode == Message.LoadMode.IMMEDIATE)
            {
                it.remove();
            }
        }

        // send message
        // chat
        Message chatMessage = this.messagesOnStage[Message.Type.CHAT.ordinal()];
        if(chatMessage != null && !chatMessage.sent)
            this.player.sendMessage(chatMessage.content);

        // actionbar
        Message actionbarPrefix = this.messagesOnStage[Message.Type.ACTIONBAR_PREFIX.ordinal()];
        Message actionbarInfix = this.messagesOnStage[Message.Type.ACTIONBAR_INFIX.ordinal()];
        Message actionbarSuffix = this.messagesOnStage[Message.Type.ACTIONBAR_SUFFIX.ordinal()];
        int prefixLength = actionbarPrefix == null ? 0 : Util.getTextComponentLength(actionbarPrefix.content);
        int infixLength = actionbarInfix == null ? 0 : Util.getTextComponentLength(actionbarInfix.content);
        int suffixLength = actionbarSuffix == null ? 0 : Util.getTextComponentLength(actionbarSuffix.content);
        int prefixPaddingLength = Math.max(prefixLength, suffixLength) - prefixLength;
        int infixPaddingLength = Math.max(this.actionbarInfixWidth - infixLength, 0);
        int suffixPaddingLength = Math.max(prefixLength, suffixLength) - suffixLength;
        TextComponent actionbarComponent = Component.text("");
        // prefix
        if(prefixPaddingLength > 0)
            actionbarComponent = actionbarComponent.append(Component.text("*".repeat(prefixPaddingLength)));
        if(prefixLength > 0)
            actionbarComponent = actionbarComponent.append(actionbarPrefix.content);
        // infix
        if(infixPaddingLength > 0)
            actionbarComponent = actionbarComponent.append(Component.text("_".repeat(infixPaddingLength/2)));
        if(infixLength > 0)
            actionbarComponent = actionbarComponent.append(actionbarInfix.content);
        if(infixPaddingLength > 0)
            actionbarComponent = actionbarComponent.append(Component.text("_".repeat(infixPaddingLength-infixPaddingLength/2)));
        // suffix
        if(suffixLength > 0)
            actionbarComponent = actionbarComponent.append(actionbarSuffix.content);
        if(suffixPaddingLength > 0)
            actionbarComponent = actionbarComponent.append(Component.text("*".repeat(suffixPaddingLength)));

        player.sendActionBar(actionbarComponent);

        // update or unload the messagesOnStage
        for(int i = 0; i < messagesOnStage.length; i ++)
        {
            if(messagesOnStage[i] != null)
            {
                // set sent to true
                messagesOnStage[i].sent = true;
                // update lifeTime
                if(messagesOnStage[i].lifeTime > 0)
                    messagesOnStage[i].lifeTime --;
                // unload if lifeTime is 0
                if(messagesOnStage[i].lifeTime == 0)
                    messagesOnStage[i] = null;
            }
        }
    }
}
