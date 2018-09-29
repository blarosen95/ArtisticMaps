package com.github.blarosen95.ArtisticMaps.Menu.Event;

import com.github.blarosen95.ArtisticMaps.Menu.Handler.CacheableMenu;
import org.bukkit.entity.Player;

public interface MenuFactory {
    CacheableMenu get(Player viewer);
}
