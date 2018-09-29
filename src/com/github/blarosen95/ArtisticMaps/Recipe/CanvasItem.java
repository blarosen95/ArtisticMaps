//TODO: DELETE
package com.github.blarosen95.ArtisticMaps.Recipe;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class CanvasItem implements Listener {

    private Plugin plugin = ArtisticMaps.getPlugin(ArtisticMaps.class);
    public String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    public void customRecipe() {
        ItemStack canvasItem = new ItemStack(Material.MAP, 1);
        ItemMeta canvasMeta = canvasItem.getItemMeta();

        canvasMeta.setDisplayName(ChatColor.AQUA + "Canvas");
        ArrayList<String> canvasLore = new ArrayList<String>();
        canvasLore.add(ChatColor.WHITE + "Used for ArtisticMaps");
        canvasMeta.setLore(canvasLore);
        canvasMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        canvasItem.setItemMeta(canvasMeta);

        NamespacedKey canvasKey = new NamespacedKey(ArtisticMaps.instance(), "Canvas");
        ShapedRecipe canvasRecipe = new ShapedRecipe(canvasKey, canvasItem);

        canvasRecipe.shape("lel", "epe", "lel");
        canvasRecipe.setIngredient('l', Material.LEATHER);
        canvasRecipe.setIngredient('e', Material.EMERALD);
        canvasRecipe.setIngredient('p', Material.MAP);

        plugin.getServer().addRecipe(canvasRecipe);
    }

}
