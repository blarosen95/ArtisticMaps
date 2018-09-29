package com.github.blarosen95.ArtisticMaps.Utils.Formatting;

public class TooltipsUtils {
    private String[] tooltip;

    public TooltipsUtils(String[] tooltip) {
        this.tooltip = tooltip;
    }

    public void justify() {
        int lineLength = 0;
        for (String line : tooltip) if (line.length() > lineLength) lineLength = line.length();
    }
}
