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

class EaselItem implements Listener {

    private final Plugin plugin = ArtisticMaps.getPlugin(ArtisticMaps.class);
    private String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }



    public void customRecipe() {
        ItemStack easelItem = new ItemStack(Material.ARMOR_STAND, 1);
        ItemMeta easelMeta = easelItem.getItemMeta();

        easelMeta.setDisplayName(ChatColor.AQUA + "Easel");
        ArrayList<String> easelLore = new ArrayList<>();
        easelLore.add(ChatColor.WHITE + "Used for ArtisticMaps");
        easelMeta.setLore(easelLore);
        easelMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        easelItem.setItemMeta(easelMeta);

        //NamespacedKey key = new NamespacedKey(ArtisticMaps.instance(), this.getName());
        NamespacedKey easelKey = new NamespacedKey(ArtisticMaps.instance(), "Easel");
        ShapedRecipe easelRecipe = new ShapedRecipe(easelKey, easelItem);

        easelRecipe.shape("*s*", "tft", "lal");
        easelRecipe.setIngredient('s', Material.STICK);
        easelRecipe.setIngredient('t', Material.STRING);
        easelRecipe.setIngredient('f', Material.ITEM_FRAME);
        easelRecipe.setIngredient('l', Material.LEATHER);
        easelRecipe.setIngredient('a', Material.ARMOR_STAND);

        plugin.getServer().addRecipe(easelRecipe);
    }

}
