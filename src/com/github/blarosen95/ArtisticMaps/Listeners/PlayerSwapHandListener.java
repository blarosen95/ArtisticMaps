package com.github.blarosen95.ArtisticMaps.Listeners;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class PlayerSwapHandListener implements RegisteredListener {
    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        if (ArtisticMaps.getPreviewManager().endPreview(event.getPlayer())) event.setCancelled(true);
    }

    @Override
    public void unregister() {
        PlayerSwapHandItemsEvent.getHandlerList().unregister(this);
    }
}
