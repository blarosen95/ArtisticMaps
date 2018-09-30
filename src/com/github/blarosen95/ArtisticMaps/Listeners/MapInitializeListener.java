package com.github.blarosen95.ArtisticMaps.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.MapInitializeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.Database.Map;

public class MapInitializeListener implements RegisteredListener {

    @EventHandler
    public void onMapInitialize(MapInitializeEvent event) {
        short mapId = event.getMap().getId();
        Bukkit.getLogger().info("Map initialize: " + mapId);
        ArtisticMaps.getScheduler().ASYNC.run(() -> {
            if (!ArtisticMaps.getArtDatabase().getMapTable().containsMap(mapId)) return;
            Bukkit.getLogger().info("Contains map!");

            ArtisticMaps.getScheduler().SYNC.run(() -> {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Bukkit.getLogger().info("   Player online: " + player.getDisplayName());

                    ItemStack item = player.getInventory().getItemInMainHand();
                    if (item.getType() == Material.FILLED_MAP && mapId == event.getMap().getId()) {
                        Bukkit.getLogger().info("   ItemMatches!");
                        MapMeta meta = (MapMeta) item.getItemMeta();
                        meta.setMapId(Bukkit.createMap(player.getWorld()).getId());
                        item.setItemMeta(meta);
                        player.getInventory().setItemInMainHand(item);
                    }
                }
            });
            Map map = new Map(mapId);
            ArtisticMaps.getArtDatabase().restoreMap(map);
        });
    }

    @Override
    public void unregister() {
        MapInitializeEvent.getHandlerList().unregister(this);
    }
}
