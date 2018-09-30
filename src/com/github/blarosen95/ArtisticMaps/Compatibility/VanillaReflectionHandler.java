package com.github.blarosen95.ArtisticMaps.Compatibility;

import io.netty.channel.Channel;
import com.github.blarosen95.ArtisticMaps.Utils.Reflection;
import org.bukkit.entity.Player;

public class VanillaReflectionHandler implements ReflectionHandler {
    @Override
    public Channel getPlayerChannel(Player player) throws ReflectiveOperationException {
        Object nmsPlayer, playerConnection, networkManager;
        Channel channel;
        nmsPlayer = Reflection.invokeMethod(player, "getHandle");
        playerConnection = Reflection.getField(nmsPlayer, "playerConnection");
        networkManager = Reflection.getField(playerConnection, "networkManager");
        channel = (Channel) Reflection.getField(networkManager, "channel");
        return channel;
    }

    @Override
    public boolean isLoaded() {
        return true;
    }
}
