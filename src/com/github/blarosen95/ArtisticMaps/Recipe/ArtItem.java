package com.github.blarosen95.ArtisticMaps.Recipe;

import static com.github.blarosen95.ArtisticMaps.Config.Lang.RECIPE_ARTWORK_ARTIST;
import static com.github.blarosen95.ArtisticMaps.Config.Lang.RECIPE_PAINTBUCKET_NAME;
import static com.github.blarosen95.ArtisticMaps.Config.Lang.Array.RECIPE_PAINTBUCKET;
import static org.bukkit.ChatColor.DARK_GREEN;
import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.ITALIC;
import static org.bukkit.ChatColor.YELLOW;

import java.lang.ref.WeakReference;
import java.util.*;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Color.ArtDye;
import com.github.blarosen95.ArtisticMaps.Color.DyeType;
import com.github.blarosen95.ArtisticMaps.Color.Palette;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;

public class ArtItem {

    private static final String ARTWORK_TAG = "§b§oPlayer Artwork";
    public static final String CANVAS_KEY = "§b§oArtisticMaps Canvas";
    public static final String EASEL_KEY = "§b§oArtisticMaps Easel";
    private static final String PAINT_BUCKET_KEY = "§b§oPaint Bucket";
    public static final String KIT_KEY = "§8[ArtKit]";
    public static final String PREVIEW_KEY = "§b§oPreview Artwork";
    public static final String COPY_KEY = "§b§oArtwork Copy";
    public static final String PAINT_BRUSH = "§b§oPaint Brush";

    private static WeakReference<List<ItemStack[]>> kitReference = new WeakReference<>(new LinkedList<>());

    public static ItemStack[] getArtKit(int page) {
        //Inspect cache
        if (kitReference != null && kitReference.get() != null && !Objects.requireNonNull(kitReference.get()).isEmpty()) {
            return Objects.requireNonNull(kitReference.get()).get(page).clone();
        }
        kitReference = new WeakReference<>(new LinkedList<>());
        Palette palette = ArtisticMaps.getDyePalette();
        int numDyes = palette.getDyes(DyeType.DYE).length;
        int pages = (int) Math.ceil(numDyes / 18d);
        for (int pg = 0; pg < pages; pg++) {
            ItemStack[] itemStack = new ItemStack[36];
            Arrays.fill(itemStack, new ItemStack(Material.AIR));

            for (int j = 0; j < 18; j++) {
                if (((pg * 18) + j) >= numDyes) {
                    break;
                }
                ArtDye dye = palette.getDyes(DyeType.DYE)[(pg * 18) + j];
                itemStack[j + 9] = ItemUtils.addKey(dye.toItem(), KIT_KEY);
            }

            //If > first page, add the back button to the gui
            if (pg != 0) {
                ItemStack back = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
                ItemMeta meta = back.getItemMeta();
                meta.setDisplayName("Back");
                meta.setLore(Collections.singletonList("Artkit:Back"));
                back.setItemMeta(meta);
                itemStack[27] = back;
            }

            if (pg < pages - 1) {
                ItemStack next = new ItemStack(Material.MAGENTA_GLAZED_TERRACOTTA);
                ItemMeta meta = next.getItemMeta();
                meta.setDisplayName("Next");
                meta.setLore(Collections.singletonList("Artkit:Next"));
                next.setItemMeta(meta);
                itemStack[35] = next;
            }

            itemStack[29] = new KitItem(Material.FEATHER, "§1Feather").toItemStack();
            itemStack[30] = new KitItem(Material.COAL, "§7§1Coal").toItemStack();
            itemStack[31] = new KitItem(Material.COMPASS, "§6§1Compass").toItemStack();
            itemStack[32] = ArtMaterial.PAINT_BRUSH.getItem();
            itemStack[33] = ItemUtils.addKey(new DyeBucket(palette.getDefaultColor()).toItemStack(), KIT_KEY);
            Objects.requireNonNull(kitReference.get()).add(itemStack);
        }

        return Objects.requireNonNull(kitReference.get()).get(page).clone();
    }

    static class CraftableItem extends CustomItem {
        public CraftableItem(String itemName, Material material, String uniqueKey) {
            super(material, uniqueKey);
            try {
                recipe(ArtisticMaps.getRecipeLoader().getRecipe(itemName.toUpperCase()));
            } catch (RecipeLoader.InvalidRecipeException e) {
                e.printStackTrace();
            }
        }

        @Override
        public CustomItem name(Lang name) {
            return super.name("§e•§6§l" + name.get() + "§e•");
        }
    }

    public static class DyeBucket extends CustomItem {
        DyeBucket(ArtDye dye) {
            super(Material.BUCKET, bucketKey(dye));
            if (dye == null) dye = ArtisticMaps.getDyePalette().getDefaultColor();
            name(bucketName(dye));
            tooltip(RECIPE_PAINTBUCKET.get());
            flag(ItemFlag.HIDE_ENCHANTS);
            enchant();
            recipe(new SimpleRecipe.Shapeless(dye.rawName().replaceAll(" ", "_") + "_bucket").add(Material.BUCKET).add(new Ingredient.WrappedItem(dye.toItem())));
        }

        public static ArtDye getColor(ItemStack bucket) {
            if (bucket.getType() == Material.BUCKET && bucket.hasItemMeta() && bucket.getItemMeta().hasLore()) {
                ItemMeta meta = bucket.getItemMeta();
                String key = meta.getLore().get(0);

                for (ArtDye dye : ArtisticMaps.getDyePalette().getDyes(DyeType.ALL)) {
                    if (key.equals(bucketKey(dye))) {
                        return dye;
                    }
                }
            }
            return null;
        }

        private static String bucketKey(ArtDye dye) {
            return dye == null ? PAINT_BUCKET_KEY : PAINT_BUCKET_KEY + " §7[" + dye.rawName() + "]";
        }

        private static String bucketName(ArtDye dye) {
            return String.format("§e•%s§l%s§e•", dye.getDisplayColor(), RECIPE_PAINTBUCKET_NAME.get());
        }
    }

    public static class ArtworkItem extends CustomItem {
        public ArtworkItem(short id, String title, OfflinePlayer player, String date) {
            super(new ItemStack(Material.FILLED_MAP), ARTWORK_TAG);
            MapMeta meta = (MapMeta) this.stack.get().getItemMeta();
            meta.setMapId(id);
            this.stack.get().setItemMeta(meta);
            String name = player != null ? player.getName() : "Player";
            name(title);
            String artist = GOLD + String.format(RECIPE_ARTWORK_ARTIST.get(), (YELLOW + name));
            tooltip(artist, DARK_GREEN + "" + ITALIC + date);
        }
    }

    static class KitItem extends CustomItem {
        KitItem(Material material, String name) {
            super(material, KIT_KEY, name);
        }
    }
}