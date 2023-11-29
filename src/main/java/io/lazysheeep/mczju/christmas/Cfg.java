package io.lazysheeep.mczju.christmas;

import io.lazysheeep.mczju.christmas.command.Chris;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Cfg
{
    public int giftNumber;
    public List<Location> giftSpawnerLocations;


    private final FileConfiguration fileConfig;
    private final Christmas christmas;

    public Cfg(Christmas christmas)
    {
        christmas.saveDefaultConfig();
        this.fileConfig = christmas.getConfig();
        this.christmas = christmas;
    }

    public void load()
    {
        // load cfg
        giftNumber = fileConfig.getInt("gift_number");
        giftSpawnerLocations = (List<Location>)fileConfig.getList("locations");
    }

    public void save()
    {
        // save config to cfg
        fileConfig.set("gift_number", giftNumber);
        fileConfig.set("locations", giftSpawnerLocations);
        // save cfg to file
        try {
            File file = new File(christmas.getDataFolder(), "config.yml");
            fileConfig.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
