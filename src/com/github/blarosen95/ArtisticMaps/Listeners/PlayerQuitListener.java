package com.github.blarosen95.ArtisticMaps.Listeners;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;

//TODO: (MAJOR) extract method(s) for the two repeating if statements in each EventHandler
class PlayerQuitListener implements RegisteredListener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            ArtisticMaps.getArtistHandler().getCurrentSession(player).removeKit(player);
            ArtisticMaps.getArtistHandler().removePlayer(player);
        }
        if (ArtisticMaps.getPreviewManager().isPreviewing(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.FILLED_MAP) {
                ArtisticMaps.getPreviewManager().endPreview(player);
            }
        }
    }

    @EventHandler
    public void onPlayerKick(PlayerKickEvent event) {
        Player player = event.getPlayer();

        if (ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            ArtisticMaps.getArtistHandler().getCurrentSession(player).removeKit(player);
            ArtisticMaps.getArtistHandler().removePlayer(player);
        }
        if (ArtisticMaps.getPreviewManager().isPreviewing(player)) {
            if (event.getPlayer().getItemInHand().getType() == Material.FILLED_MAP) {
                ArtisticMaps.getPreviewManager().endPreview(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = event.getEntity();
        if (ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            ArtisticMaps.getArtistHandler().removePlayer(player);
        }
        ArtisticMaps.getPreviewManager().endPreview(player);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntity().getType() != EntityType.PLAYER) {
            return;
        }
        Player player = (Player) event.getEntity(); //TODO: NOTE: original author had some dumb redundant class.cast() call here
        if (ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            ArtisticMaps.getArtistHandler().getCurrentSession(player).removeKit(player);
            ArtisticMaps.getArtistHandler().removePlayer(player);
        }
        if (ArtisticMaps.getPreviewManager().isPreviewing(player)) {
            if (player.getItemInHand().getType() == Material.FILLED_MAP) {
                ArtisticMaps.getPreviewManager().endPreview(player);
            }
        }
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (ArtisticMaps.getArtistHandler().containsPlayer(event.getPlayer())) {
            if (event.getPlayer().isInsideVehicle()) {
                ArtisticMaps.getArtistHandler().getCurrentSession(event.getPlayer()).removeKit(event.getPlayer());
                ArtisticMaps.getArtistHandler().removePlayer(event.getPlayer());
            }
        }
        if (ArtisticMaps.getPreviewManager().isPreviewing(event.getPlayer())) {
            if (event.getPlayer().getItemInHand().getType() == Material.FILLED_MAP) {
                ArtisticMaps.getPreviewManager().endPreview(event.getPlayer());
            }
        }
    }

    @Override
    public void unregister() {
        PlayerQuitEvent.getHandlerList().unregister(this);
        PlayerDeathEvent.getHandlerList().unregister(this);
        PlayerTeleportEvent.getHandlerList().unregister(this);
    }
}
