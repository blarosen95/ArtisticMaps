package com.github.blarosen95.ArtisticMaps.IO.Protocol.In;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.*;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.In.Packet.ArtistPacket;
import com.github.blarosen95.ArtisticMaps.Painting.ArtistHandler;
import org.bukkit.plugin.java.JavaPlugin;

public class ProtocolLibReceiver extends PacketReceiver {

    public ProtocolLibReceiver() {
        registerListeners(ArtisticMaps.instance());
    }

    private void registerListeners(JavaPlugin plugin) {
        PacketAdapter.AdapterParameteters options = new PacketAdapter.AdapterParameteters();
        options.plugin(plugin);
        options.optionAsync();
        options.connectionSide(ConnectionSide.CLIENT_SIDE);
        options.listenerPriority(ListenerPriority.HIGH);
        options.types(
                PacketType.Play.Client.ARM_ANIMATION,
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.USE_ENTITY
        );
        ProtocolLibrary.getProtocolManager().addPacketListener(new DefaultPacketAdapter(options));
    }

    private ArtistPacket getPacketType(PacketContainer packet) {
        if (packet.getType() == PacketType.Play.Client.LOOK) {
            float yaw = packet.getFloat().read(0);
            float pitch = packet.getFloat().read(1);
            return new ArtisticPacket.PacketLook(yaw, pitch);
        } else if (packet.getType() == PacketType.Play.Client.ARM_ANIMATION) {
            return new ArtisticPacket.PacketArmSwing();
        } else if (packet.getType() == PacketType.Play.Client.USE_ENTITY) {
            EnumWrappers.EntityUseAction action = packet.getEntityUseActions().read(0);
            if (action == EnumWrappers.EntityUseAction.ATTACK) {
                return new PacketInteract(InteractType.ATTACK);
            } else {
                return new PacketInteract(InteractType.INTERACT);
            }
        }
        return null;
    }

    @Override
    public void close() {
        ProtocolLibrary.getProtocolManager().removePacketListeners(ArtisticMaps.instance());
    }

    class DefaultPacketAdapter extends PacketAdapter {
        DefaultPacketAdapter(AdapterParameteters options) {
            super(options);
        }

        @Override
        public void onPacketReceiving(PacketEvent event) {
            ArtistHandler handler = ArtisticMaps.getArtistHandler();
            if (!handler.containsPlayer(event.getPlayer())) return;
            ArtistPacket packet = getPacketType(event.getPacket());
            if (packet == null) return;
            if (!onPacketPlayIn(handler, event.getPlayer(), packet)) event.setCancelled(true);
        }
    }
}
