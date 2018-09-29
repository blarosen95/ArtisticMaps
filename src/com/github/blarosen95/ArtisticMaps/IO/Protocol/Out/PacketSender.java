package com.github.blarosen95.ArtisticMaps.IO.Protocol.Out;

public interface PacketSender {
    WrappedPacket<?> buildChatPacket(String message);
}
