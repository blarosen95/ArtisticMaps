package com.github.blarosen95.ArtisticMaps.Menu.API;

import java.util.Optional;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryType;

import com.github.Fupery.InvMenu.Utils.SoundCompat;

import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Button.CloseButton;
import com.github.blarosen95.ArtisticMaps.Menu.Button.LinkedButton;
import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuFactory;
import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuCloseReason;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.CacheableMenu;


public abstract class ListMenu extends CacheableMenu {
    private final String heading;
    private int page;
    private Optional<MenuFactory> parent = Optional.empty();

    protected ListMenu(String heading, int page) {
        super(heading, InventoryType.CHEST);
        this.heading = heading;
        this.page = page;
    }

    protected ListMenu(String heading, MenuFactory parent) {
        super(heading, InventoryType.CHEST);
        this.heading = heading;
        this.page = 0;
        this.parent = Optional.of(parent);
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

    @Override
    public Button[] getButtons() {
        Button[] listItems = getListItems();
        int maxButtons = 25;
        Button[] buttons = new Button[maxButtons + 2];

        if (page < 1) {
            if (this.parent.isPresent()) {
                String[] back = { "§c§l⬅" };
                buttons[0] = new LinkedButton(this.parent.get(), Material.MAGENTA_GLAZED_TERRACOTTA, back);
            } else {
                buttons[0] = new CloseButton();
            }
        } else {
            buttons[0] = new PageButton(false);

            if (page > 0) {
                buttons[0].setAmount(page - 1);
            }
        }
        if (listItems == null || listItems.length < 1) return buttons;

        int start = page * maxButtons;
        int pageLength = listItems.length - start;

        if (pageLength > 0) {
            int end = (pageLength >= maxButtons) ? maxButtons : pageLength;

            System.arraycopy(listItems, start, buttons, 1, end);

            if (listItems.length > (maxButtons + start)) {
                buttons[maxButtons + 1] = new PageButton(true);

                if (page < 64) {
                    buttons[maxButtons + 1].setAmount(page + 1);
                }

            } else {
                buttons[maxButtons + 1] = null;
            }
        }
        return buttons;
    }

    private void changePage(Player player, boolean forward) {
        if (forward) page++;
        else page--;
        refresh(player);
    }

    protected abstract Button[] getListItems();

    private class PageButton extends Button {

        final boolean forward;

        private PageButton(boolean forward) {
            super(forward ? Material.MAGENTA_GLAZED_TERRACOTTA : Material.BARRIER, forward ? "§a§l➡" : "§c§l⬅");
            this.forward = forward;
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            if (forward) SoundCompat.UI_BUTTON_CLICK.play(player);
            else SoundCompat.UI_BUTTON_CLICK.play(player);
            changePage(player, forward);
        }
    }
}
