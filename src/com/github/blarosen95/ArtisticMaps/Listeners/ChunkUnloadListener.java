package com.github.blarosen95.ArtisticMaps.Listeners;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.world.ChunkUnloadEvent;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Easel.Easel;
import com.github.blarosen95.ArtisticMaps.Easel.EaselMap;
import com.github.blarosen95.ArtisticMaps.Utils.ChunkLocation;

public class ChunkUnloadListener implements RegisteredListener {

    @EventHandler
    public void onChunkUnload(final ChunkUnloadEvent event) {
        EaselMap easels = ArtisticMaps.getEasels();
        if (!easels.isEmpty() && event.getChunk() != null) {
            ChunkLocation chunk = new ChunkLocation(event.getChunk());
            ArtisticMaps.getScheduler().ASYNC.run(() -> {
                for (Location location : easels.keySet()) {
                    Easel easel = easels.get(location);
                    if (easel != null && easel.getChunk() != null && easel.getChunk().equals(chunk)) {
                        easels.remove(location);
                    }
                }
            });
        }
    }

    @Override
    public void unregister() {
        ChunkUnloadEvent.getHandlerList().unregister(this);
    }
}
