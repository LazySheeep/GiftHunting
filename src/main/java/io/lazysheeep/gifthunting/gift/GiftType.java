package io.lazysheeep.gifthunting.gift;

import io.lazysheeep.gifthunting.GiftHunting;
import org.spongepowered.configurate.ConfigurationNode;

public class GiftType
{
    public static GiftType NORMAL;
    public static GiftType SPECIAL;

    public static void LoadConfig()
    {
        ConfigurationNode giftTypeNode = GiftHunting.GetPlugin().getConfigRootNode().node("normalGift");
        int clicksPerFetch = giftTypeNode.node("clicksPerFetch").getInt();
        int scorePerFetch = giftTypeNode.node("scorePerFetch").getInt();
        int scoreVariation = giftTypeNode.node("scoreVariation").getInt();
        String texture = giftTypeNode.node("texture").getString();
        NORMAL = new GiftType(clicksPerFetch, scorePerFetch, scoreVariation, 0.0f, texture);

        giftTypeNode = GiftHunting.GetPlugin().getConfigRootNode().node("specialGift");
        clicksPerFetch = giftTypeNode.node("clicksPerFetch").getInt();
        scorePerFetch = giftTypeNode.node("scorePerFetch").getInt();
        scoreVariation = giftTypeNode.node("scoreVariation").getInt();
        float capacityMultiplierPerPlayer = giftTypeNode.node("capacityMultiplierPerPlayer").getFloat();
        texture = giftTypeNode.node("texture").getString();
        SPECIAL = new GiftType(clicksPerFetch, scorePerFetch, scoreVariation, capacityMultiplierPerPlayer, texture);
    }

    public final int clicksPerFetch;
    public final int scorePerFetch;
    public final int scoreVariation;
    public final float capacityMultiplierPerPlayer;
    public final String texture;

    private GiftType(int clicksPerFetch, int scorePerFetch, int scoreVariation, float capacityMultiplierPerPlayer, String texture)
    {
        this.clicksPerFetch = clicksPerFetch;
        this.scorePerFetch = scorePerFetch;
        this.scoreVariation = scoreVariation;
        this.capacityMultiplierPerPlayer = capacityMultiplierPerPlayer;
        this.texture = texture;
    }
}
