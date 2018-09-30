package com.github.blarosen95.ArtisticMaps.Compatibility;

import com.github.blarosen95.ArtisticMaps.Easel.EaselEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

interface RegionHandler extends CompatibilityHandler {

    boolean checkBuildAllowed(Player player, Location location);

    boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click);
}
