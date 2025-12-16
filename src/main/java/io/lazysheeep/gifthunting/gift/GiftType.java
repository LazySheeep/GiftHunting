package io.lazysheeep.gifthunting.gift;

import io.lazysheeep.gifthunting.factory.ItemFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;

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
    public List<Pair<ItemStack, Float>> lootTable;

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

        lootTable = List.of(new Pair[]{
            Pair.of(ItemFactory.Club, lootProbability_club),
            Pair.of(ItemFactory.Booster, lootProbability_booster),
            Pair.of(ItemFactory.Silencer, lootProbability_silencer),
            Pair.of(ItemFactory.Reflector, lootProbability_reflector),
            Pair.of(ItemFactory.Revolution, lootProbability_revolution),
            Pair.of(ItemFactory.SpeedUp, lootProbability_speedUp),
        });
    }
}
