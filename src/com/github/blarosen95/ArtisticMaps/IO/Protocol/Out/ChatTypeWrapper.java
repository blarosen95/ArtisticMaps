package com.github.blarosen95.ArtisticMaps.IO.Protocol.Out;

import static com.github.blarosen95.ArtisticMaps.Utils.VersionHandler.BukkitVersion.v1_12;

import java.lang.reflect.Field;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.ErrorLogger;
import com.github.blarosen95.ArtisticMaps.Utils.Reflection;

class ChatTypeWrapper {
    private final boolean legacy;
    private Class<?> chatTypeClass;
    private Object chatType;

    public ChatTypeWrapper() {
        legacy = ArtisticMaps.getBukkitVersion().getVersion().isLessThan(v1_12);
        if (legacy) {
            chatTypeClass = byte.class;
            chatType = (byte) 2;
        } else {
            String chatTypeClassName = Reflection.NMS + ".ChatMessageType";
            try {
                chatTypeClass = Class.forName(chatTypeClassName);
                Field chatTypeField = chatTypeClass.getDeclaredField("GAME_INFO");
                chatType = chatTypeField.get(null);
            } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException e) {
                ErrorLogger.log(e);
            }
        }
    }

    public Class<?> getChatTypeClass() {
        return chatTypeClass;
    }

    public Object getChatType() {
        return chatType;
    }
}