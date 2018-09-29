package com.github.blarosen95.ArtisticMaps.IO.Protocol.Out;

import static com.github.blarosen95.ArtisticMaps.Utils.VersionHandler.BukkitVersion.v1_12;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.ErrorLogger;

public class ProtocolLibSender implements PacketSender {
    private PacketBuilder builder = ArtisticMaps.getBukkitVersion().getVersion().isGreaterOrEqualTo(v1_12) ? new ChatPacketBuilder() : new ChatPacketBuilderLegacy();

    @Override
    public WrappedPacket<?> buildChatPacket(String message) {
        return new WrappedPacket<PacketContainer>(builder.buildChatPacket(message)) {
            @Override
            public void send(Player player) {
                try {
                    ProtocolLibrary.getProtocolManager().sendServerPacket(player, this.rawPacket);
                } catch (InvocationTargetException e) {
                    ErrorLogger.log(e);
                }
            }
        };
    }

    interface PacketBuilder {
        PacketContainer buildChatPacket(String message);
    }

    private class ChatPacketBuilderLegacy implements PacketBuilder {
        @Override
        public PacketContainer buildChatPacket(String message) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
            packet.getBytes().write(0, (byte) 2);
            return packet;
        }
    }

    private class ChatPacketBuilder implements PacketBuilder {
        @Override
        public PacketContainer buildChatPacket(String message) {
            PacketContainer packet = ProtocolLibrary.getProtocolManager().createPacket(PacketType.Play.Server.CHAT);
            packet.getChatComponents().write(0, WrappedChatComponent.fromText(message));
            packet.getChatTypes().write(0, EnumWrappers.ChatType.GAME_INFO);
            return packet;
        }
    }
}
