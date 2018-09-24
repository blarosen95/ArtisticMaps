package com.github.blarosen95.ArtisticMaps.IO.Protocol.In;

import com.github.blarosen95.ArtisticMaps.Utils.Reflection;
import com.google.common.collect.MapMaker;
import io.netty.channel.Channel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

public class GenericPacketReceiver extends PacketReceiver {

    private final Map<UUID, Channel> channelLookup = new MapMaker().weakValues().makeMap();

    private final String handlerName = "ArtisticMapsHandler";

    @Override
    public boolean injectPlayer(Player player) {
        Channel channel = getChannel(player);
        if (channel == null) {
            return false;
        }
        PacketHandler handler;
        try {
            handler = (PacketHandler) channel.pipeline().get(handlerName);

            if (handler == null) {
                handler = new PacketHandler();
                channel.pipeline().addBefore("packet_handler", handlerName, handler);
            }
        } catch (IllegalArgumentException | ClassCastException e) {
            handler = (PacketHandler) channel.pipeline().get(handlerName);
        }
        handler.player = player;
        return true;
    }

    @Override
    public void unInjectPlayer(Player player) {

        try {
            final Channel channel = getChannel(player);

            if (channel.pipeline().get(handlerName) != null) {

                channel.eventLoop().execute(() -> {
                    if (channel.pipeline().get(handlerName) != null) {
                        channel.pipeline().remove(handlerName);
                    }
                });
            }
        } catch (Exception e) {
            ErrorLogger.log(e, "Error unbinding player channel!");
        }
        channelLookup.remove(player.getUniqueId());
    }

    @Override
    public void close() {

        if (channelLookup != null && channelLookup.size() > 0) {

            for (UUID player : channelLookup.keySet()) {
                unInjectPlayer(Bukkit.getPlayer(player));
            }
            channelLookup.clear();
        }
    }

    private Channel getChannel(Player player) {
        Channel channel = channelLookup.get(player.getUniqueId());

        if (channel == null) {
            channel = Reflection
        }
    }

}