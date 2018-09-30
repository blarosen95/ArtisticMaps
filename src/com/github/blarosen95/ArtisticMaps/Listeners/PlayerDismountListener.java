package com.github.blarosen95.ArtisticMaps.Listeners;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.spigotmc.event.entity.EntityDismountEvent;

public class PlayerDismountListener implements RegisteredListener {

    @EventHandler
    public void onPlayerDismount(EntityDismountEvent event) {

        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity();
        if (ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            ArtisticMaps.getArtistHandler().removePlayer(player);
        }
    }

    @Override
    public void unregister() {
        EntityDismountEvent.getHandlerList().unregister(this);
    }
}
