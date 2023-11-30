package io.lazysheeep.mczju.christmas;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Cfg
{
    public int readyStateDuration;
    public int progressStateDuration;
    public List<Map<String, Object>> giftBatches;
    public int clicksPerFetch_normal;
    public int scorePerFetch_normal;
    public int capacityInFetches_normal;
    public int clicksPerFetch_special;
    public int scorePerFetch_special;
    public int capacityInFetches_special;
    public List<Location> giftSpawnerLocations;


    private final FileConfiguration fileConfig;
    private final Christmas plugin;

    public Cfg(Christmas plugin)
    {
        plugin.saveDefaultConfig();
        this.fileConfig = plugin.getConfig();
        this.plugin = plugin;
    }

    // load cfg from file
    public void load()
    {
        this.readyStateDuration = fileConfig.getInt("readyStateDuration");
        this.progressStateDuration = fileConfig.getInt("progressStateDuration");

        this.giftBatches = Util.castMapList(fileConfig.getMapList("giftBatches"), String.class, Object.class);

        this.clicksPerFetch_normal = fileConfig.getInt("clicksPerFetch_normal");
        this.scorePerFetch_normal = fileConfig.getInt("scorePerFetch_normal");
        this.capacityInFetches_normal = fileConfig.getInt("capacityInFetches_normal");

        this.clicksPerFetch_special = fileConfig.getInt("clicksPerFetch_special");
        this.scorePerFetch_special = fileConfig.getInt("scorePerFetch_special");
        this.capacityInFetches_special = fileConfig.getInt("capacityInFetches_special");

        this.giftSpawnerLocations = Util.castList(fileConfig.getList("giftSpawnerLocations"), Location.class);
    }

    // save cfg to file
    public void save()
    {
        fileConfig.set("giftSpawnerLocations", this.giftSpawnerLocations);

        try {
            File file = new File(plugin.getDataFolder(), "config.yml");
            fileConfig.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
