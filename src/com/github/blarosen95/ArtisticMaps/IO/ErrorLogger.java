package com.github.blarosen95.ArtisticMaps.IO;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.ref.WeakReference;
import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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
        ArtisticMaps.getScheduler().ASYNC.run(() -> {
            File file = new File(dataFolder, log);
            if (doesNotExist(throwable, file)) return;
            FileWriter fileWriter;
            PrintWriter logger;
            try {
                fileWriter = new FileWriter(file, true);
                logger = new PrintWriter(fileWriter);
                logger.println(dateFormat.format(new Date()));
                logger.println("[VERSION]:" + Bukkit.getServer().getVersion() + ", " + ArtisticMaps.instance().toString());
                logger.println("---------------------[SERVER]---------------------");
                StringBuilder loadedPlugins = new StringBuilder("Loaded Plugins: [");
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    loadedPlugins.append(plugin.toString()).append(", ");
                }
                loadedPlugins.append("]");
                logger.println(loadedPlugins.toString());
                logger.println(ArtisticMaps.getCompatManager().toString());
                logger.println("--------------------[STACKTRACE]---------------------");
                logger.println(throwable.getMessage());
                for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                    logger.println(stackTraceElement.toString());
                }
                logger.println();
                logger.println();
                logger.println();
                logger.flush();
                logger.close();
            } catch (IOException e) {
                throwable.printStackTrace();
            }
        });
    }

    private static boolean doesNotExist(Throwable throwable, File file) {
        if (!file.exists()) {
            try {
                if (!(file.createNewFile())) {
                    throwable.printStackTrace();
                    return true;
                }
            } catch (IOException e) {
                throwable.printStackTrace();
                return true;
            }
        }
        return false;
    }

    private static void log(PreparedStatement statement, Throwable throwable) {
        File dataFolder = getDataFolder();
        ArtisticMaps.getScheduler().ASYNC.run(() -> {
            File file = new File(dataFolder, log);
            if (doesNotExist(throwable, file)) return;
            FileWriter fileWriter;
            PrintWriter logger;
            try {
                fileWriter = new FileWriter(file, true);
                logger = new PrintWriter(fileWriter);
                logger.println(dateFormat.format(new Date()));
                logger.println("[VERSION]:" + Bukkit.getServer().getVersion() + ", " + ArtisticMaps.instance().toString());
                logger.println("---------------------[SERVER]---------------------");
                StringBuilder loadedPlugins = new StringBuilder("Loaded Plugins: [   ");
                for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                    loadedPlugins.append(plugin.toString()).append(", ");
                }
                loadedPlugins.append("]");
                logger.println(loadedPlugins.toString());
                logger.println(ArtisticMaps.getCompatManager().toString());
                logger.println("--------------------[STATEMENT]---------------------");
                logger.println(statement.toString());
                logger.println("--------------------[STACKTRACE]---------------------");
                logger.println(throwable.getMessage());
                for (StackTraceElement stackTraceElement : throwable.getStackTrace()) {
                    logger.println(stackTraceElement.toString());
                }
                logger.println();
                logger.println();
                logger.println();
                logger.flush();
                logger.close();
            } catch (IOException e) {
                throwable.printStackTrace();
            }
        });
    }

    private static File getDataFolder() {
        if (dataFolder == null || dataFolder.get() == null) {
            dataFolder = new WeakReference<>(ArtisticMaps.instance().getDataFolder());
        }
        return dataFolder.get();
    }

}
