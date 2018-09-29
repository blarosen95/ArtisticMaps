package com.github.blarosen95.ArtisticMaps;

import com.github.blarosen95.ArtisticMaps.Command.CommandHandler;
import com.github.blarosen95.ArtisticMaps.Heads.Heads;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.MenuHandler;
import com.github.blarosen95.ArtisticMaps.Color.BasicPalette;
import com.github.blarosen95.ArtisticMaps.Color.Palette;
import com.github.blarosen95.ArtisticMaps.Compatibility.CompatibilityManager;
import com.github.blarosen95.ArtisticMaps.Config.Configuration;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Easel.EaselMap;
import com.github.blarosen95.ArtisticMaps.IO.Database.Database;
import com.github.blarosen95.ArtisticMaps.IO.ErrorLogger;
import com.github.blarosen95.ArtisticMaps.IO.Legacy.OldDatabaseConverter;
import com.github.blarosen95.ArtisticMaps.IO.PixelTableManager;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.Channel.ChannelCacheManager;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.ProtocolHandler;
import com.github.blarosen95.ArtisticMaps.Listeners.EventManager;
import com.github.blarosen95.ArtisticMaps.Painting.ArtistHandler;
import com.github.blarosen95.ArtisticMaps.Preview.PreviewManager;

import com.github.blarosen95.ArtisticMaps.Recipe.RecipeLoader;
import com.github.blarosen95.ArtisticMaps.Utils.Scheduler;
import com.github.blarosen95.ArtisticMaps.Utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.UUID;

public class ArtisticMaps extends JavaPlugin {

    private static SoftReference<ArtisticMaps> pluginInstance = null;
    private MenuHandler menuHandler;
    private ArtistHandler artistHandler;
    private VersionHandler bukkitVersion;
    private Scheduler scheduler;
    private Database database;
    private ChannelCacheManager cacheManager;
    private RecipeLoader recipeLoader;
    private CompatibilityManager compatManager;
    private ProtocolHandler protocolHandler;
    private PixelTableManager pixelTable;
    private Configuration config;
    private EventManager eventManager;
    private PreviewManager previewManager;
    private EaselMap easels;
    private Palette dyePalette;
    private boolean recipesLoaded = false; //TODO this is probably more than 99.99% likely to cause an error on startup
    // besides, booleans default to false...
    private boolean disabled;

    public static Database getArtDatabase() {
        return instance().database;
    }

    public static ArtisticMaps instance() {
        if (pluginInstance == null || pluginInstance.get() == null) {
            pluginInstance = new SoftReference<>((ArtisticMaps) Bukkit.getPluginManager().getPlugin("ArtisticMaps"));
        }
        return pluginInstance.get();
    }

    public static Scheduler getScheduler() {
        return instance().scheduler;
    }

    public static ArtistHandler getArtistHandler() {
        return instance().artistHandler;
    }

    public static VersionHandler getBukkitVersion() {
        return instance().bukkitVersion;
    }

    public static ChannelCacheManager getCacheManager() {
        return instance().cacheManager;
    }

    public static RecipeLoader getRecipeLoader() {
        return instance().recipeLoader;
    }

    public static CompatibilityManager getCompatManager() {
        return instance().compatManager;
    }

    public static MenuHandler getMenuHandler() {
        return instance().menuHandler;
    }

    public static Configuration getConfiguration() {
        return instance().config;
    }

    public static ProtocolHandler getProtocolManager() {
        return instance().protocolHandler;
    }

    public static Palette getDyePalette() {
        return instance().dyePalette;
    }

    public static PreviewManager getPreviewManager() {
        return instance().previewManager;
    }

    public static EaselMap getEasels() {
        return instance().easels;
    }

    public static PixelTableManager getPixelTable() {
        return instance().pixelTable;
    }

    public static boolean isDisabled() {
        return instance().disabled;
    }

    public void setColorPalette(Palette palette) {
        this.dyePalette = palette;
    }

    //TODO: annotate as an override
    public void onEnable() {
        pluginInstance = new SoftReference<>(this);
        saveDefaultConfig();
        compatManager = new CompatibilityManager(this);
        config = new Configuration(this, compatManager);
        scheduler = new Scheduler(this);
        bukkitVersion = new VersionHandler();
        protocolHandler = new ProtocolHandler();
        artistHandler = new ArtistHandler();
        cacheManager = new ChannelCacheManager();
        Lang.load(this, config);
        dyePalette = new BasicPalette();
        if ((database = Database.build(this)) == null) {
            getPluginLoader().disablePlugin(this);
            return;
        }
        new OldDatabaseConverter(this).convertDatabase(); //TODO we probably won't need this until 1.14 (if even then)
        if ((pixelTable = PixelTableManager.buildTables(this)) == null) {
            getLogger().warning(Lang.INVALID_DATA_TABLES.get());
            getPluginLoader().disablePlugin(this);
            return;
        }
        if (!recipesLoaded) {
            recipeLoader = new RecipeLoader(this, config);
            recipeLoader.loadRecipes();
            recipesLoaded = true;
        }
        easels = new EaselMap();
        eventManager = new EventManager(this, bukkitVersion);
        previewManager = new PreviewManager();
        menuHandler = new MenuHandler(this);
        getCommand("art").setExecutor(new CommandHandler());
        ArtisticMaps.instance();
        //Load artist button cache
        if (ArtisticMaps.getConfiguration().HEAD_PREFETCH) {
            this.getServer().getScheduler().runTaskLaterAsynchronously(this, this::initHeadCache, ArtisticMaps.getConfiguration().HEAD_PREFETCH_DELAY);
        }

        //this.getServer().getScheduler().runTaskAsynchronously(this, this::initHeadCache);

        disabled = false;

        //TODO: code from here to end of onEnable() is supplementary
        /*
        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "\n\nArtisticMaps has been enabled\n\n");
        getServer().getPluginManager().registerEvents(new EventsClass(), this);
        loadConfig();

        EaselItem easelItem = new EaselItem();
        easelItem.customRecipe();
        CanvasItem canvasItem = new CanvasItem();
        canvasItem.customRecipe();
        PaintBrushItem paintBrushItem = new PaintBrushItem();
        paintBrushItem.customRecipe();

        PaintBucketItem paintBucketItem = new PaintBucketItem();
        paintBucketItem.customRecipe();
        */
    }

    public void onDisable() {
        disabled = true;
        previewManager.endAllPreviews();
        artistHandler.stop();
        menuHandler.closeAll();
        eventManager.unregisterAll();
        database.close();
        reloadConfig();
        pluginInstance = null;
        //TODO: code from here to end of onDisable() is supplementary
        //getServer().getConsoleSender().sendMessage(ChatColor.RED + "\n\nArtisticMaps has been disabled\n\n");
    }

    private void initHeadCache() {
        UUID[] artists = ArtisticMaps.getArtDatabase().listArtists(UUID.randomUUID());
        this.getLogger().info(MessageFormat.format("Async load of {0} artists started.", artists.length - 1));
        int loaded = 0;
        //The following for loop skips the first value because it was pre-populated
        for (int i = 1; i < artists.length; i++) {
            ItemStack head = Heads.getHead(artists[i]);
            if (head != null) {
                loaded++;
            }
            //Mojang API limiter to prevent interrupts
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                //Ignored
            }
        }
        this.getLogger().info(MessageFormat.format("Async loaded {0} of {1} artists.", loaded, artists.length - 1));
    }

    public boolean writeResource(String resourcePath, File destination) {
        String writeError = String.format("Cannot write resource '%s' to destination '%s'.", resourcePath, destination.getAbsolutePath());
        if (!destination.exists())
            try {
                if (destination.createNewFile()) {
                    Files.copy(getResource(resourcePath), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    getLogger().warning(writeError + " Error: destination could not be created.");
                }
            } catch (IOException e) {
                ErrorLogger.log(e, writeError);
                return false;
            }
        return true;
    }

    public Reader getTextResourceFile(String fileName) {
        return getTextResource(fileName);
    }

    public void loadConfig() {
        getConfig().options().copyDefaults(true);
        saveConfig();
    }


}
