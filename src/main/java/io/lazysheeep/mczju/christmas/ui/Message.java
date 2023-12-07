package io.lazysheeep.mczju.christmas.ui;

import net.kyori.adventure.text.Component;

public class Message
{
    public enum Type { CHAT, ACTIONBAR_PREFIX, ACTIONBAR_INFIX, ACTIONBAR_SUFFIX, TITLE }
    Type type;
    Component content;
    int lifeTime;
    boolean sent = false;

    public Message(Type type, Component content, int lifeTime)
    {
        this.type = type;
        this.content = content;
        this.lifeTime = lifeTime == 0 ? 1 : lifeTime;
    }
}
