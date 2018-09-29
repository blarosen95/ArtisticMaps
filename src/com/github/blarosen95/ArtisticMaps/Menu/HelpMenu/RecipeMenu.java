package com.github.blarosen95.ArtisticMaps.Menu.HelpMenu;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.Fupery.InvMenu.Utils.MenuType;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Menu.API.BasicMenu;
import com.github.blarosen95.ArtisticMaps.Menu.API.ChildMenu;
import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Button.LinkedButton;
import com.github.blarosen95.ArtisticMaps.Menu.Button.StaticButton;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.CacheableMenu;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;
import com.github.blarosen95.ArtisticMaps.Utils.VersionHandler;

public class RecipeMenu extends BasicMenu implements ChildMenu {
    private boolean adminMenu;
    private boolean version_1_12 = ArtisticMaps.getBukkitVersion().getVersion().isEqualTo(VersionHandler.BukkitVersion.v1_12);

    public RecipeMenu(boolean adminMenu) {
        super(ChatColor.DARK_BLUE + Lang.MENU_RECIPE.get(), new MenuType(9));
        this.adminMenu = adminMenu;
    }

    @Override
    public Button[] getButtons() {
        String[] back = {"§c§l⬅"};
        return new Button[]{
                new LinkedButton(ArtisticMaps.getMenuHandler().MENU.HELP, Material.MAGENTA_GLAZED_TERRACOTTA, back),
                new StaticButton(Material.AIR),
                new StaticButton(Material.SIGN, Lang.Array.INFO_RECIPES.get()),
                new RecipeButton(ArtMaterial.EASEL),
                new RecipeButton(ArtMaterial.CANVAS),
                new RecipeButton(ArtMaterial.PAINT_BUCKET),
                new RecipeButton(ArtMaterial.PAINT_BRUSH),
        };
    }

    @Override
    public CacheableMenu getParent(Player viewer) {
        return ArtisticMaps.getMenuHandler().MENU.HELP.get(viewer);
    }

    private class RecipeButton extends Button {
        final ArtMaterial recipe;

        public RecipeButton(ArtMaterial material) {
            super(material.getType());
            this.recipe = material;
            ItemMeta meta = material.getItem().getItemMeta();
            List<String> lore = meta.getLore();
            lore.add("");
            lore.add(ChatColor.GREEN + Lang.RECIPE_BUTTON.get());
            if (adminMenu) lore.add(lore.size(), ChatColor.GOLD + Lang.ADMIN_RECIPE.get());
            meta.setLore(lore);
            setItemMeta(meta);
        }

        @Override
        public void onClick(Player player, ClickType clickType) {
            if (adminMenu) {
                if (clickType == ClickType.LEFT) {
                    openRecipePreview(player);
                } else if (clickType == ClickType.RIGHT) {
                    ArtisticMaps.getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, recipe.getItem()));
                }
            } else {
                openRecipePreview(player);
            }
        }

        private void openRecipePreview(Player player) {
            if (version_1_12) {
                ArtisticMaps.getMenuHandler().openMenu(player, new RecipePreview_1_12(recipe));
            } else {
                ArtisticMaps.getMenuHandler().openMenu(player, new RecipePreview(recipe));
            }
        }
    }
}