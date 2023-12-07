package io.lazysheeep.mczju.christmas.ui;

import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.TitlePart;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Deque;
import java.util.LinkedList;

public class UI implements Listener
{
    private final Player player;
    private final Deque<Message>[] messageQueue = new Deque[Message.Type.values().length];

    public UI(Player player)
    {
        this.player = player;
        for(int i = 0; i < messageQueue.length; i ++)
        {
            messageQueue[i] = new LinkedList<Message>();
        }
    }

    public void sendMessage(Message message)
    {
        messageQueue[message.type.ordinal()].offerLast(message);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onServerTickStartEvent(ServerTickStartEvent event)
    {
        // chat
        Message chatMessage = messageQueue[Message.Type.CHAT.ordinal()].peekFirst();
        if(chatMessage != null && !chatMessage.sent)
            player.sendMessage(chatMessage.content);

        // actionbar
        Message actionbarPrefix = messageQueue[Message.Type.ACTIONBAR_PREFIX.ordinal()].peekFirst();
        Message actionbarInfix = messageQueue[Message.Type.ACTIONBAR_INFIX.ordinal()].peekFirst();
        Message actionbarSuffix = messageQueue[Message.Type.ACTIONBAR_SUFFIX.ordinal()].peekFirst();
        Component actionbarComponent = Component.text("");
        if(actionbarPrefix != null)
            actionbarComponent = actionbarComponent.append(actionbarPrefix.content);
        if(actionbarInfix != null)
            actionbarComponent = actionbarComponent.append(actionbarInfix.content);
        if(actionbarSuffix != null)
            actionbarComponent = actionbarComponent.append(actionbarSuffix.content);
        if(!actionbarComponent.equals(Component.text("")))
            player.sendActionBar(actionbarComponent);

        // title
        Message titleMessage = messageQueue[Message.Type.TITLE.ordinal()].peekFirst();
        if(titleMessage != null)
            player.sendTitlePart(TitlePart.TITLE, titleMessage.content);

        // update queue
        for(Message.Type type : Message.Type.values())
        {
            Message currentMessage = messageQueue[type.ordinal()].peekFirst();
            if(currentMessage != null)
            {
                currentMessage.sent = true;
                if(currentMessage.lifeTime > 0)
                    currentMessage.lifeTime --;
                if(currentMessage.lifeTime == 0)
                    messageQueue[type.ordinal()].removeFirst();
            }
        }
    }
}
