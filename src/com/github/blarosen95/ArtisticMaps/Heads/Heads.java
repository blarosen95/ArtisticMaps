package com.github.blarosen95.ArtisticMaps.Heads;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.net.ssl.HttpsURLConnection;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;

/**
 * Heads handler to be used for caching head textures.
 *
 * @author wispoffates
 */
public class Heads {
    static private JsonParser parser = new JsonParser();
    static private String API_PROFILE_LINK = "https://sessionserver.mojang.com/session/minecraft/profile/";

    private static Map<UUID, TextureData> textureCache = new HashMap<>();

    /**
     * Create a head item with the provided texture.
     *
     * @param playerId the ID of the player whose skull is to be retrieved
     * @return the skull
     */
    public static ItemStack getHead(UUID playerId) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = getHeadMeta(playerId);
        if (meta == null) {
            return null;
        }
        head.setItemMeta(meta);
        return head;
    }

    /**
     * Create a player SkullMeta for the provided player ID.
     *
     * @param playerId the ID of the player to retrieve SkullMeta for
     * @return the retrieved SkullMeta
     */
    public static SkullMeta getHeadMeta(UUID playerId) {
        //Check cache
        if (!textureCache.containsKey(playerId)) {
            TextureData data = getSkinUrl(playerId);
            if (data == null) {
                return null;
            }
            textureCache.put(playerId, data);
        }
        TextureData data = textureCache.get(playerId);
        if (data == null) {
            return null;
        }
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        PropertyMap propertyMap = profile.getProperties();
        if (propertyMap == null) {
            throw new IllegalStateException("Profile doesn't contain a property map");
        }
        //Handle players without skin textures
        if (!data.texture.isEmpty()) {
            propertyMap.put("textures", new Property("textures", data.texture));
        }
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, 1);
        ItemMeta headMeta = head.getItemMeta();
        Class<?> headMetaClass = headMeta.getClass();
        Reflections.getField(headMetaClass, "profile", GameProfile.class).set(headMeta, profile);
        headMeta.setDisplayName(data.name);

        return (SkullMeta) headMeta;
    }

    /**
     * Retrieves current cache size.
     *
     * @return current cache size
     */
    public static int getCacheSize() {
        return textureCache.size();
    }

    //HTTP Methods
    private static String getContent(String link) {
        try {
            URL url = new URL(link);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                return inputLine;
            }
            br.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            ArtisticMaps.instance().getLogger().info("Error retrieving head texture. Server is likely over API limit temporarily");
        }
        return null;
    }

    private static TextureData getSkinUrl(UUID uuid) {
        try {
            String id = uuid.toString().replaceAll("\\-", "");
            String json = getContent(API_PROFILE_LINK + id);
            JsonObject o = parser.parse(json).getAsJsonObject();
            String name = o.get("name").getAsString();
            String jsonBase64 = o.get("properties").getAsJsonArray().get(0).getAsJsonObject().get("value").getAsString();
            return new TextureData(name, jsonBase64);
        } catch (Exception e) {
            ArtisticMaps.instance().getLogger().info("Failed to parse JSON for id:" + uuid);
        }
        return null;
    }

    private static class TextureData {
        public String name;
        public String texture;

        public TextureData(String name, String texture) {
            this.name = name;
            this.texture = texture;
        }
    }
}