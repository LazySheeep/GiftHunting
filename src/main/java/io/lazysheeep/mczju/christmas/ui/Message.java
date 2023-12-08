package io.lazysheeep.mczju.christmas.ui;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public class Message
{
    public enum Type { CHAT, ACTIONBAR_PREFIX, ACTIONBAR_INFIX, ACTIONBAR_SUFFIX }
    public enum LoadMode { IMMEDIATE, WAIT, REPLACE }
    Type type;
    LoadMode loadMode;
    TextComponent content;
    int lifeTime;
    boolean sent = false;

    public Message(Type type, TextComponent content, LoadMode loadMode, int lifeTime)
    {
        this.type = type;
        this.content = content;
        this.loadMode = loadMode;
        this.lifeTime = lifeTime;
    }
}
