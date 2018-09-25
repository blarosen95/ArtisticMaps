package com.github.blarosen95.ArtisticMaps.IO.Database;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.IO.ErrorLogger;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.Utils.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

public final class Database {
    private final String DATABASE_ACCESS_ERROR = "Error accessing database, try using /artmap reload.";
    private final ArtTable artworks;
    private final MapTable maps;
    private final SQLiteDatabase database;
    private final MapIDQueue idQueue;
    private final BukkitRunnable AUTO_SAVE = new BukkitRunnable() {
        @Override
        public void run() {
            for (UUID uuid : ArtMap.getArtistHandler().getArtists()) {
                ArtisticMaps.getArtistHandler().getCurrentSession(uuid).persistMap(false);
            }
        }
    };

    public Database(JavaPlugin plugin, SQLiteDatabase database, ArtTable artworks, MapTable maps) {
        this.database = database;
        this.artworks = artworks;
        this.maps = maps;
        idQueue = new MapIDQueue(plugin);
        int delay = ArtisticMaps.getConfiguration().ARTWORK_AUTO_SAVE;
        AUTO_SAVE.runTaskTimerAsynchronously(plugin, delay, delay);
        idQueue.loadIds();
    }

    public static Database build(JavaPlugin plugin) {
        SQLiteDatabase database;
        ArtTable artworks;
        MapTable maps;
        database = new SQLiteDatabase(new File(plugin.getDataFolder(), "Art.db"));
        if (!database.initialize(artworks = new ArtTable(database), maps = new MapTable(database))) return null;
        Database db = new Database(plugin, database, artworks, maps);
        try {
            db.loadArtworks();
        } catch (Exception e) {
            ErrorLogger.log(e, "Error Loading ArtMap Database");
            return null;
        }
        return db;
    }

    public MapArt getArtwork(String title) {
        return artworks.getArtwork(title);
    }

    public MapArt getArtwork(short id) {
        return artworks.getArtwork(id);
    }

    // TODO: Feels wrong to handle all of this here.
    public MapArt saveArtwork(Canvas art, String title, Player player) {
        // handle update case or all ready used name
        MapArt mapArt = ArtisticMaps.getArtDatabase().getArtwork(title);
        if (mapArt != null) { // same name
            if (art instanceof Canvas.CanvasCopy) {
                CanvasCopy copy = CanvasCopy.class.cast(art);
                if (copy.getOriginalId() == mapArt.getMapId()) {
                    if (mapArt.getArtist().equals(player.getUniqueId()) || player.isOp()
                            || player.hasPermission("artmap.admin")) {
                        // update
                        MapView newView = getMap(art.getMapId());
                        // Force update of map data
                        mapArt.getMap().setMap(Reflection.getMap(newView), true);
                        // Update database
                        CompressedMap map = CompressedMap.compress(copy.getOriginalId(), newView);
                        maps.updateMap(map);
                        this.recycleMap(new Map(copy.getMapId())); // recycle the copy
                        return mapArt;
                    } else {
                        Lang.NO_PERM.send(player);
                        return null;
                    }
                }
            } else {
                // duplicate name
                Lang.TITLE_USED.send(player);
                return null;
            }
        }
        // new artwork
        mapArt = new MapArt(art.getMapId(), title, player);
        MapView mapView = getMap(art.getMapId());
        CompressedMap map = CompressedMap.compress(mapView);
        boolean success = artworks.addArtwork(mapArt);
        if (!success) {
            return null;
        }
        if (maps.containsMap(map.getId())) {
            maps.updateMap(map);
        } else {
            maps.addMap(map);
        }
        return mapArt;
    }

    public boolean deleteArtwork(MapArt art) {
        if (artworks.deleteArtwork(art.getTitle())) {
            maps.deleteMap(art.getMapId());
            ArtisticMaps.getScheduler().SYNC.run(() -> art.getMap().setMap(new byte[Map.Size.MAX.value]));
            return true;
        } else return false;
    }

    public boolean renameArtwork(MapArt art, String title) {
        art.setTitle(title);
        if (artworks.renameArtwork(art, title)) {
            art.getMap().setMap(art.getMap().readData(), true);
            return true;
        }
        return false;
    }

    public boolean containsArtwork(MapArt artwork, boolean ignoreMapId) {
        return artworks.containsArtwork(artwork, ignoreMapId);
    }

    public MapArt[] listMapArt(UUID artist) {
        MapArt[] art;
        try {
            art = artworks.listMapArt(artist);
        } catch (Exception e) {
            ErrorLogger.log(e, DATABASE_ACCESS_ERROR);
            art = new MapArt[0];
        }
        return art;
    }

    public UUID[] listArtists(UUID player) {
        UUID[] art;
        try {
            art = artworks.listArtists(player);
        } catch (Exception e) {
            ErrorLogger.log(e, DATABASE_ACCESS_ERROR);
            art = new UUID[]{player};
        }
        return art;
    }

    public UUID[] listArtists() {
        UUID[] art = null;
        try {
            art = artworks.listArtists();
        } catch (Exception e) {
            ErrorLogger.log(e, DATABASE_ACCESS_ERROR);
        }
        return art;
    }


    private void loadArtworks() {
        ArtMap.getScheduler().runSafely(() -> {
            int artworksRestored = 0;
            for (MapId mapId : maps.getMapIds()) {
                if (restoreMap(mapId)) artworksRestored++;
            }
            if (artworksRestored > 0)
                ArtMap.instance().getLogger()
                        .info(artworksRestored + " corrupted artworks were restored. More info at https://gitlab.com/BlockStack/ArtMap/wikis/Common-Errors");
        });
    }

    public ArtTable getArtTable() {
        return artworks;
    }

    public MapTable getMapTable() {
        return maps;
    }

    public void close() {
        idQueue.saveIds();
        AUTO_SAVE.cancel();
    }

    public Map createMap() {
        Short id = idQueue.poll();
        MapView mapView = null;
        if (id != null && getArtwork(id) == null) {
            mapView = getMap(id);
        } else {
            World world = Bukkit.getWorld(ArtisticMaps.getConfiguration().WORLD);
            if (world != null) {
                mapView = Bukkit.createMap(world);
            } else {
                ArtisticMaps.instance().getLogger()
                        .severe("MapView creation Failed! World is null! Please check that the world: option is set correctly in config.yml");
            }
        }
        if (mapView == null) {
            ArtisticMaps.instance().getLogger().severe("MapView creation Failed! id=" + id);
        }
        Reflection.setWorldMap(mapView, Map.BLANK_MAP);
        return new Map(mapView);
    }

    public void cacheMap(Map map, byte[] data) {
        accessSQL(() -> {
            CompressedMap compressedMap = CompressedMap.compress(map.getMapId(), data);
            if (maps.containsMap(map.getMapId())) maps.updateMap(compressedMap);
            else maps.addMap(compressedMap);
        });
    }

    public void restoreMap(Map map) {
        byte[] data = map.readData();
        accessSQL(() -> {
            int oldMapHash = Arrays.hashCode(data);
            if (maps.containsMap(map.getMapId())
                    && maps.getHash(map.getMapId()) != oldMapHash) {
                map.setMap(data);
            }
        });
    }

    private boolean restoreMap(MapId mapId) {
        boolean needsRestore;
        Map map = new Map(mapId.getId());
        if (!map.exists()) {
            // spicy map necromancy
            ArtisticMaps.instance().getLogger().info("Map id:" + map.getMapId() + " is missing! Restoring data file...");

            short topMapId = Map.getNextMapId();
            if (topMapId == -1 || topMapId < mapId.getId()) {
                ArtisticMaps.instance().getLogger()
                        .warning(String.format(
                                "Map Id %s could not be restored: the current maximum valid mapId is: %s.",
                                mapId.getId(), topMapId));
                return false;
            }

            ArtisticMaps.instance().writeResource("blank.dat", map.getDataFile());
            needsRestore = true;
        } else {
            byte[] storedMap = map.readData();
            needsRestore = Arrays.hashCode(storedMap) != mapId.getHash();
            if (needsRestore) {
                ArtisticMaps.instance().getLogger().info("Map id:" + map.getMapId() + " is corrupted! Restoring data file...");
            }
        }
        if (needsRestore) {
            map.setMap(maps.getMap(mapId.getId()).decompressMap());
            //ArtMap.instance().getLogger().info(String.format("Id '%s' was restored!", mapId.getId()));
            return true;
        }
        return false;
    }

    private void accessSQL(Runnable runnable) {
        SQLAccessor accessor = new SQLAccessor(runnable);
        if (Bukkit.isPrimaryThread() && !ArtisticMaps.isDisabled()) {
            ArtisticMaps.getScheduler().ASYNC.run(accessor);
        } else {
            accessor.run();
        }
    }

    public void recycleMap(Map map) {
        map.setMap(Map.BLANK_MAP);
        accessSQL(() -> {
            maps.deleteMap(map.getMapId());
            idQueue.offer(map.getMapId());
        });
    }

    public MapView getMap(short mapId) {
        return Bukkit.getMap(mapId);
    }

    private class SQLAccessor implements Runnable {

        private Runnable accessor;

        private SQLAccessor(Runnable accessor) {
            this.accessor = accessor;
        }

        @Override
        public void run() {
            try {
                accessor.run();
            } catch (Exception e) {
                ErrorLogger.log(e, DATABASE_ACCESS_ERROR);
            }
        }
    }
}