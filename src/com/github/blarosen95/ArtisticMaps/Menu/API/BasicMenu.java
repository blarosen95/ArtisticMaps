package com.github.blarosen95.ArtisticMaps.Menu.API;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

import com.github.Fupery.InvMenu.Utils.MenuType;

import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuCloseReason;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.CacheableMenu;

public abstract class BasicMenu extends CacheableMenu {
    protected BasicMenu(String heading, InventoryType type) {
        super(heading, type);
    }

    protected BasicMenu(String heading, MenuType type) {
        super(heading, type);
    }

    @Override
    public void onMenuOpenEvent(Player viewer) {
    }

    @Override
    public void onMenuRefreshEvent(Player viewer) {
    }

    @Override
    public void onMenuClickEvent(Player viewer, int slot, ClickType click) {
    }

    @Override
    public void onMenuCloseEvent(Player viewer, MenuCloseReason reason) {
    }
}
