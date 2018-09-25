package com.github.blarosen95.ArtisticMaps.Compatibility;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public interface RegionHandler extends CompatibilityHandler {

    boolean checkBuildAllowed(Player player, Location location);

    boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click);
}
