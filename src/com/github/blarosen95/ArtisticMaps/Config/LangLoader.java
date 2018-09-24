package com.github.blarosen95.ArtisticMaps.Config;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.HashMap;
import java.util.List;

class LangLoader {
    private JavaPlugin plugin;
    private FileConfiguration defaults;
    private FileConfiguration lang;
    private boolean usingCustomLang = false;
    private HashMap<String, String> missingStrings = new HashMap<>();

    LangLoader(ArtisticMaps plugin, Configuration configuration) {
        this.plugin = plugin;
        String language = configuration.LANGUAGE;
        plugin.getLogger().info(String.format("Loading '%s' language file", language.toLowerCase()));
        defaults = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile("lang.yml"));
        lang = null;
/*
        if (language.equalsIgnoreCase("custom")) {
            usingCustomLang = true;
            File customLang = getCustomLangFile();
            lang = YamlConfiguration.loadConfiguration(customLang);
        }
        else */if (!language.equalsIgnoreCase("english")) {
            String languageFileName = String.format("lang%s.yml", File.separator + language);
            Reader langReader = plugin.getTextResourceFile(languageFileName);
            if (langReader != null) {
                lang = YamlConfiguration.loadConfiguration(plugin.getTextResourceFile(languageFileName));
            } else {
                logLangError(String.format("Error loading lang.yml! '%s' is not a valid language", language));
            }
        }
        if (lang == null) {
            lang = defaults;
            usingCustomLang = false;
        }
        if (configuration.HIDE_PREFIX) Lang.PREFIX = "";
    }

    String loadString(String key) {
        if (!lang.contains(key)) {
            logLangError(String.format("Error loading key from lang.yml: '%s'", key));
            if (defaults == null || !defaults.contains(key)) return null;
            String defaultString = defaults.getString(key);
            missingStrings.put(key, defaultString);
            return defaultString;
        }
        return lang.getString(key);
    }

    private void logLangError(String reason) {
        plugin.getLogger().warning(reason + " Default values will be used.");
    }
}
