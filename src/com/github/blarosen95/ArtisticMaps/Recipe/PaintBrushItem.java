//TODO: DELETE
package com.github.blarosen95.ArtisticMaps.Recipe;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

class PaintBrushItem implements Listener {

    private final Plugin plugin = ArtisticMaps.getPlugin(ArtisticMaps.class);
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    private ItemMeta paintBrushMeta;

    public void customRecipe() {
        ItemStack paintBrushItem = new ItemStack(Material.FEATHER, 1);
        //ItemMeta paintBrushMeta = paintBrushItem.getItemMeta();
        this.paintBrushMeta = paintBrushItem.getItemMeta();

        paintBrushMeta.setDisplayName(ChatColor.AQUA + "Paint Brush");
        ArrayList<String> paintBrushLore = new ArrayList<>();
        paintBrushLore.add(ChatColor.WHITE + "Used for ArtisticMaps");
        paintBrushMeta.setLore(paintBrushLore);
        paintBrushMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paintBrushItem.setItemMeta(paintBrushMeta);

        NamespacedKey paintBrushKey = new NamespacedKey(ArtisticMaps.instance(), "Paint_Brush");
        ShapelessRecipe paintBrushRecipe = new ShapelessRecipe(paintBrushKey, paintBrushItem);

        paintBrushRecipe.addIngredient(Material.STICK);
        paintBrushRecipe.addIngredient(Material.RABBIT_HIDE);

        plugin.getServer().addRecipe(paintBrushRecipe);
    }

}
