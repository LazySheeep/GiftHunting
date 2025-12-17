package io.lazysheeep.gifthunting.utils;

import io.lazysheeep.gifthunting.factory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;

public class LootTable
{
    static class LootTableEntry
    {
        public ItemStack item;
        public float weight;

        public LootTableEntry(ItemStack item, float weight)
        {
            this.item = item;
            this.weight = weight;
        }
    }

    private final int _rollTimes;
    private final float _rollChance;
    private final List<LootTableEntry> _lootEntries;
    private final float _totalWeight;

    public LootTable(ConfigurationNode configNode)
    {
        _rollTimes = configNode.node("rollTimes").getInt();
        _rollChance = configNode.node("rollChance").getFloat();

        ConfigurationNode entriesConfigNode = configNode.node("loots");
        float lootWeight_club = entriesConfigNode.node("club").getFloat();
        float lootWeight_booster = entriesConfigNode.node("booster").getFloat();
        float lootWeight_silencer = entriesConfigNode.node("silencer").getFloat();
        float lootWeight_reflector = entriesConfigNode.node("reflector").getFloat();
        float lootWeight_revolution = entriesConfigNode.node("revolution").getFloat();
        float lootWeight_speedUp = entriesConfigNode.node("speedUp").getFloat();

        _lootEntries = List.of(new LootTableEntry[]{
            new LootTableEntry(ItemFactory.Club, lootWeight_club),
            new LootTableEntry(ItemFactory.Booster, lootWeight_booster),
            new LootTableEntry(ItemFactory.Silencer, lootWeight_silencer),
            new LootTableEntry(ItemFactory.Reflector, lootWeight_reflector),
            new LootTableEntry(ItemFactory.Revolution, lootWeight_revolution),
            new LootTableEntry(ItemFactory.SpeedUp, lootWeight_speedUp),
        });

        float totalWeight = 0.0f;
        for(LootTableEntry entry : _lootEntries)
        {
            totalWeight += entry.weight;
        }
        _totalWeight = totalWeight;
    }

    public List<ItemStack> loot()
    {
        List<ItemStack> loots = new ArrayList<>();
        if (_rollTimes <= 0 || _rollChance <= 0.0f || _lootEntries.isEmpty())
            return loots;

        for (int i = 0; i < _rollTimes; i++)
        {
            if (RandUtil.nextBool(_rollChance))
            {
                float randomValue = RandUtil.nextFloat(0.0f, _totalWeight);
                float cumulativeWeight = 0.0f;
                for(LootTableEntry entry : _lootEntries)
                {
                    cumulativeWeight += entry.weight;
                    if(randomValue <= cumulativeWeight)
                    {
                        loots.add(entry.item.clone());
                        break;
                    }
                }
            }
        }

        return loots;
    }
}
