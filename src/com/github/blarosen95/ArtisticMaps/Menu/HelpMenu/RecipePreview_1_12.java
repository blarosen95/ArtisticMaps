package com.github.blarosen95.ArtisticMaps.Menu.HelpMenu;

import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Menu.API.BasicMenu;
import com.github.blarosen95.ArtisticMaps.Menu.Button.Button;
import com.github.blarosen95.ArtisticMaps.Menu.Button.StaticButton;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class RecipePreview_1_12 extends BasicMenu {
    private final ArtMaterial recipe;

    public RecipePreview_1_12(ArtMaterial recipe) {
        super(String.format(Lang.RECIPE_HEADER.get(), recipe.name().toLowerCase()),
                InventoryType.DISPENSER);
        this.recipe = recipe;
    }

    @Override
    public void onMenuOpenEvent(Player viewer) {
        viewer.updateInventory();
    }

    @Override
    public Button[] getButtons() {
        ItemStack[] preview = recipe.getPreview();
        Button[] buttons = new Button[preview.length];

        for (int i = 0; i < preview.length; i++) {
            buttons[i] = preview[i] != null ? new StaticButton(preview[i]) : null;
        }
        return buttons;
    }
}