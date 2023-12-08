package io.lazysheeep.mczju.christmas.ui;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;

public class Util
{
    static public int getTextComponentLength(TextComponent component)
    {
        return getStringLength(PlainTextComponentSerializer.plainText().serialize(component));
    }

    static public int getStringLength(String s)
    {
        int length = 0;
        for(int i = 0; i < s.length(); i++)
        {
            int ascii = Character.codePointAt(s, i);
            if(ascii >= 0 && ascii <=255)
                length ++;
            else
                length += 2;
        }
        return length;
    }
}
