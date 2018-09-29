package com.github.blarosen95.ArtisticMaps.Painting.Brushes;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Color.ArtDye;
import com.github.blarosen95.ArtisticMaps.Painting.Brush;
import com.github.blarosen95.ArtisticMaps.Painting.CanvasRenderer;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtItem;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class Fill extends Brush {
    private final ArrayList<CachedPixel> lastFill;
    private int axisLength;

    public Fill(CanvasRenderer renderer) {
        super(renderer);
        lastFill = new ArrayList<>();
        this.axisLength = getAxisLength();
        cooldownMilli = 350;
    }

    @Override
    public void paint(BrushAction action, ItemStack bucket, long strokeTime) {

        if (action == BrushAction.LEFT_CLICK) {
            ItemMeta meta = bucket.getItemMeta();

            if (!meta.hasLore()) {
                return;
            }
            ArtDye color = ArtItem.DyeBucket.getColor(bucket);

            if (color != null) {
                clean();
                fillPixel(color);
            }

        } else if (lastFill.size() > 0) {
            for (CachedPixel cachedPixel : lastFill) {
                addPixel(cachedPixel.x, cachedPixel.y, cachedPixel.color);
            }
        }
    }

    @Override
    public boolean checkMaterial(ItemStack bucket) {
        return ArtItem.DyeBucket.getColor(bucket) != null;
    }

    @Override
    public void clean() {
        lastFill.clear();
    }

    private void fillPixel(ArtDye color) {
        final byte[] pixel = getCurrentPixel();

        if (pixel != null) {

            final boolean[][] colored = new boolean[axisLength][axisLength];
            final byte clickedColor = getPixelBuffer()[pixel[0]][pixel[1]];
            final byte setColor = color.getDyeColor(clickedColor);

            ArtisticMaps.getScheduler().ASYNC.run(() -> fillBucket(colored, pixel[0], pixel[1], clickedColor, setColor));
        }
    }

    private void fillBucket(boolean[][] colored, int x, int y, byte sourcecolor, byte newColor) {
        if (x <0 || y < 0) {
            return;
        }
        if (x >= axisLength || y >= axisLength) {
            return;
        }

        if (colored[x][y]) {
            return;
        }

        if (getPixelBuffer()[x][y] != sourcecolor) {
            return;
        }
        addPixel(x, y, newColor);
        colored[x][y] = true;
        lastFill.add(new CachedPixel(x, y, sourcecolor));

        fillBucket(colored, x - 1, y, sourcecolor, newColor);
        fillBucket(colored, x + 1, y, sourcecolor, newColor);
        fillBucket(colored, x, y - 1, sourcecolor, newColor);
        fillBucket(colored, x, y + 1, sourcecolor, newColor);
    }

    private static class CachedPixel {
        final int x, y;
        final byte color;

        CachedPixel(int x, int y, byte color) {
            this.x = x;
            this.y = y;
            this.color = color;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof CachedPixel)) return false;
            CachedPixel cachedPixel = (CachedPixel) obj;
            return cachedPixel.x == x && cachedPixel.y == y && cachedPixel.color == color;
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }
    }
}
