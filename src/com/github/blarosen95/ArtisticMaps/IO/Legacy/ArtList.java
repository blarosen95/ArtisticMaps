package com.github.blarosen95.ArtisticMaps.IO.Legacy;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.CompressedMap;
import com.github.blarosen95.ArtisticMaps.IO.Database.ArtTable;
import com.github.blarosen95.ArtisticMaps.IO.Database.MapTable;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;

import java.util.ArrayList;
import java.util.List;

class ArtList {
    private final List<MapArt> artworks = new ArrayList<>();
    private final List<CompressedMap> maps = new ArrayList<>();

    public List<MapArt> getArtworks() {
        return artworks;
    }

    public List<CompressedMap> getMaps() {
        return maps;
    }

    boolean isEmpty() {
        return artworks.size() < 1 && maps.size() < 1;
    }

    void addArtworks() {
        if (artworks.size() > 0 || maps.size() > 0) {
            ArtTable artTable = ArtisticMaps.getArtDatabase().getArtTable();
            MapTable mapTable = ArtisticMaps.getArtDatabase().getMapTable();
            List<MapArt> failedArtworks = artTable.addArtworks(artworks);
            List<CompressedMap> failedMaps = mapTable.addMaps(maps);

            for (MapArt art : failedArtworks) {
                if (mapTable.containsMap(art.getMapId())) mapTable.deleteMap(art.getMapId());
                artworks.remove(art);
            }
            for (CompressedMap map : failedMaps) {
                if (artTable.containsMapID(map.getId())) artTable.deleteArtwork(map.getId());
            }
        }
    }
}