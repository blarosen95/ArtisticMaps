package com.github.blarosen95.ArtisticMaps.IO;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;

import java.io.File;
import java.lang.ref.WeakReference;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ErrorLogger {
    private static final DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String log = "error.log";
    private static WeakReference<File> dataFolder = null;

    public static void log(Throwable throwable, String consoleMessage) {
        ArtisticMaps.instance().getLogger().warning(consoleMessage + " Check /plugins/ArtisticMaps/error.log for details.");
        log(throwable);
    }

    public static void log(PreparedStatement statement, Throwable throwable, String consoleMessage) {
        ArtisticMaps.instance().getLogger().warning(consoleMessage + " Check /plugins/ArtisticMaps/error.log for details.");
        log(statement, throwable);
    }

    public static void log(Throwable throwable) {
        File dataFolder = getDataFolder();
        ArtisticMaps.getScheduler()
    }



    public static File getDataFolder() {
        if (dataFolder == null || dataFolder.get() == null) {
            dataFolder = new WeakReference<>(ArtisticMaps.instance().getDataFolder());
        }
        return dataFolder.get();
    }

}
