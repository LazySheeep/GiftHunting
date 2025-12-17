package io.lazysheeep.gifthunting.gift;

import io.lazysheeep.gifthunting.utils.LootTable;
import org.spongepowered.configurate.ConfigurationNode;

public class GiftType
{
    public final int clicksPerFetch;
    public final int scorePerFetch;
    public final int scoreVariation;
    public final int capacityInFetches;
    public final String texture;
    public final LootTable lootTable;

    public GiftType(ConfigurationNode configNode)
    {
        this.clicksPerFetch = configNode.node("clicksPerFetch").getInt();
        this.scorePerFetch = configNode.node("scorePerFetch").getInt();
        this.scoreVariation = configNode.node("scoreVariation").getInt();
        this.capacityInFetches = configNode.node("capacityInFetches").getInt();
        this.texture = configNode.node("texture").getString();

        lootTable = new LootTable(configNode.node("lootTable"));
    }
}
