package com.github.blarosen95.ArtisticMaps.IO.Protocol.In;

import org.bukkit.entity.Player;

public abstract class PacketReceiver {

    public boolean injectPlayer(Player player) {
        return true;
    }

    public void uninjectPlayer(Player player) {
    }

    public abstract void close();

    boolean onPacketPlayIn(ArtistHandler handler, Player player, ArtistPacket packet) {
        return handler.handlePacket(player, packet);
    }
}
