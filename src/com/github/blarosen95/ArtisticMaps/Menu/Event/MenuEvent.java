package com.github.blarosen95.ArtisticMaps.Menu.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;

public abstract class MenuEvent {
    private final Player player;
    private final Inventory inventory;

    public MenuEvent(Player player, Inventory inventory) {
        this.player = player;
        this.inventory = inventory;
    }

    public Player getPlayer() {
        return player;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public static class MenuCloseEvent extends MenuEvent {
        private final MenuCloseReason reason;

        public MenuCloseEvent(Player player, Inventory inventory, MenuCloseReason reason) {
            super(player, inventory);
            this.reason = reason;
        }

        public MenuCloseEvent(Player player, MenuCloseReason reason) {
            super(player, null);
            this.reason = reason;
        }

        public MenuCloseReason getReason() {
            return reason;
        }
    }

    public static class MenuInteractEvent extends MenuEvent {
        private final ClickType clickType;
        private final int slot;

        public MenuInteractEvent(Player player, Inventory inventory, int slot, ClickType clickType) {
            super(player, inventory);
            this.slot = slot;
            this.clickType = clickType;
        }

        public ClickType getClick() {
            return clickType;
        }

        public int getSlot() {
            return slot;
        }
    }
}