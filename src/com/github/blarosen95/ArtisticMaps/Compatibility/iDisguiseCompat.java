package com.github.blarosen95.ArtisticMaps.Compatibility;

import io.netty.channel.Channel;
import com.github.blarosen95.ArtisticMaps.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class iDisguiseCompat implements ReflectionHandler {

    private final boolean loaded;

    public iDisguiseCompat() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("iDisguise");
        loaded = (plugin != null && plugin.isEnabled());
    }

    @Override
    public Channel getPlayerChannel(Player player) throws ReflectiveOperationException {
        Object nmsPlayer, playerConnection, networkManager;
        nmsPlayer = Reflection.invokeMethod(player, "getHandle");
        playerConnection = Reflection.getField(nmsPlayer, "playerConnection");
        networkManager = Reflection.getSuperField(playerConnection, "networkManager");
        return (Channel) Reflection.getField(networkManager, "channel");
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
