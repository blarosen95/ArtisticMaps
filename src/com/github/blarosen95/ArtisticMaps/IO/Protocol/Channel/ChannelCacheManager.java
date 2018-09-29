package com.github.blarosen95.ArtisticMaps.IO.Protocol.Channel;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import io.netty.channel.Channel;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChannelCacheManager {
    private final ConcurrentHashMap<UUID, CacheablePlayerChannel> cacheMap = new ConcurrentHashMap<>();
    private final Object cleanupLock = new Object();
    private CleanupThread cleanup = null;

    private CacheablePlayerChannel cacheChannel(UUID player) {
        CacheablePlayerChannel playerChannel = new CacheablePlayerChannel(player, 30000);
        cacheMap.put(player, playerChannel);
        synchronized (cleanupLock) {
            if (cleanup == null) cleanup = new CleanupThread();
        }
        return playerChannel;
    }

    public Channel getChannel(UUID player) {
        CacheablePlayerChannel cachedChannel = cacheMap.get(player);
        if (cachedChannel == null) {
            cachedChannel = cacheChannel(player);
        }
        if (cachedChannel.isExpired()) {
            cacheMap.remove(player);
            cacheChannel(player);
        }
        return cachedChannel.getChannel();
    }

    private class CleanupThread extends BukkitRunnable {
        CleanupThread() {
            ArtisticMaps.getScheduler().getTaskHandler(this).runTimer(true, 300, 300);
        }

        @Override
        public void run() {
            if (cacheMap.isEmpty()) {
                synchronized (cleanupLock) {
                    cancel();
                    cleanup = null;
                }
            }
            for (UUID key : cacheMap.keySet()) {
                CacheablePlayerChannel value = cacheMap.get(key);
                if (value.isExpired()) {
                    cacheMap.remove(key);
                }
            }
        }
    }
}
