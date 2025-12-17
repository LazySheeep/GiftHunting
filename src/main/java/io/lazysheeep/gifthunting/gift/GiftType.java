package io.lazysheeep.gifthunting.gift;

import io.lazysheeep.gifthunting.utils.LootTable;
import org.spongepowered.configurate.ConfigurationNode;

public class GiftType
{
    public final int clicksPerFetch;
    public final int scorePerFetch;
    public final int capacityInFetches;
    public final String texture;
    public final LootTable lootTable;
    public final double lootRadius;
    public final double lootWeightShape;

    public GiftType(ConfigurationNode configNode)
    {
        this.clicksPerFetch = configNode.node("clicksPerFetch").getInt();
        this.scorePerFetch = configNode.node("scorePerFetch").getInt();
        this.capacityInFetches = configNode.node("capacityInFetches").getInt();
        this.texture = configNode.node("texture").getString();
        this.lootTable = new LootTable(configNode.node("lootTable"));
        this.lootRadius = configNode.node("lootRadius").getDouble();
        this.lootWeightShape = configNode.node("lootWeightShape").getDouble();
    }
}
