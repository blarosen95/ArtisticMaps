package com.github.blarosen95.ArtisticMaps.IO;

import java.io.IOException;
import java.util.Arrays;

import org.bukkit.map.MapView;

import com.github.blarosen95.ArtisticMaps.IO.ColorMap.f32x32;
import com.github.blarosen95.ArtisticMaps.IO.Database.Map;
import com.github.blarosen95.ArtisticMaps.Utils.Reflection;

public class CompressedMap extends MapId {
    private final byte[] compressedMap;

    public CompressedMap(short id, int hash, byte[] compressedMap) {
        super(id, hash);
        this.compressedMap = compressedMap;
    }

    public static CompressedMap compress(MapView mapView) {
        return compress(mapView.getId(), Reflection.getMap(mapView));
    }

    public static CompressedMap compress(short mapId, byte[] map) {
        byte[] compressed;
        try {
            compressed = new f32x32().generateBLOB(map);
        } catch (IOException e) {
            ErrorLogger.log(e, "Compression error!");
            return null;
        }
        return new CompressedMap(mapId, Arrays.hashCode(map), compressed);
    }

    public static CompressedMap compress(short newId, MapView mapView) {
        byte[] compressed;
        try {
            compressed = new f32x32().generateBLOB(Reflection.getMap(mapView));
        } catch (IOException e) {
            ErrorLogger.log(e, "Compression error!");
            return null;
        }
        return new CompressedMap(newId, Arrays.hashCode(Reflection.getMap(mapView)), compressed);
    }

    public byte[] getCompressedMap() {
        return compressedMap;
    }

    public byte[] decompressMap() {
        try {
            return compressedMap == null ? new byte[Map.Size.MAX.value] : new f32x32().readBLOB(compressedMap);
        } catch (IOException e) {
            ErrorLogger.log(e, "Decompression error!");
            return null;
        }
    }
}
