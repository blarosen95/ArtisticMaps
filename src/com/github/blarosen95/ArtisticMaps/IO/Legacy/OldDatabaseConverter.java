package com.github.blarosen95.ArtisticMaps.IO.Legacy;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.ColorMap.f32x32;
import com.github.blarosen95.ArtisticMaps.IO.CompressedMap;
import com.github.blarosen95.ArtisticMaps.IO.Database.SQLiteDatabase;
import com.github.blarosen95.ArtisticMaps.IO.Database.SQLiteTable;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OldDatabaseConverter {
    private JavaPlugin plugin;

    public OldDatabaseConverter(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean convertDatabase() {
        if (new FlatDatabaseConverter(plugin).convertDatabase()) return true;
        String dbFileName = "ArtMap.db";
        File databaseFile = new File(plugin.getDataFolder(), dbFileName);
        if (!databaseFile.exists()) return false;

        plugin.getLogger().info("Old 'ArtMap.db' database found! Converting to new format ...");
        plugin.getLogger().info("(This may take a while, but only needs to run once)");

        ArtList artList = readArtworks();
        artList.addArtworks();

        if (!databaseFile.renameTo(new File(plugin.getDataFolder(), dbFileName + ".off"))) {
            plugin.getLogger().info("Error disabling ArtMap.db! Delete this file manually.");
            return true;
        }
        plugin.getLogger().info(String.format("Conversion completed! %s artworks converted. " +
                "ArtMap.db has been disabled.", artList.getArtworks().size()));

        return true;
    }

    private ArtList readArtworks() {
        ArtList artList = new ArtList();
        OldDatabase database = new OldDatabase(plugin);
        OldDatabaseTable table = new OldDatabaseTable(database);
        if (!database.initialize(table)) return artList;

        for (RichMapArt artwork : table.readArtworks()) {
            String title = artwork.getArt().getTitle();
            if (Bukkit.getMap(artwork.getArt().getMapId()) == null) {
                plugin.getLogger().info(String.format("    Ignoring '%s' (failed to access map data) ...", title));
                continue;
            }
            OfflinePlayer player = artwork.getArt().getArtistPlayer();
            if (player == null || !player.hasPlayedBefore()) {
                plugin.getLogger().info(String.format("    Ignoring '%s' (artist UUID is invalid) ...", title));
                continue;
            }
            if (ArtisticMaps.getArtDatabase().containsArtwork(artwork.getArt(), true)) {
                plugin.getLogger().info(String.format("    Ignoring '%s' (already exists in database) ...", title));
                continue;
            }
            plugin.getLogger().info(String.format("    Converting '%s' ...", title));
            artList.getArtworks().add(artwork.getArt());
            artList.getMaps().add(artwork.getMap());
        }
        return artList;
    }

    private static class RichMapArt {
        private final MapArt art;
        private final CompressedMap mapData;

        RichMapArt(MapArt art, CompressedMap mapData) {
            this.art = art;
            this.mapData = mapData;
        }

        public MapArt getArt() {
            return art;
        }

        public CompressedMap getMap() {
            return mapData;
        }
    }

    private class OldDatabase extends SQLiteDatabase {

        OldDatabase(JavaPlugin plugin) {
            super(new File(plugin.getDataFolder(), "ArtMap.db"));
        }

        private boolean initialize(OldDatabaseTable table) {
            return super.initialize(table);
        }

        @Override
        protected Connection getConnection() {
            return super.getConnection();
        }
    }

    private class OldDatabaseTable extends SQLiteTable {

        OldDatabaseTable(SQLiteDatabase database) {
            super(database, "artworks", "SELECT * FROM artworks");
        }

        List<RichMapArt> readArtworks() {
            return new QueuedQuery<List<RichMapArt>>() {

                protected void prepare(PreparedStatement statement) throws SQLException {
                }

                protected List<RichMapArt> read(ResultSet set) throws SQLException {
                    List<RichMapArt> artList = new ArrayList<>();
                    while (set.next()) {
                        try {
                            artList.add(readArtwork(set));
                        } catch (Exception ignored) {
                        }
                    }
                    return artList;
                }
            }.execute("SELECT * FROM artworks");
        }

        private RichMapArt readArtwork(ResultSet set) throws SQLException {
            String title = set.getString("title");
            short id = (short) set.getInt("id");
            UUID artist = UUID.fromString(set.getString("artist"));
            String date = set.getString("date");
            MapArt art = new MapArt(id, title, artist, date);
            byte[] map = new byte[0];
            try {
                map = new f32x32().readBLOB(set.getBytes("map"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            CompressedMap data = CompressedMap.compress(id, map);
            return new RichMapArt(art, data);
        }

        @Override
        protected boolean create() {
            return new QueuedQuery<Boolean>() {
                @Override
                protected void prepare(PreparedStatement statement) throws SQLException {

                }

                @Override
                protected Boolean read(ResultSet set) throws SQLException {
                    return set.next();
                }
            }.execute("SELECT * FROM artworks LIMIT 1");
        }
    }
}
