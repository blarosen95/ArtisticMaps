package com.github.blarosen95.ArtisticMaps.Compatibility;

import io.netty.channel.Channel;
import org.bukkit.entity.Player;

public interface ReflectionHandler extends CompatibilityHandler {
    Channel getPlayerChannel(Player player) throws ReflectiveOperationException;
}
