package io.lazysheeep.gifthunting.gift;

import org.spongepowered.configurate.ConfigurationNode;

public class GiftType
{
    public final int clicksPerFetch;
    public final int scorePerFetch;
    public final int scoreVariation;
    public final int capacityInFetches;
    public final String texture;

    public final float lootProbability_club;
    public final float lootProbability_booster;
    public final float lootProbability_silencer;
    public final float lootProbability_reflector;
    public final float lootProbability_revolution;
    public final float lootProbability_speedUp;

    public GiftType(ConfigurationNode configNode)
    {
        this.clicksPerFetch = configNode.node("clicksPerFetch").getInt();
        this.scorePerFetch = configNode.node("scorePerFetch").getInt();
        this.scoreVariation = configNode.node("scoreVariation").getInt();
        this.capacityInFetches = configNode.node("capacityInFetches").getInt();
        this.texture = configNode.node("texture").getString();

        ConfigurationNode lootConfigNode = configNode.node("loot");
        lootProbability_club = lootConfigNode.node("club").getFloat();
        lootProbability_booster = lootConfigNode.node("booster").getFloat();
        lootProbability_silencer = lootConfigNode.node("silencer").getFloat();
        lootProbability_reflector = lootConfigNode.node("reflector").getFloat();
        lootProbability_revolution = lootConfigNode.node("revolution").getFloat();
        lootProbability_speedUp = lootConfigNode.node("speedUp").getFloat();
    }
}
