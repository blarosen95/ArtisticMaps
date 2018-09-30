package com.github.blarosen95.ArtisticMaps.Color;

import com.github.blarosen95.ArtisticMaps.Config.Lang;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static com.github.blarosen95.ArtisticMaps.Config.Lang.*;


public class BasicPalette implements Palette {
    private final ArtDye

            VOID	= new BasicDye(DYE_VOID.get(), 0, ChatColor.DARK_GREEN, Material.ENDER_EYE);
    private final ArtDye GRASS = new BasicDye(DYE_GRASS.get(), 1, ChatColor.DARK_GREEN, Material.GRASS);
    private final ArtDye CREAM = new BasicDye(DYE_CREAM.get(), 2, ChatColor.GOLD, Material.PUMPKIN_SEEDS);
    private final ArtDye LIGHT_GRAY = new BasicDye(Lang.DYE_LIGHT_GRAY.get(), 3, ChatColor.GRAY, Material.COBWEB);                                                            // new
            private final ArtDye RED = new BasicDye(DYE_RED.get(), 4, ChatColor.RED, Material.ROSE_RED);
    private final ArtDye ICE = new BasicDye(Lang.DYE_ICE.get(), 5, ChatColor.GRAY, Material.ICE); // new
            private final ArtDye SILVER = new BasicDye(DYE_SILVER.get(), 6, ChatColor.GRAY, Material.LIGHT_GRAY_DYE);
    private final ArtDye LEAVES = new BasicDye(Lang.DYE_LEAVES.get(), 7, ChatColor.GREEN, Material.OAK_LEAVES);                                                                // new
            private final ArtDye SNOW = new BasicDye(Lang.DYE_SNOW.get(), 8, ChatColor.BLUE, Material.SNOW); // new
            private final ArtDye GRAY = new BasicDye(DYE_GRAY.get(), 9, ChatColor.DARK_GRAY, Material.GRAY_DYE);
    private final ArtDye COFFEE = new BasicDye(DYE_COFFEE.get(), 10, ChatColor.DARK_RED, Material.MELON_SEEDS);
    private final ArtDye STONE = new BasicDye(Lang.DYE_STONE.get(), 11, ChatColor.DARK_GRAY, Material.GHAST_TEAR); // new
            private final ArtDye WATER = new BasicDye(Lang.DYE_WATER.get(), 12, ChatColor.DARK_BLUE, Material.LAPIS_BLOCK); // new
            private final ArtDye DARK_WOOD = new BasicDye(Lang.DYE_DARK_WOOD.get(), 13, ChatColor.GREEN, Material.OAK_WOOD);                                                            // new
            private final ArtDye WHITE = new BasicDye(DYE_WHITE.get(), 14, ChatColor.WHITE, Material.BONE_MEAL);
    private final ArtDye ORANGE = new BasicDye(DYE_ORANGE.get(), 15, ChatColor.GOLD, Material.ORANGE_DYE);
    private final ArtDye MAGENTA = new BasicDye(DYE_MAGENTA.get(), 16, ChatColor.LIGHT_PURPLE, Material.MAGENTA_DYE);
    private final ArtDye LIGHT_BLUE = new BasicDye(DYE_LIGHT_BLUE.get(), 17, ChatColor.BLUE, Material.LIGHT_BLUE_DYE);
    private final ArtDye YELLOW = new BasicDye(DYE_YELLOW.get(), 18, ChatColor.YELLOW, Material.DANDELION_YELLOW);
    private final ArtDye LIME = new BasicDye(DYE_LIME.get(), 19, ChatColor.GREEN, Material.LIME_DYE);
    private final ArtDye PINK = new BasicDye(DYE_PINK.get(), 20, ChatColor.LIGHT_PURPLE, Material.PINK_DYE);
    private final ArtDye GRAPHITE = new BasicDye(DYE_GRAPHITE.get(), 21, ChatColor.DARK_GRAY, Material.FLINT);
    private final ArtDye GUNPOWDER = new BasicDye(DYE_GUNPOWDER.get(), 22, ChatColor.GRAY, Material.GUNPOWDER);
    private final ArtDye CYAN = new BasicDye(DYE_CYAN.get(), 23, ChatColor.DARK_AQUA, Material.CYAN_DYE);
    private final ArtDye PURPLE = new BasicDye(DYE_PURPLE.get(), 24, ChatColor.DARK_PURPLE, Material.PURPLE_DYE);
    private final ArtDye BLUE = new BasicDye(DYE_BLUE.get(), 25, ChatColor.DARK_BLUE, Material.LAPIS_LAZULI);
    private final ArtDye BROWN = new BasicDye(DYE_BROWN.get(), 26, ChatColor.DARK_RED, Material.COCOA_BEANS);
    private final ArtDye GREEN = new BasicDye(DYE_GREEN.get(), 27, ChatColor.DARK_GREEN, Material.CACTUS_GREEN);
    private final ArtDye BRICK = new BasicDye(Lang.DYE_BRICK.get(), 28, ChatColor.RED, Material.BRICK); // new
            private final ArtDye BLACK = new BasicDye(DYE_BLACK.get(), 29, ChatColor.DARK_GRAY, Material.INK_SAC);
    private final ArtDye GOLD = new BasicDye(DYE_GOLD.get(), 30, ChatColor.GOLD, Material.GOLD_NUGGET);
    private final ArtDye AQUA = new BasicDye(DYE_AQUA.get(), 31, ChatColor.AQUA, Material.PRISMARINE_CRYSTALS);
    private final ArtDye LAPIS = new BasicDye(Lang.DYE_LAPIS.get(), 32, ChatColor.BLUE, Material.LAPIS_ORE); // new
            private final ArtDye EMERALD = new BasicDye(Lang.DYE_EMERALD.get(), 33, ChatColor.GREEN, Material.EMERALD); // new
            private final ArtDye LIGHT_WOOD = new BasicDye(Lang.DYE_LIGHT_WOOD.get(), 34, ChatColor.RED, Material.BIRCH_WOOD);                                                        // new
            private final ArtDye MAROON = new BasicDye(DYE_MAROON.get(), 35, ChatColor.DARK_RED, Material.NETHER_WART);
    private final ArtDye WHITE_TERRACOTTA = new BasicDye(Lang.DYE_WHITE_TERRACOTTA.get(), 36, ChatColor.DARK_GRAY, Material.EGG); // new
            private final ArtDye ORANGE_TERRACOTTA = new BasicDye(Lang.DYE_ORANGE_TERRACOTTA.get(), 37, ChatColor.DARK_GRAY,
                    Material.MAGMA_CREAM); // new
            private final ArtDye MAGENTA_TERRACOTTA = new BasicDye(Lang.DYE_MAGENTA_TERRACOTTA.get(), 38, ChatColor.DARK_GRAY,
                    Material.BEETROOT); // new
            private final ArtDye LIGHT_BLUE_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_BLUE_TERRACOTTA.get(), 39, ChatColor.DARK_GRAY,
                    Material.MYCELIUM);                                                                                                                            // new
            private final ArtDye YELLOW_TERRACOTTA = new BasicDye(Lang.DYE_YELLOW_TERRACOTTA.get(), 40, ChatColor.DARK_GRAY,
                    Material.GLOWSTONE_DUST); // new
            private final ArtDye LIME_TERRACOTTA = new BasicDye(Lang.DYE_LIME_TERRACOTTA.get(), 41, ChatColor.GREEN, Material.SLIME_BALL); // new
            private final ArtDye PINK_TERRACOTTA = new BasicDye(Lang.DYE_PINK_TERRACOTTA.get(), 42, ChatColor.RED, Material.SPIDER_EYE); // new
            private final ArtDye GRAY_TERRACOTTA = new BasicDye(Lang.DYE_GRAY_TERRACOTTA.get(), 43, ChatColor.DARK_GRAY, Material.SOUL_SAND); // new
            private final ArtDye LIGHT_GRAY_TERRACOTTA = new BasicDye(Lang.DYE_LIGHT_GRAY_TERRACOTTA.get(), 44, ChatColor.DARK_GRAY,
                    Material.BROWN_MUSHROOM); // new
            private final ArtDye CYAN_TERRACOTTA = new BasicDye(Lang.DYE_CYAN_TERRACOTTA.get(), 45, ChatColor.AQUA, Material.IRON_NUGGET); // new
            private final ArtDye PURPLE_TERRACOTTA = new BasicDye(Lang.DYE_PURPLE_TERRACOTTA.get(), 46, ChatColor.LIGHT_PURPLE,
                    Material.CHORUS_FRUIT); // new
            private final ArtDye BLUE_TERRACOTTA = new BasicDye(Lang.DYE_BLUE_TERRACOTTA.get(), 47, ChatColor.DARK_BLUE,
                    Material.PURPUR_BLOCK); // new
            private final ArtDye BROWN_TERRACOTTA = new BasicDye(Lang.DYE_BROWN_TERRACOTTA.get(), 48, ChatColor.DARK_GRAY, Material.COARSE_DIRT);                                    // new
            private final ArtDye GREEN_TERRACOTTA = new BasicDye(Lang.DYE_GREEN_TERRACOTTA.get(), 49, ChatColor.GREEN,
                    Material.POISONOUS_POTATO); // new
            private final ArtDye RED_TERRACOTTA = new BasicDye(Lang.DYE_RED_TERRACOTTA.get(), 50, ChatColor.RED, Material.APPLE); // new
            private final ArtDye BLACK_TERRACOTTA = new BasicDye(Lang.DYE_BLACK_TERRACOTTA.get(), 51, ChatColor.DARK_GRAY, Material.CHARCOAL);                                        // new

    private final ArtDye// Shading Dyes
    COAL = new ShadingDye(DYE_COAL.get(), true, ChatColor.DARK_GRAY, Material.COAL);
    private final ArtDye FEATHER = new ShadingDye(DYE_FEATHER.get(), false, ChatColor.WHITE, Material.FEATHER);

    private final ArtDye[] dyes = new ArtDye[] { BLACK, RED, GREEN, BROWN, BLUE, PURPLE, CYAN, SILVER, GRAY, PINK, LIME,
            YELLOW, LIGHT_BLUE, MAGENTA, ORANGE, WHITE, CREAM, COFFEE, GRAPHITE, GUNPOWDER, MAROON, AQUA, GRASS, GOLD,
            VOID, LIGHT_GRAY, ICE, LEAVES, SNOW, STONE, WATER, DARK_WOOD, BRICK, LAPIS, EMERALD, LIGHT_WOOD,
            WHITE_TERRACOTTA, ORANGE_TERRACOTTA, MAGENTA_TERRACOTTA, LIGHT_BLUE_TERRACOTTA, YELLOW_TERRACOTTA,
            LIME_TERRACOTTA, PINK_TERRACOTTA, GRAY_TERRACOTTA, LIGHT_GRAY_TERRACOTTA, CYAN_TERRACOTTA,
            PURPLE_TERRACOTTA, BLUE_TERRACOTTA, BROWN_TERRACOTTA, GREEN_TERRACOTTA, RED_TERRACOTTA, BLACK_TERRACOTTA };

    private final ArtDye[] tools = new ArtDye[] { COAL, FEATHER };

    @Override
    public ArtDye getDye(ItemStack item) {
        for (ArtDye[] dyeList : new ArtDye[][] { dyes, tools }) {
            for (ArtDye dye : dyeList) {
                if (item.getType() == dye.getMaterial()) {
                    return dye;
                }
            }
        }
        return null;
    }

    @Override
    public ArtDye[] getDyes(DyeType dyeType) {
        if (dyeType == DyeType.DYE)
            return dyes;
        else if (dyeType == DyeType.TOOL)
            return tools;
        else if (dyeType == DyeType.ALL)
            return concatenate(dyes, tools);
        else
            return null;
    }

    private ArtDye[] concatenate(ArtDye[] a, ArtDye[] b) {
        int aLength = a.length;
        int bLength = b.length;
        ArtDye[] c = new ArtDye[aLength + bLength];
        System.arraycopy(a, 0, c, 0, aLength);
        System.arraycopy(b, 0, c, aLength, bLength);
        return c;
    }

    @Override
    public BasicDye getDefaultColor() {
        return ((BasicDye) WHITE);
    }

}
