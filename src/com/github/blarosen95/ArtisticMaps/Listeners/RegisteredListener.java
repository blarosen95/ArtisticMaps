package com.github.blarosen95.ArtisticMaps.Listeners;

import org.bukkit.event.Listener;

interface RegisteredListener extends Listener {
    void unregister();
}
