package com.github.blarosen95.ArtisticMaps.Color;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Painting.Pixel;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.logging.Level;

public abstract class ArtDye {
    private final String name;
    private final ChatColor chatColor;
    private final Material material;

    ArtDye(String name, ChatColor chatColor, Material material) {
        if (name == null) {
            ArtisticMaps.instance().getLogger().log(Level.SEVERE, "Dye with material: " + material + " does not have a name set!");
        }
        this.name = name;
        this.chatColor = chatColor;
        this.material = material;
    }

    public abstract void apply(Pixel pixel);

    public abstract byte getDyeColor(byte currentPixelColor);

    public String name() {
        return chatColor + name;
    }

    public String rawName() {
        return name.toUpperCase();
    }

    public ChatColor getDisplayColor() {
        return chatColor;
    }

    public Material getMaterial() {
        return material;
    }

    public ItemStack toItem() {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(chatColor + name);
        item.setItemMeta(meta);
        return item;
    }
}
