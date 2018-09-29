package com.github.blarosen95.ArtisticMaps.Event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public class PlayerOpenMenuEvent extends PlayerEvent {
    public static final HandlerList handlers = new HandlerList();

    public PlayerOpenMenuEvent(Player who) {
        super(who);
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}
