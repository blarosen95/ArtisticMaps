package com.github.blarosen95.ArtisticMaps.Menu.Handler;

import com.github.blarosen95.ArtisticMaps.Menu.API.ChildMenu;
import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuCloseReason;
import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuFactory;
import com.github.blarosen95.ArtisticMaps.Menu.Event.MenuListener;
import com.github.blarosen95.ArtisticMaps.Menu.HelpMenu.*;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MenuHandler {
    public final MenuList MENU = new MenuList();
    private final ConcurrentHashMap<UUID, CacheableMenu> openMenus = new ConcurrentHashMap<>();
    private final MenuListener listener;

    public MenuHandler(JavaPlugin plugin) {
        listener = new MenuListener(this, plugin);
    }

    private CacheableMenu getMenu(Player viewer) {
        return openMenus.get(viewer.getUniqueId());
    }

    public boolean isTrackingPlayer(Player player) {
        return openMenus.containsKey(player.getUniqueId());
    }

    public void openMenu(Player viewer, CacheableMenu menu) {
        if (openMenus.containsKey(viewer.getUniqueId())) closeMenu(viewer, MenuCloseReason.SWITCH);
        else viewer.closeInventory();
        openMenus.put(viewer.getUniqueId(), menu);
        menu.open(viewer);
    }

    public void fireClickEvent(Player viewer, int slot, ClickType clickType) {
        if (!openMenus.containsKey(viewer.getUniqueId()) || viewer.getOpenInventory() == null) return;
        getMenu(viewer).click(viewer, slot, clickType);
    }

    public void refreshMenu(Player viewer) {
        if (!openMenus.containsKey(viewer.getUniqueId()) || viewer.getOpenInventory() == null) return;
        getMenu(viewer).refresh(viewer);
    }

    public void closeMenu(Player viewer, MenuCloseReason reason) {
        if (!openMenus.containsKey(viewer.getUniqueId())) return;
        CacheableMenu menu = getMenu(viewer);
        openMenus.remove(viewer.getUniqueId());
        menu.close(viewer, reason);
        if (menu instanceof ChildMenu && reason == MenuCloseReason.BACK) {
            openMenu(viewer, ((ChildMenu) menu).getParent(viewer));
        }
    }

    public void closeAll() {
        for (UUID uuid : openMenus.keySet()) closeMenu(Bukkit.getPlayer(uuid), MenuCloseReason.SYSTEM);
        openMenus.clear();
    }

    public static class MenuList {
        public MenuFactory HELP = new StaticMenuFactory(HelpMenu::new);
        public MenuFactory DYES = new StaticMenuFactory(DyeMenu::new);
        public MenuFactory TOOLS = new StaticMenuFactory(ToolMenu::new);
        public MenuFactory ARTIST = new DynamicMenuFactory(ArtistMenu::new);
        public MenuFactory RECIPE = new ConditionalMenuFactory(new ConditionalMenuFactory.ConditionalGenerator() {
            @Override
            public CacheableMenu getConditionTrue() {
                return new RecipeMenu(true);
            }

            @Override
            public CacheableMenu getConditionFalse() {
                return new RecipeMenu(false);
            }

            @Override
            public boolean evaluateCondition(Player viewer) {
                return viewer.hasPermission("artisticmaps.admin");
            }
        });
    }
}