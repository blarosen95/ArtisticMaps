package com.github.blarosen95.ArtisticMaps.Recipe.PaintBuckets;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.Dye;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class PaintBucketItem implements Listener {
    public DyeColor color;

    private Plugin plugin = ArtisticMaps.getPlugin(ArtisticMaps.class);
    public String name;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void customRecipe() {
        ItemStack paintBucketItem = new ItemStack(Material.BUCKET, 1);
        ItemMeta paintBucketMeta = paintBucketItem.getItemMeta();

        paintBucketMeta.setDisplayName(ChatColor.AQUA + "Paint Bucket");
        ArrayList<String> paintBucketLore = new ArrayList<String>();
        paintBucketLore.add(ChatColor.WHITE + "Used for ArtisticMaps");
        paintBucketMeta.setLore(paintBucketLore);
        paintBucketMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        paintBucketItem.setItemMeta(paintBucketMeta);

        NamespacedKey paintBucketKey = new NamespacedKey(ArtisticMaps.instance(), "Paint_Bucket");
        ShapelessRecipe paintBucketRecipe = new ShapelessRecipe(paintBucketKey, paintBucketItem);

        Dye dye = new Dye();
        paintBucketRecipe.addIngredient(dye);
        paintBucketRecipe.addIngredient(Material.BUCKET);

        switch (dye.getColor().raw()) {
            case "black":
                paintBucketMeta.setDisplayName(ChatColor.AQUA + "Black Paint Bucket");
                paintBucketItem.setItemMeta(paintBucketMeta);
                plugin.getServer().addRecipe(paintBucketRecipe);
                System.out.println("Hey it's black");
        }

        plugin.getServer().addRecipe(paintBucketRecipe);
    }
}
