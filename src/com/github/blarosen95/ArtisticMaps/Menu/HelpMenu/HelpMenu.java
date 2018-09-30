package com.github.blarosen95.ArtisticMaps.Menu.HelpMenu;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Menu.API.BasicMenu;
import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Button.LinkedButton;
import com.github.blarosen95.ArtisticMaps.Menu.Button.StaticButton;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.MenuHandler;

public class HelpMenu extends BasicMenu {
    public static final String CLICK = ChatColor.GREEN + Lang.BUTTON_CLICK.get();
    public static final String DELETE_NAME = ChatColor.RED + Lang.BUTTON_DELETE_NAME.get();
    public static final String DELETE_TEXT = ChatColor.RED + Lang.BUTTON_DELETE_TEXT.get();
    public static final String RENAME_NAME = ChatColor.GREEN + Lang.BUTTON_RENAME_NAME.get();
    public static final String RENAME_TEXT = ChatColor.GREEN + Lang.BUTTON_RENAME_TEXT.get();
    public static final String ACCEPT_NAME = ChatColor.GREEN + Lang.BUTTON_ACCEPT_NAME.get();
    public static final String ACCEPT_TEXT = ChatColor.GREEN + Lang.BUTTON_ACCEPT_TEXT.get();

    public HelpMenu() {
        super(ChatColor.DARK_BLUE + Lang.MENU_HELP.get(), InventoryType.HOPPER);
    }

    @Override
    public Button[] getButtons() {
        MenuHandler.MenuList list = ArtisticMaps.getMenuHandler().MENU;
        return new Button[]{
                new StaticButton(Material.SIGN, Lang.Array.HELP_GETTING_STARTED.get()),
                new LinkedButton(list.RECIPE, Material.CRAFTING_TABLE, Lang.Array.HELP_RECIPES.get()),
                new LinkedButton(list.DYES, Material.ROSE_RED, Lang.Array.HELP_DYES.get()),
                new LinkedButton(list.TOOLS, Material.WRITABLE_BOOK, Lang.Array.HELP_TOOLS.get()),
                new LinkedButton(list.ARTIST, Material.PAINTING, Lang.Array.HELP_LIST.get())
        };
    }
}