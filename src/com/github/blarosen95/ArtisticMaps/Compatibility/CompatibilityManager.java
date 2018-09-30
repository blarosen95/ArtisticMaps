package com.github.blarosen95.ArtisticMaps.Compatibility;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.blarosen95.ArtisticMaps.Easel.EaselEvent;

public class CompatibilityManager implements RegionHandler {
    private final List<RegionHandler> regionHandlers;
    private final ReflectionHandler reflectionHandler;

    public CompatibilityManager(JavaPlugin plugin) {
        regionHandlers = new ArrayList<>();
        loadRegionHandler(GriefPreventionCompat.class);

        reflectionHandler = loadReflectionHandler();
        if (!(reflectionHandler instanceof VanillaReflectionHandler))
            plugin.getLogger().info(String.format("%s reflection handler enabled.",
                    reflectionHandler.getClass().getSimpleName().replace("Compat", "")));
        for (RegionHandler regionHandler : regionHandlers) {
            plugin.getLogger().info(String.format("%s hooks enabled",
                    regionHandler.getClass().getSimpleName().replace("Compat", "")));
        }
    }

    public boolean isPluginLoaded(String pluginName) {
        JavaPlugin plugin = (JavaPlugin) Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null && plugin.isEnabled();
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        if (player.hasPermission("artisticmaps.admin")) return true; //TODO: replace this with an ignoringclaims?
        for (RegionHandler regionHandler : regionHandlers) {
            if (!regionHandler.checkBuildAllowed(player, location)) return false;
        }
        return true;
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        if (checkBuildAllowed(player, entity.getLocation())) return true; //If you can build it you can dream it!
        for (RegionHandler regionHandler : regionHandlers) {
            if (!regionHandler.checkInteractAllowed(player, entity, click)) return false;
        }
        return true;
    }

    public ReflectionHandler getReflectionHandler() {
        return reflectionHandler;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }

    private ReflectionHandler loadReflectionHandler() {
        ReflectionHandler iDisguiseHandler = new iDisguiseCompat();
        if (iDisguiseHandler.isLoaded()) return iDisguiseHandler;
        return new VanillaReflectionHandler();
    }

    private void loadRegionHandler(Class<? extends RegionHandler> handlerClass) {
        try {
            RegionHandler handler = handlerClass.newInstance();
            if (handler.isLoaded()) regionHandlers.add(handler);
        } catch (Exception | NoClassDefFoundError ignored) {
        }
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Plugin compatibility hooks: ");
        for (RegionHandler regionHandler : regionHandlers) {
            string.append(regionHandler.getClass().getSimpleName()).append(" [LOADED:").append(regionHandler.isLoaded()).append("], ");
        }
        string.append("Reflections Handler: ").append(reflectionHandler.getClass());
        return string.toString();
    }
}
