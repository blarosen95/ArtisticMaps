package com.github.blarosen95.ArtisticMaps.Preview;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

interface Preview {

    boolean start(Player player);

    boolean end(Player player);

    boolean isEventAllowed(UUID player, Event event);
}
