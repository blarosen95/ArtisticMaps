package com.github.blarosen95.ArtisticMaps.Preview;

import java.util.UUID;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;

import com.github.Fupery.InvMenu.Utils.SoundCompat;

import com.github.blarosen95.ArtisticMaps.IO.MapArt;

public class ArtPreview extends TimedPreview {

    private final ItemStack preview;

    public ArtPreview(MapArt artwork) {
        this.preview = artwork.getMapItem();
    }

    @Override
    public boolean start(Player player) {
        super.start(player);
        if (player.getItemInHand() == null) return false; //TODO: after next test deployment, replace deprecated call with ...inMainHand()...
        player.setItemInHand(preview); //TODO: same idea as above
        return true;
    }

    @Override
    public boolean end(Player player) {
        super.end(player);
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, -2);
        if (player.getItemOnCursor().equals(preview)) player.setItemOnCursor(null);
        player.getInventory().removeItem(preview);
        return true;
    }

    @Override
    public boolean isEventAllowed(UUID player, Event event) {
        return false;
    }
}
