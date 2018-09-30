package com.github.blarosen95.ArtisticMaps.Color;

import com.github.blarosen95.ArtisticMaps.Painting.Pixel;
import org.bukkit.ChatColor;
import org.bukkit.Material;

public class ShadingDye extends ArtDye {

    private final boolean darken;

    ShadingDye(String name, boolean darken, ChatColor chatColor, Material material) {
        super(name, chatColor, material);
        this.darken = darken;
    }

    @Override
    public void apply(Pixel pixel) {
        pixel.setColor(getDyeColor(pixel.getColor()));
    }

    @Override
    public byte getDyeColor(final byte currentPixelColor) {
        int current = (currentPixelColor & 0xFF);
        int shade = current % 4;
        int shift;

        if (darken) {
            if (shade > 0 && shade < 3) {
                shift = -1;
            } else if (shade == 0) {
                shift = 3;
            } else {
                return currentPixelColor;
            }
        } else {
            if (shade < 2) {
                shift = 1;
            } else if (shade == 3) {
                shift = -3;
            } else {
                return currentPixelColor;
            }
        }
        return (byte) (current + shift);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ShadingDye)) return false;
        ShadingDye dye = (ShadingDye) obj;
        return dye.darken == darken;
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

}
