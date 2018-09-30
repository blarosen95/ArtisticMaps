package com.github.blarosen95.ArtisticMaps.IO.Protocol.Channel;

import io.netty.channel.Channel;
import com.github.blarosen95.ArtisticMaps.Utils.Reflection;
import org.bukkit.Bukkit;

import java.util.UUID;

class CacheablePlayerChannel {

    private final UUID player;
    private final long expiryTime;
    private final Channel channel;

    CacheablePlayerChannel(UUID player) {
        this.player = player;
        expiryTime = ((long) 30000 == -1) ? -1 : System.currentTimeMillis() + (long) 30000;
        channel = Reflection.getPlayerChannel(Bukkit.getPlayer(player));
    }

    public void sendPacket(Object packet) {
        channel.pipeline().writeAndFlush(packet);
    }

    boolean isExpired() {
        return channel == null || !channel.isOpen() || (expiryTime != -1 && expiryTime >= System.currentTimeMillis());
    }

    Channel getChannel() {
        return channel;
    }

    public UUID getPlayer() {
        return player;
    }
}
