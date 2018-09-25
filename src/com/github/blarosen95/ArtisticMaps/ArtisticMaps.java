package com.github.blarosen95.ArtisticMaps;

import com.github.blarosen95.ArtisticMaps.IO.Protocol.ProtocolHandler;
import com.github.blarosen95.ArtisticMaps.Painting.ArtistHandler;
import com.github.blarosen95.ArtisticMaps.Recipe.CanvasItem;
import com.github.blarosen95.ArtisticMaps.Recipe.EaselItem;
import com.github.blarosen95.ArtisticMaps.Recipe.PaintBrushItem;
import com.github.blarosen95.ArtisticMaps.Recipe.PaintBuckets.PaintBucketItem;

import com.github.blarosen95.ArtisticMaps.Utils.Scheduler;
import com.github.blarosen95.ArtisticMaps.Utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.Reader;
import java.lang.ref.SoftReference;

public class ArtisticMaps extends JavaPlugin {

    private static SoftReference<ArtisticMaps> pluginInstance = null;
    //private MenuHandler menuHandler;
    private ArtistHandler artistHandler;
    private VersionHandler bukkitVersion;
    private Scheduler scheduler;
    //private Database database;


    private ProtocolHandler protocolHandler;



    public void onEnable() {
        pluginInstance = new SoftReference<>(this);

        scheduler = new Scheduler(this);

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nArtisticMaps has been enabled\n\n");
        getServer().getPluginManager().registerEvents(new EventsClass(), this);
        loadConfig();

        EaselItem easelItem = new EaselItem();
        easelItem.customRecipe();
        CanvasItem canvasItem = new CanvasItem();
        canvasItem.customRecipe();
        PaintBrushItem paintBrushItem = new PaintBrushItem();
        paintBrushItem.customRecipe();

        PaintBucketItem paintBucketItem = new PaintBucketItem();
        paintBucketItem.customRecipe();
    }

    public void onDisable() {
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nArtisticMaps has been disabled\n\n");
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }

    public static ArtisticMaps instance() {
        if (pluginInstance == null || pluginInstance.get() == null) {
            pluginInstance = new SoftReference<>((ArtisticMaps) Bukkit.getPluginManager().getPlugin("ArtisticMaps"));
        }
        return pluginInstance.get();
    }

    public static Scheduler getScheduler() {
        return instance().scheduler;
    }

    public ArtistHandler getArtistHandler() {
        return instance().artistHandler;
    }

    public ProtocolHandler getProtocolHandler() {
        return instance().protocolHandler;
    }

    public VersionHandler getBukkitVersion() {
        return instance().bukkitVersion;
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }

}
