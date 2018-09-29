package com.github.blarosen95.ArtisticMaps.IO.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.github.blarosen95.ArtisticMaps.IO.CompressedMap;
import com.github.blarosen95.ArtisticMaps.IO.ErrorLogger;
import com.github.blarosen95.ArtisticMaps.IO.MapId;

public final class MapTable extends SQLiteTable {
    public MapTable(SQLiteDatabase database) {
        super(database, "maps", "CREATE TABLE IF NOT EXISTS maps (" +
                "id INT NOT NULL UNIQUE," +
                "hash INT NOT NULL," +
                "map BLOB NOT NULL," +
                "PRIMARY KEY (id)" +
                ");");
    }

    public void addMap(CompressedMap map) {
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, map.getId());
                statement.setInt(2, map.getHash());
                statement.setBytes(3, map.getCompressedMap());
            }
        }.execute("INSERT INTO " + TABLE + " (id, hash, map) VALUES(?,?,?);");
    }

    void updateMapId(int oldMapId, int newMapId) {
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, newMapId);
                statement.setInt(2, oldMapId);
            }
        }.execute("UPDATE " + TABLE + " SET id=? WHERE id=?;");
    }

    public boolean deleteMap(short mapId) {
        return new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }
        }.execute("DELETE FROM " + TABLE + " WHERE id=?;");
    }

    public boolean containsMap(short mapId) {
        return new QueuedQuery<Boolean>() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            @Override
            protected Boolean read(ResultSet set) throws SQLException {
                return set.isBeforeFirst();
            }
        }.execute("SELECT hash FROM " + TABLE + " WHERE id=?;");
    }

    public void updateMap(CompressedMap map) {
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, map.getHash());
                statement.setBytes(2, map.getCompressedMap());
                statement.setInt(3, map.getId());
            }
        }.execute("UPDATE " + TABLE + " SET hash=?, map=? WHERE id=?;");
    }

    public CompressedMap getMap(short mapId) {
        return new QueuedQuery<CompressedMap>() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            @Override
            protected CompressedMap read(ResultSet set) throws SQLException {
                if (!set.next()) return null;
                short id = (short) set.getInt("id");
                int hash = set.getInt("hash");
                byte[] map = set.getBytes("map");
                return new CompressedMap(id, hash, map);
            }
        }.execute("SELECT * FROM " + TABLE + " WHERE id=?;");
    }

    public Integer getHash(short mapId) {
        return new QueuedQuery<Integer>() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                statement.setInt(1, mapId);
            }

            @Override
            protected Integer read(ResultSet set) throws SQLException {
                return (set.next()) ? set.getInt("hash") : null;
            }
        }.execute("SELECT hash FROM " + TABLE + " WHERE id=?;");
    }

    List<MapId> getMapIds() {
        return new QueuedQuery<List<MapId>>() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
            }

            @Override
            protected List<MapId> read(ResultSet set) throws SQLException {
                List<MapId> mapHashes = new ArrayList<>();
                while (set.next()) {
                    mapHashes.add(new MapId((short) set.getInt("id"), set.getInt("hash")));
                }
                return mapHashes;
            }
        }.execute("SELECT id, hash FROM " + TABLE + ";");
    }

    /**
     * @param maps List of maps to add to DB
     * @return List of maps that couldn't be added
     */
    public List<CompressedMap> addMaps(List<CompressedMap> maps) {
        List<CompressedMap> failed = new ArrayList<>();
        new QueuedStatement() {
            @Override
            protected void prepare(PreparedStatement statement) throws SQLException {
                for (CompressedMap map : maps) {
                    try {
                        statement.setInt(1, map.getId());
                        statement.setInt(2, map.getHash());
                        statement.setBytes(3, map.getCompressedMap());
                    } catch (Exception e) {
                        failed.add(map);
                        ErrorLogger.log(e, String.format("Error writing map '%s' to database!", map.getId()));
                        continue;
                    }
                    statement.addBatch();
                }
            }
        }.executeBatch("INSERT INTO " + TABLE + " (id, hash, map) VALUES(?,?,?);");
        return failed;
    }
}