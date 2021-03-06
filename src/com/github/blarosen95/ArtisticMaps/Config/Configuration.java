package com.github.blarosen95.ArtisticMaps.Config;

import org.bukkit.configuration.file.FileConfiguration;
import com.github.blarosen95.ArtisticMaps.Compatibility.CompatibilityManager;
import com.github.blarosen95.ArtisticMaps.ArtisticMaps;

public class Configuration {
    public final String LANGUAGE;
    public final String WORLD;
    public final int ARTWORK_AUTO_SAVE;
    public final boolean SWEAR_FILTER;
    public final boolean DISABLE_ACTION_BAR;
    public final boolean CUSTOM_RECIPES;
    public final boolean FORCE_ART_KIT;
    public final boolean HIDE_PREFIX;
    public final boolean FORCE_GUI;
    public final boolean HEAD_PREFETCH;
    public final long HEAD_PREFETCH_DELAY;

    public Configuration(ArtisticMaps plugin, CompatibilityManager manager) {
        FileConfiguration configuration = plugin.getConfig();
        this.LANGUAGE = configuration.getString("language");
        String world = configuration.getString("world");
        this.WORLD = world != null ? world : "world";
        int autoSave = configuration.getInt("artworkAutoSave");
        this.ARTWORK_AUTO_SAVE = autoSave >= 30 ? autoSave : 300;
        this.SWEAR_FILTER = configuration.getBoolean("swearFilter");
        this.DISABLE_ACTION_BAR = configuration.getBoolean("disableActionBar");
        this.CUSTOM_RECIPES = configuration.getBoolean("customRecipes");
        this.FORCE_ART_KIT = configuration.getBoolean("forceArtKit");
        this.HIDE_PREFIX = configuration.getBoolean("hidePrefix");
        this.FORCE_GUI = configuration.getBoolean("guiOnly", false);
        this.HEAD_PREFETCH = configuration.getBoolean("headPrefetch", true);
        this.HEAD_PREFETCH_DELAY = configuration.getLong("headPrefetchDelay", 0L);
    }
}
