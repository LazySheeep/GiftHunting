package io.lazysheeep.gifthunting;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

class Config
{
    public String worldName;
    public Location gameSpawn;
    public int readyStateDuration;
    public int progressStateDuration;
    public int finishedStateDuration;
    public List<Map<String, Object>> giftBatches;
    public float bonusPercentage;
    public List<Integer> bonusEvents;
    public int clicksPerFetch_normal;
    public int scorePerFetch_normal;
    public int capacityInFetches_normal;
    public int clicksPerFetch_special;
    public int scorePerFetch_special;
    public int capacityInFetches_special;
    public int promptInterval_special;
    public float lootProbability_club;
    public float lootProbability_booster;
    public int stealerScore;
    private List<Location> giftSpawnerLocations;

    public List<Location> getGiftSpawnerLocations()
    {
        ArrayList<Location> result = new ArrayList<>();
        for(Location element : giftSpawnerLocations)
            result.add(element.clone());
        return result;
    }

    public int getGiftSpawnerCount()
    {
        return giftSpawnerLocations.size();
    }

    public void addGiftSpawner(Location location)
    {
        giftSpawnerLocations.add(location);
    }

    public boolean removeGiftSpawner(Location location)
    {
        if(giftSpawnerLocations.contains(location))
        {
            giftSpawnerLocations.remove(location);
            return true;
        }
        return false;
    }

    public void clearGiftSpawners()
    {
        giftSpawnerLocations.clear();
        GiftHunting.plugin.logger.log(Level.INFO, "cleared gift-spawners");
    }

    private final FileConfiguration fileConfig;
    private final GiftHunting plugin;

    public Config(GiftHunting plugin)
    {
        plugin.saveDefaultConfig();
        this.fileConfig = plugin.getConfig();
        this.plugin = plugin;
    }

    // load cfg from file
    public void load()
    {
        this.worldName = fileConfig.getString("worldName");
        this.gameSpawn = fileConfig.getLocation("gameSpawn");

        this.readyStateDuration = fileConfig.getInt("readyStateDuration");
        this.progressStateDuration = fileConfig.getInt("progressStateDuration");
        this.finishedStateDuration = fileConfig.getInt("finishedStateDuration");

        this.giftBatches = Util.castMapList(fileConfig.getMapList("giftBatches"), String.class, Object.class);

        this.bonusPercentage = (float) fileConfig.getDouble("bonusPercentage");
        this.bonusEvents = Util.castList(fileConfig.getList("bonusEvents"), Integer.class);

        this.clicksPerFetch_normal = fileConfig.getInt("clicksPerFetch_normal");
        this.scorePerFetch_normal = fileConfig.getInt("scorePerFetch_normal");
        this.capacityInFetches_normal = fileConfig.getInt("capacityInFetches_normal");

        this.clicksPerFetch_special = fileConfig.getInt("clicksPerFetch_special");
        this.scorePerFetch_special = fileConfig.getInt("scorePerFetch_special");
        this.capacityInFetches_special = fileConfig.getInt("capacityInFetches_special");
        this.promptInterval_special = fileConfig.getInt("promptInterval_special");

        this.lootProbability_club = (float) fileConfig.getDouble("lootProbability_club");
        this.lootProbability_booster = (float) fileConfig.getDouble("lootProbability_booster");

        this.stealerScore = fileConfig.getInt("stealerScore");

        this.giftSpawnerLocations = Util.castList(fileConfig.getList("giftSpawnerLocations"), Location.class);

        GiftHunting.plugin.logger.log(Level.INFO, "config loaded");
    }

    // save cfg to file
    public void save()
    {
        if(this.gameSpawn != null)
            fileConfig.set("gameSpawn", this.gameSpawn);
        if(this.giftSpawnerLocations != null)
            fileConfig.set("giftSpawnerLocations", this.giftSpawnerLocations);

        try {
            File file = new File(plugin.getDataFolder(), "config.yml");
            fileConfig.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        GiftHunting.plugin.logger.log(Level.INFO, "config saved");
    }
}
