package io.lazysheeep.gifthunting.utils;

import io.lazysheeep.gifthunting.GiftHunting;
import io.lazysheeep.gifthunting.factory.CustomItem;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
        List<LootTableEntry> entries = new ArrayList<>();

        if (entriesConfigNode.isList())
        {
            for (ConfigurationNode entryNode : entriesConfigNode.childrenList())
            {
                String id = entryNode.node("id").getString();
                float weight = entryNode.node("weight").getFloat(0.0f);
                if (id == null)
                {
                    GiftHunting.Log(Level.WARNING, "LootTable: loot entry missing id, skipped.");
                    continue;
                }
                CustomItem itemType = CustomItem.fromId(id);
                if (itemType == null)
                {
                    GiftHunting.Log(Level.WARNING, "LootTable: unknown item id '" + id + "', skipped.");
                    continue;
                }
                if (weight <= 0.0f)
                {
                    continue;
                }
                entries.add(new LootTableEntry(itemType.create(), weight));
            }
        }
        else
        {
            GiftHunting.Log(Level.WARNING, "LootTable: missing loot entries, skipped.");
        }

        _lootEntries = entries;

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
        if (_rollTimes <= 0 || _rollChance <= 0.0f || _lootEntries.isEmpty() || _totalWeight <= 0.0f)
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
