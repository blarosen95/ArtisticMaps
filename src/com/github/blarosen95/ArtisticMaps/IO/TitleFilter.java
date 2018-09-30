package com.github.blarosen95.ArtisticMaps.IO;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TitleFilter {
    private final String[] regex;

    public TitleFilter(String[] regex) {
        this.regex = regex;
    }

    private static boolean containsIllegalCharacters(String toExamine) {
        Pattern pattern = Pattern.compile("[!@#$|%^&*()-/\\\\;:.,<>~`?]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    private boolean containsIllegalWords(String toExamine) {
        for (String expression : regex) {
            Pattern pattern = Pattern.compile(expression);
            Matcher matcher = pattern.matcher(toExamine);
            if (matcher.find()) return true;
        }
        return false;
    }

    public boolean check(String title) {
        return !(title.length() < 3 || title.length() > 16) && !containsIllegalCharacters(title)
                && (!ArtisticMaps.getConfiguration().LANGUAGE.equalsIgnoreCase("english")
                || !(ArtisticMaps.getConfiguration().SWEAR_FILTER && containsIllegalWords(title)));
    }
}
