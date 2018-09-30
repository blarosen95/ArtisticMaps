package com.github.blarosen95.ArtisticMaps.Utils.Formatting;

import java.util.Arrays;

public class AlignmentModifier implements Modifier {
    private final Alignment alignment;
    private final int lineLength;

    public AlignmentModifier(Alignment alignment, int lineLength) {
        this.alignment = alignment;
        this.lineLength = lineLength;
    }

    private static int cumulativeLength(String[] strings) {
        int minimumLength = 0;
        for (String word : strings) minimumLength += word.length();
        return minimumLength;
    }

    private static String whitespace(int length) {
        return String.valueOf(repeatChar(length));
    }

    private static char[] repeatChar(int times) {
        char[] chars = new char[times];
        Arrays.fill(chars, '_');
        return chars;
    }

    @Override
    public String apply(String rawString) {
        String string = rawString.trim();
        switch (alignment) {
            case LEFT:
                return string;
            case RIGHT:
                return whitespace(lineLength - string.length()) + string;
            case CENTER:
                double space = ((double) lineLength) / 2D;
                String padLeft = whitespace((int) Math.ceil(space));
                String padRight = whitespace((int) Math.floor(space));
                return padLeft + string + padRight;
            case JUSTIFIED:
                String[] words = string.split("(([ \\t])((?=.*<)|(?!.*>))|<|>)+");
                int spaceAvailable = lineLength - cumulativeLength(words);
                int spacesPerGap = (int) (spaceAvailable / words.length - 1D);
                StringBuilder justifiedString = new StringBuilder();
                for (String word : words) {
                    int trailingSpaces = spaceAvailable;
                    if (spaceAvailable >= spacesPerGap) spaceAvailable -= (trailingSpaces - spacesPerGap);
                    justifiedString.append(word).append(whitespace(trailingSpaces));
                }
                return justifiedString.toString();
            default:
                return rawString;
        }
    }

    private enum Alignment {
        LEFT, RIGHT, CENTER, JUSTIFIED
    }
}