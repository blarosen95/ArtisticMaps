package com.github.blarosen95.ArtisticMaps.Recipe;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Color.ArtDye;
import com.github.blarosen95.ArtisticMaps.Color.DyeType;
import com.github.blarosen95.ArtisticMaps.Config.Lang;

public enum ArtMaterial {

    EASEL, CANVAS, MAP_ART, PAINT_BUCKET, PAINT_BRUSH;

    private CustomItem artItem;

    public static void setupRecipes() {
        EASEL.artItem = new ArtItem.CraftableItem("EASEL", Material.ARMOR_STAND, ArtItem.EASEL_KEY)
                .name(Lang.RECIPE_EASEL_NAME).tooltip(Lang.Array.RECIPE_EASEL);

        CANVAS.artItem = new ArtItem.CraftableItem("CANVAS", Material.PAPER, ArtItem.CANVAS_KEY)
                .name(Lang.RECIPE_CANVAS_NAME).tooltip(Lang.Array.RECIPE_CANVAS);

        MAP_ART.artItem = new ArtItem.ArtworkItem((short) -1, "Artwork", null, null);

        PAINT_BUCKET.artItem = new ArtItem.DyeBucket(null) {
            @Override
            public void addRecipe() {
                for (ArtDye d : ArtisticMaps.getDyePalette().getDyes(DyeType.ALL)) {
                    new ArtItem.DyeBucket(d).addRecipe();
                }
            }
        };

        PAINT_BRUSH.artItem = new ArtItem.CraftableItem("PAINT_BRUSH", Material.REDSTONE_TORCH, ArtItem.PAINT_BRUSH)
                .name(Lang.RECIPE_PAINT_BRUSH_NAME).tooltip(Lang.Array.RECIPE_PAINT_BRUSH);

        for (ArtMaterial material : values()) material.artItem.addRecipe();
    }

    public static ArtMaterial getCraftItemType(ItemStack item) {
        for (ArtMaterial material : values()) {
            if (material.artItem.checkItem(item)) return material;
        }
        return null;
    }

    public static ArtItem.ArtworkItem getMapArt(short id, String title, OfflinePlayer player, String date) {
        return new ArtItem.ArtworkItem(id, title, player, date);
    }

    public Material getType() {
        return artItem.getMaterial();
    }

    public ItemStack getItem() {
        return artItem.toItemStack();
    }

    public boolean isValidMaterial(ItemStack item) {
        return artItem.checkItem(item);
    }

    public Recipe getRecipe() {
        return artItem.getBukkitRecipe();
    }

    public ItemStack[] getPreview() {
        return artItem.getRecipe().getPreview();
    }
}
