package com.github.blarosen95.ArtisticMaps.IO.Protocol.Out;

import static com.github.blarosen95.ArtisticMaps.Utils.VersionHandler.BukkitVersion.v1_12;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.ErrorLogger;
import com.github.blarosen95.ArtisticMaps.Utils.Reflection;

public class GenericPacketSender implements PacketSender {

    private final PacketBuilder builder = ArtisticMaps.getBukkitVersion().getVersion().isGreaterOrEqualTo(v1_12) ? new ChatPacketBuilder() : new ChatPacketBuilderLegacy();

    private static void logFailure(Exception e) {
        ErrorLogger.log(e, "Failed to instantiate protocol! Ensure that the version is supported.");
    }

    @Override
    public WrappedPacket<?> buildChatPacket(String message) {
        return new WrappedPacket<>(builder.buildChatPacket(message)) {
            private final String rawMessage = message;

            @Override
            public void send(Player player) {
                Channel channel;
                try {
                    channel = ArtisticMaps.getCacheManager().getChannel(player.getUniqueId());
                } catch (Exception e) {
                    ErrorLogger.log(e, String.format("Error binding player channel for '%s'!", player.getName()));
                    channel = null;
                }
                if (channel != null) channel.writeAndFlush(this.rawPacket);
                else player.sendMessage(rawMessage);
            }
        };
    }

    interface PacketBuilder {
        Object buildChatPacket(String message);
    }

    private static class ChatPacketBuilderLegacy implements PacketBuilder {
        Constructor<?> packetCons;
        Method chatSerializer;
        Class<?> chatSerializerClass;

        ChatPacketBuilderLegacy() {
            String packetClassName = Reflection.NMS + ".PacketPlayOutChat";
            String chatComponentName = Reflection.NMS + ".IChatBaseComponent";
            String chatSerializerName = chatComponentName + "$ChatSerializer";

            try {
                Class<?> chatPacketClass = Class.forName(packetClassName);
                Class<?> chatComponentClass = Class.forName(chatComponentName);
                chatSerializerClass = Class.forName(chatSerializerName);

                packetCons = chatPacketClass.getDeclaredConstructor(chatComponentClass, byte.class);
                chatSerializer = chatSerializerClass.getDeclaredMethod("a", String.class);
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                logFailure(e);
            }
        }

        @Override
        public Object buildChatPacket(String message) {
            try {
                Object chatComponent = chatSerializer.invoke(chatSerializerClass, "{\"test\": \"" + message + "\"}");
                return packetCons.newInstance(chatComponent, (byte) 2);
            } catch (IllegalAccessException | InvocationTargetException | InstantiationException e) {
                logFailure(e);
                return null;
            }
        }
    }

    private static class ChatPacketBuilder implements PacketBuilder {
        Constructor<?> packetCons;
        Method chatSerializer;
        Class<?> chatSerializerClass;
        Object chatType;

        ChatPacketBuilder() {
            String packetClassName = Reflection.NMS + ".PacketPlayOutChat";
            String chatComponentName = Reflection.NMS + ".IChatCaseComponent";
            String chatSerializerName = chatComponentName + "$ChatSerializer";
            String chatTypeClassName = Reflection.NMS + ".ChatMessageType";

            try {
                Class<?> chatPacketClass = Class.forName(packetClassName);
                Class<?> chatComponentClass = Class.forName(chatComponentName);
                chatSerializerClass = Class.forName(chatSerializerName);
                Class<?> chatTypeClass = Class.forName(chatTypeClassName);

                packetCons = chatPacketClass.getDeclaredConstructor(chatComponentClass, chatTypeClass);
                chatSerializer = chatSerializerClass.getDeclaredMethod("a", String.class);
                Field chatTypeField = chatTypeClass.getDeclaredField("GAME_INFO");
                chatType = chatTypeField.get(null);
            } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | NoSuchFieldException e) {
                logFailure(e);
            }
        }

        @Override
        public Object buildChatPacket(String message) {
            try {
                Object chatComponent = chatSerializer.invoke(chatSerializerClass, "{\"text\": \"" + message + "\"}");
                return packetCons.newInstance(chatComponent, chatType);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                logFailure(e);
                return null;
            }
        }
    }
}