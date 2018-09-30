package com.github.blarosen95.ArtisticMaps.IO;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class YamlReader {
    private final ArtisticMaps plugin;
    private final String fileName;

    public YamlReader(ArtisticMaps plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
    }

    public YamlReader(String fileName) {
        plugin = ArtisticMaps.instance();
        this.fileName = fileName;
    }

    public FileConfiguration readFromResources() {
        return YamlConfiguration.loadConfiguration(plugin.getTextResourceFile(fileName));
    }

    private FileConfiguration readFromDataFolder() {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!plugin.getDataFolder().exists() || !file.exists()) return null;
        return YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration tryDataFolder() {
        FileConfiguration config = readFromDataFolder();
        if (config != null) return config;
        File file = new File(plugin.getDataFolder(), fileName);
        if (!ArtisticMaps.instance().writeResource(fileName, file)) return readFromResources();
        else return YamlConfiguration.loadConfiguration(file);
    }
}
