package com.github.blarosen95.ArtisticMaps.Color;

import com.github.blarosen95.ArtisticMaps.Painting.Pixel;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class BasicDye extends ArtDye {

    private final byte color;

    BasicDye(String name, int color, ChatColor chatColor, Material material) {
        super(name, chatColor, material);
        this.color = (byte) color;

    }

    public byte getColor() {
        return (byte) ((this.color*4)+1);
    }

    @Override
    public void apply(Pixel pixel) {
        pixel.setColor(getColor());
    }

    @Override
    public byte getDyeColor(byte currentPixelColor) {
        return getColor();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BasicDye)) return false;
        BasicDye dye = (BasicDye) obj;
        return dye.color == color;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
