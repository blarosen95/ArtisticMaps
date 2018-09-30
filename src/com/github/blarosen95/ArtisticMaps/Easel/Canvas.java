package com.github.blarosen95.ArtisticMaps.Easel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.IO.Database.Map;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtItem;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;


public class Canvas {
    final short mapId;

    public Canvas(Map map) {
        this(map.getMapId());
    }

    Canvas(short mapId) {
        this.mapId = mapId;
    }

    public static Canvas getCanvas(ItemStack item) {
        if (item == null || item.getType() != Material.FILLED_MAP)
            return null;

        MapMeta meta = (MapMeta) item.getItemMeta();
        short mapId = (short) meta.getMapId();
        if (item.getItemMeta() != null && item.getItemMeta().getLore() != null
        && item.getItemMeta().getLore().contains(ArtItem.COPY_KEY)) {
            return new CanvasCopy(item);
        } else {
            return new Canvas(mapId);
        }
    }

    public ItemStack getDroppedItem() {
        return ArtMaterial.CANVAS.getItem();
    }

    public ItemStack getEaselItem() {
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        meta.setMapId(this.mapId);
        mapItem.setItemMeta(meta);
        return mapItem;
    }

    public short getMapId() {
        return this.mapId;
    }

    public static class CanvasCopy extends Canvas {
        private MapArt original;

        public CanvasCopy(Map map, MapArt original) {
            super(map);
            this.original = original;
        }

        CanvasCopy(ItemStack map) {
            super(ItemUtils.getMapID(map));
            ItemMeta meta = map.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore != null && !lore.contains(ArtItem.COPY_KEY)) {
                throw new IllegalArgumentException("This copied canvas is missing its copy key!");
            }
            String originalName = lore.get(Objects.requireNonNull(lore).indexOf(ArtItem.COPY_KEY) + 1);
            this.original = ArtisticMaps.getArtDatabase().getArtwork(originalName);
        }

        @Override
        public ItemStack getDroppedItem() {
            return this.original.getMapItem();
        }

        @Override
        public ItemStack getEaselItem() {
            ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
            MapMeta meta = (MapMeta)  mapItem.getItemMeta();
            meta.setMapId(this.mapId);
            //Set the copied lore
            meta.setLore(Arrays.asList(ArtItem.COPY_KEY, this.original.getTitle()));
            mapItem.setItemMeta(meta);
            return mapItem;
        }

        public short getOriginalId() {
            return this.original.getMapId();
        }
    }
}
