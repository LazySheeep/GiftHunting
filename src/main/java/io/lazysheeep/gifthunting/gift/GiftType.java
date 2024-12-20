package io.lazysheeep.gifthunting.gift;

import io.lazysheeep.gifthunting.GiftHunting;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class GiftType
{
    private static final Map<String, GiftType> _Values = new HashMap<>();

    public static @Nullable GiftType valueOf(@NotNull String name)
    {
        return _Values.get(name);
    }

    public static void LoadConfig()
    {
        _Values.clear();
        for (ConfigurationNode giftTypeNode : GiftHunting.GetPlugin().getConfigRootNode().node("giftTypes").childrenList())
        {
            String name = giftTypeNode.node("name").getString("gift_type");
            int clicksPerFetch = giftTypeNode.node("clicksPerFetch").getInt();
            int scorePerFetch = giftTypeNode.node("scorePerFetch").getInt();
            int capacityInFetches = giftTypeNode.node("capacityInFetches").getInt();
            String texture = giftTypeNode.node("texture").getString();
            _Values.put(name, new GiftType(name, clicksPerFetch, scorePerFetch, capacityInFetches, texture));
        }
        GiftHunting.Log(Level.INFO, "Loaded " + _Values.size() + " gift types");
    }


    private final String _name;
    private final int _clicksPerFetch;
    private final int _scorePerFetch;
    private final int _capacityInFetches;
    private final String _texture;

    public String getName()
    {
        return _name;
    }

    public int getClicksPerFetch()
    {
        return _clicksPerFetch;
    }

    public int getScorePerFetch()
    {
        return _scorePerFetch;
    }

    public int getCapacityInFetches()
    {
        return _capacityInFetches;
    }

    public String getTexture()
    {
        return _texture;
    }

    private GiftType(@NotNull String name, int clicksPerFetch, int scorePerFetch, int capacityInFetches, String texture)
    {
        _name = name;
        _clicksPerFetch = clicksPerFetch;
        _scorePerFetch = scorePerFetch;
        _capacityInFetches = capacityInFetches;
        _texture = texture;
    }
}
