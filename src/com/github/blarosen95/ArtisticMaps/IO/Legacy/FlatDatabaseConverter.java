package com.github.blarosen95.ArtisticMaps.IO.Legacy;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.Database.Map;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.UUID;

class FlatDatabaseConverter {
    private final JavaPlugin plugin;

    public FlatDatabaseConverter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    boolean convertDatabase() {
        String dbFileName = "mapList.yml";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        if (!databaseFile.exists()) return false;
        plugin.getLogger().info("Old 'mapList.yml' database found! Converting to new format...");
        plugin.getLogger().info("(This may take a bit, but only needs to be run once)");
        ArtList artworks = readArtworks(databaseFile);

        if (!artworks.isEmpty()) {
            ArtisticMaps.getScheduler().ASYNC.run(artworks::addArtworks);
        }
        //TODO: change the ".off" to ".bak" since it's only remaining as a backup
        File disabledDatabaseFile = new File(plugin.getDataFolder(), dbFileName + ".off");
        if (!databaseFile.renameTo(disabledDatabaseFile)) {
            plugin.getLogger().info("Error disabling mapList.yml! Delete this file manually.");
            return false;
        }
        plugin.getLogger().info(String.format("Conversion completed! '%s' artworks converted. mapList.yml has been disabled.", artworks.getArtworks().size()));
        return true;
    }

    private ArtList readArtworks(File databaseFile) {
        ArtList artList = new ArtList();
        FileConfiguration database = YamlConfiguration.loadConfiguration(databaseFile);
        ConfigurationSection artworks = database.getConfigurationSection("artworks");

        if (artworks == null) return artList;

        for (String title : artworks.getKeys(false)) {
            ConfigurationSection map = artworks.getConfigurationSection(title);
            if (map != null) {
                short mapIDValue = (short) map.getInt("mapID");
                OfflinePlayer player = (map.contains("artist")) ?
                        Bukkit.getOfflinePlayer(UUID.fromString(map.getString("artist"))) : null;
                String date = map.getString("date");
                MapView mapView = Bukkit.getMap(mapIDValue);
                if (mapView == null) {
                    plugin.getLogger().info(String.format(" Ignoring '%s' (failed to access map data) ...", title));
                    continue;
                }
                if (player == null || !player.hasPlayedBefore()) {
                    plugin.getLogger().info(String.format(" Ignoring '%s' (artist UUID is invalid) ...", title));
                    continue;
                }
                MapArt artwork = new MapArt(mapIDValue, title, player, date);
                if (ArtisticMaps.getArtDatabase().containsArtwork(artwork, true)) {
                    plugin.getLogger().info(String.format("Ignoring '%s' (already exists in database) ...", title));
                } else {
                    plugin.getLogger().info(String.format(" Converting '%s' ...", title));
                    artList.getArtworks().add(artwork);
                    artList.getMaps().add(new Map(mapView).compress());
                }
            }
        }
        return artList;
    }
}