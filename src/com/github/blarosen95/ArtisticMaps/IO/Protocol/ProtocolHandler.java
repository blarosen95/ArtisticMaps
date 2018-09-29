package com.github.blarosen95.ArtisticMaps.IO.Protocol;

import com.comphenix.protocol.ProtocolLibrary;
import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.In.GenericPacketReceiver;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.In.PacketReceiver;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.In.ProtocolLibReceiver;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.Out.GenericPacketSender;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.Out.PacketSender;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.Out.ProtocolLibSender;
import org.bukkit.Bukkit;

public class ProtocolHandler {

    public final PacketReceiver PACKET_RECEIVER;
    public final PacketSender PACKET_SENDER;

    public ProtocolHandler() {
        boolean useProtocolLib = ArtisticMaps.getCompatManager().isPluginLoaded("ProtocolLib");
        try {
            ProtocolLibrary.getProtocolManager();
        } catch (Exception | NoClassDefFoundError e) {
            useProtocolLib = false;
        }
        if (useProtocolLib) {
            PACKET_RECEIVER = new ProtocolLibReceiver();
            PACKET_SENDER = new ProtocolLibSender();
            Bukkit.getLogger().info("[ArtisticMaps] Hooked into ProtocolLib.");
        } else {
            PACKET_RECEIVER = new GenericPacketReceiver();
            PACKET_SENDER = new GenericPacketSender();
        }
    }
}