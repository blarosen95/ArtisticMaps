package com.github.blarosen95.ArtisticMaps.Menu.API;

import com.github.blarosen95.ArtisticMaps.Menu.Handler.CacheableMenu;
import org.bukkit.entity.Player;

public interface ChildMenu extends MenuTemplate {
    CacheableMenu getParent(Player viewer);
}
