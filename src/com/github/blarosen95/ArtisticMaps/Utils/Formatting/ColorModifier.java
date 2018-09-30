package com.github.blarosen95.ArtisticMaps.Utils.Formatting;

import org.bukkit.ChatColor;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorModifier implements Modifier {
    private final ChatColor baseColor;
    private final HashMap<Character, ChatColor> boundColors = new HashMap<>();

    public ColorModifier(ChatColor baseColor) {
        this.baseColor = baseColor;
    }

    public ColorModifier bind(char key, ChatColor color) {
        boundColors.put(key, color);
        return this;
    }

    public ColorModifier bind(ChatColor color) {
        boundColors.put(null, color);
        return this;
    }

    @Override
    public String apply(String line) {
        String baseColorCode = "§s" + baseColor.getChar();
        String string = baseColorCode + line;
        for (Character character : boundColors.keySet()) {
            String colorCode = "§" + boundColors.get(character).getChar();
            String key = character != null ? "$" + character : "$(\\w|\\d)?";
            Pattern pattern = Pattern.compile(String.format("\\%s\\{[^\\}]{1,20}\\}", key));
            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                String group = matcher.group(1);
                String formattedGroup = group.replace(key, colorCode).replaceAll("\\}", baseColorCode);
                string = string.replaceAll(group, formattedGroup);
            }
        }
        return string;
    }
}