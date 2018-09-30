package com.github.blarosen95.ArtisticMaps.IO.Protocol.Out;

import org.bukkit.entity.Player;

public abstract class WrappedPacket<T> {

    protected final T rawPacket;

    public WrappedPacket(T packet) {
        this.rawPacket = packet;
    }

    public abstract void send(Player player);
}
