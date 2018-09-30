package com.github.blarosen95.ArtisticMaps.Color;

import org.bukkit.inventory.ItemStack;

public interface Palette {

    ArtDye getDye(ItemStack item);

    ArtDye[] getDyes(DyeType dyeType);

    BasicDye getDefaultColor();
}
