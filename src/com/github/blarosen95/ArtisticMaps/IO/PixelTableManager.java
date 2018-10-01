package com.github.blarosen95.ArtisticMaps.IO;

//~~TODO: Fix awful package name for DataTables imports~~ DONE
import me.Fupery.DataTables.DataTables;
import me.Fupery.DataTables.PixelTable;
import me.Fupery.DataTables.DataTables.InvalidResolutionFactorException;
import org.bukkit.plugin.java.JavaPlugin;

public class PixelTableManager {
    private final int resolutionFactor;
    private final float[] yawBounds;
    private final Object[] pitchBounds;

    private PixelTableManager(int resolutionFactor, float[] yawBounds, Object[] pitchBounds) {
        this.resolutionFactor = resolutionFactor;
        this.yawBounds = yawBounds;
        this.pitchBounds = pitchBounds;
    }

    public static PixelTableManager buildTables(JavaPlugin plugin) {
        System.out.println("21PTM");
        PixelTable table;
        int mapResolutionFactor = 4;
        try {
            System.out.println(mapResolutionFactor);
            table = DataTables.loadTable(mapResolutionFactor);
            return new PixelTableManager(mapResolutionFactor, table.getYawBounds(), table.getPitchBounds());
        } catch (Exception | NoClassDefFoundError | InvalidResolutionFactorException e) {
            return null;
        }
    }

    public float[] getYawBounds() {
        return yawBounds;
    }

    public Object[] getPitchBounds() {
        return pitchBounds;
    }

    public int getResolutionFactor() {
        return resolutionFactor;
    }
}