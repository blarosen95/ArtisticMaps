package com.github.blarosen95.ArtisticMaps.Menu.HelpMenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import com.github.Fupery.InvMenu.Utils.MenuType;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Menu.API.BasicMenu;
import com.github.blarosen95.ArtisticMaps.Menu.API.ChildMenu;
import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Button.LinkedButton;
import com.github.blarosen95.ArtisticMaps.Menu.Button.StaticButton;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.CacheableMenu;

public class ToolMenu extends BasicMenu implements ChildMenu {

    public ToolMenu() {
        super(ChatColor.DARK_BLUE + Lang.MENU_TOOLS.get(), new MenuType(9));
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtisticMaps.getMenuHandler().MENU.HELP.get(viewer);
    }

    @Override
    public Button[] getButtons() {
        String[] back = {"§c§l⬅"}; //TODO: this is (at least) the second time this has been explicitly declared the
        //TODO (continued): value of a String[] "back". Set it as a field in either ArtisticMaps or Lang (Lang.Array.back?)??
        return new Button[]{
                new LinkedButton(ArtisticMaps.getMenuHandler().MENU.HELP, Material.MAGENTA_GLAZED_TERRACOTTA, back),
                new StaticButton(Material.AIR),
                new StaticButton(Material.SIGN, Lang.Array.INFO_TOOLS.get()),
                new LinkedButton(ArtisticMaps.getMenuHandler().MENU.DYES, Material.ROSE_RED, Lang.Array.TOOL_DYE.get()),
                new StaticButton(Material.BUCKET, Lang.Array.TOOL_PAINTBUCKET.get()),
                new StaticButton(Material.COAL, Lang.Array.TOOL_COAL.get()),
                new StaticButton(Material.FEATHER, Lang.Array.TOOL_FEATHER.get()),
                new StaticButton(Material.COMPASS, Lang.Array.TOOL_COMPASS.get()),
        };
    }
}