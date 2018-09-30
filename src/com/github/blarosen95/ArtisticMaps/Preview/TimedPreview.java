package com.github.blarosen95.ArtisticMaps.Preview;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

abstract class TimedPreview implements Preview {
    private Timeout timeout;

    @Override
    public boolean start(Player player) {
        ArtisticMaps.getScheduler().getTaskHandler(timeout = new Timeout(player.getUniqueId())).runLater(false, 300);
        return true;
    }

    @Override
    public boolean end(Player player) {
        timeout.cancel();
        return true;
    }

    private class Timeout extends BukkitRunnable {
        private final UUID player;

        Timeout(UUID player) {
            this.player = player;
        }

        @Override
        public void run() {
            ArtisticMaps.getPreviewManager().endPreview(player);
        }
    }
}
