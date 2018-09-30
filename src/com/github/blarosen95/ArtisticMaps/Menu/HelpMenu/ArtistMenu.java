package com.github.blarosen95.ArtisticMaps.Menu.HelpMenu;

import java.text.MessageFormat;
import java.util.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.SkullMeta;

import com.github.Fupery.InvMenu.Utils.SoundCompat;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Heads.Heads;
import com.github.blarosen95.ArtisticMaps.Menu.API.ChildMenu;
import com.github.blarosen95.ArtisticMaps.Menu.API.ListMenu;
import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.CacheableMenu;

public class ArtistMenu extends ListMenu implements ChildMenu {
    private final Player viewer;

    public ArtistMenu(Player viewer) {
        super(ChatColor.BLUE + Lang.MENU_ARTIST.get(), ArtisticMaps.getMenuHandler().MENU.HELP);
        this.viewer = viewer;
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtisticMaps.getMenuHandler().MENU.HELP.get(viewer);
    }

    @Override
    protected Button[] getListItems() {
        UUID[] artists = ArtisticMaps.getArtDatabase().listArtists(this.viewer.getUniqueId());
        List<Button> buttons = new LinkedList<>();

        int notCached = artists.length - Heads.getCacheSize();
        if (notCached > 1) {
            this.viewer.sendMessage(MessageFormat.format("ArtisticMaps: {0} artist currently not cached loading the menu might take some time.", notCached));
        }

        //skip 0 as it's the viewer
        for (int i = 1; i < artists.length; i++) {
            buttons.add(new ArtworkListButton(artists[i]));
        }
        //sort list
        buttons.sort(Comparator.comparing((Button o) -> o.getItemMeta().getDisplayName().toLowerCase()));
        buttons.add(0, new ArtworkListButton(viewer.getUniqueId())); //add viewer first
        return buttons.toArray(new Button[0]);
    }

    public Player getViewer() {
        return Bukkit.getPlayer(this.viewer.getUniqueId());
    }

    private ArtistMenu getMenu() {
        return this;
    }

    public class ArtworkListButton extends Button {
        final UUID artist;

        ArtworkListButton(UUID artist) {
            super(Material.PLAYER_HEAD);
            this.artist = artist;

            SkullMeta meta = (SkullMeta) getItemMeta();
            SkullMeta head = Heads.getHeadMeta(artist);

            if (head != null) {
                meta = head.clone();
            } else {
                meta.setDisplayName(artist.toString());
            }

            meta.setLore(Collections.singletonList(HelpMenu.CLICK));
            setItemMeta(meta);
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            SoundCompat.UI_BUTTON_CLICK.play(player);
            ArtisticMaps.getMenuHandler().openMenu(player,
                    new ArtistArtworksMenu(getMenu(), this.artist, player.hasPermission("artisticmaps.admin"), 0));
        }
    }
}