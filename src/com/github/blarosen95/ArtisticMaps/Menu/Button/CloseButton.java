package com.github.blarosen95.ArtisticMaps.Menu.Button;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import com.github.Fupery.InvMenu.Utils.SoundCompat;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuCloseReason;

public class CloseButton extends Button {

    public CloseButton() {
        super(Material.BARRIER, Lang.Array.HELP_CLOSE.get());
    }

    @Override
    public void onClick(Player player, ClickType clickType) {
        SoundCompat.UI_BUTTON_CLICK.play(player, 1, 3);
        ArtisticMaps.getMenuHandler().closeMenu(player, MenuCloseReason.BACK);
    }
}