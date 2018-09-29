package com.github.blarosen95.ArtisticMaps.Menu.HelpMenu;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Color.ArtDye;
import com.github.blarosen95.ArtisticMaps.Color.DyeType;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Menu.API.ListMenu;
import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Button.StaticButton;

public class DyeMenu extends ListMenu {
    public DyeMenu() {
        super("Dyes for Painting", ArtisticMaps.getMenuHandler().MENU.HELP, 0);
    }

    @Override
    protected Button[] getListItems() {
        List<Button> buttons = new ArrayList<>();
        ArtDye[] dyes = ArtisticMaps.getDyePalette().getDyes(DyeType.DYE);
        buttons.add(new StaticButton(Material.SIGN, Lang.Array.INFO_DYES.get()));

        for (ArtDye dye : dyes) {
            buttons.add(new StaticButton(dye.toItem()));
        }
        return buttons.toArray(new Button[0]);
    }
}