package com.github.blarosen95.ArtisticMaps.Menu.API;

import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuCloseReason;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

public interface MenuTemplate {

    /**
     * Called after menus is sent to player
     *
     * @param viewer the player viewing the menu
     */
    void onMenuOpenEvent(Player viewer);

    /**
     * Called when menu is updated
     *
     * @param viewer the player viewing the menu
     */
    void onMenuRefreshEvent(Player viewer);

    /**
     * Called when player clicks the menu
     *
     * @param viewer the player viewing the menu
     */
    void onMenuClickEvent(Player viewer, int slot, ClickType click);

    /**
     * Called when player closes the menu
     *
     * @param viewer the player viewing the menu
     */
    void onMenuCloseEvent(Player viewer, MenuCloseReason reason);

    /**
     * @return A list of ItemStack buttons that fill the menu
     */
    Button[] getButtons();
}