package com.github.blarosen95.ArtisticMaps.Command;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Event.PlayerOpenMenuEvent;
import com.github.blarosen95.ArtisticMaps.IO.CompressedMap;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.Menu.Handler.MenuHandler;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;

public class CommandHandler implements CommandExecutor {
    private final HashMap<String, AsyncCommand> commands;

    public CommandHandler() {
        commands = new HashMap<>();
        //Commands go here - note that they run on an async thread

        commands.put("save", new CommandSave());

        commands.put("delete", new CommandDelete());

        commands.put("preview", new CommandPreview());

        commands.put("palette", new AsyncCommand("artisticmaps.admin", "/art palette", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                MapView mapView = Bukkit.getServer().createMap(((Player) sender).getWorld());
                mapView.getRenderers().clear();
                mapView.setScale(Scale.CLOSEST);
                mapView.addRenderer(new MapRenderer() {
                    boolean done = false; //TODO: another boolean initialized as false, probably unnecessary
                    @Override
                    public void render(MapView view, MapCanvas canvas, Player player) {
                        if (!done) {
                            for (int y = 0; y < 128; y++) {
                                for (int x = 0; x < 128; x++) {
                                    if (x < 64) {
                                        canvas.setPixel(x, y, (byte) (y));
                                    } else {
                                        canvas.setPixel(x, y, (byte) (y + 128));
                                    }
                                }
                            }
                            done = true;
                        }
                    }
                });
                ItemStack map = new ItemStack(Material.FILLED_MAP, 1);
                MapMeta meta = (MapMeta) map.getItemMeta();
                meta.setMapId(mapView.getId());
                map.setItemMeta(meta);
                ((Player) sender).getInventory().setItemInMainHand(map);
            }
        });

        commands.put("give", new AsyncCommand("artisticmaps.admin", "/art give <player> <easel|canvas|artwork|paintbrush}:<title>> [amount]", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                Player player = Bukkit.getPlayer(args[1]);
                if (player != null) {
                    ItemStack item = null;
                    if (args[2].equalsIgnoreCase("easel")) {
                        item = ArtMaterial.EASEL.getItem();
                    } else if (args[2].equalsIgnoreCase("canvas")) {
                        item = ArtMaterial.CANVAS.getItem();
                    } else if (args[2].equalsIgnoreCase("paintbrush")) {
                        item = ArtMaterial.PAINT_BRUSH.getItem();
                    } else if (args[2].contains("artwork:")) {
                        String[] strings = args[2].split(":");
                        if (strings.length > 1) {
                            String title = strings[1];
                            MapArt art = ArtisticMaps.getArtDatabase().getArtwork(title);
                            if (art == null) {
                                sender.sendMessage(Lang.PREFIX + ChatColor.RED + String.format(Lang.MAP_NOT_FOUND.get(), title));
                                return;
                            }
                            item = art.getMapItem();
                        }
                    }
                    if (item == null) {
                        sender.sendMessage(Lang.PREFIX + ChatColor.RED + this.usage);
                        return;
                    }
                    if (args.length > 3) {
                        int amount = Integer.parseInt(args[3]);
                        if (amount > 1)
                            item.setAmount(amount);
                    }
                    ItemStack finalItem = item;
                    ArtisticMaps.getScheduler().SYNC.run(() -> ItemUtils.giveItem(player, finalItem));
                    return;
                }
                sender.sendMessage(Lang.PREFIX + ChatColor.RED + String.format(Lang.PLAYER_NOT_FOUND.get(), args[1]));
            }
        });

        //Convenience commands
        commands.put("help", new AsyncCommand("artisticmaps.menu", "/art [help]", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                if (sender instanceof Player) {
                    ArtisticMaps.getScheduler().SYNC.run(() -> {
                        //TODO: below if statement uses a BITWISE AND, replace with && if any issues trace back through/to here
                        if (args.length > 0 & sender.hasPermission("artisticmaps.admin")) {
                            Lang.Array.CONSOLE_HELP.send(sender);
                        }
                        PlayerOpenMenuEvent event = new PlayerOpenMenuEvent((Player) sender);
                        Bukkit.getServer().getPluginManager().callEvent(event);
                        MenuHandler menuHandler = ArtisticMaps.getMenuHandler();
                        menuHandler.openMenu(((Player) sender), menuHandler.MENU.HELP.get(((Player) sender)));
                    });
                } else {
                    Lang.Array.CONSOLE_HELP.send(sender);
                }
            }
        });

        commands.put("reload", new AsyncCommand("artisticmaps.admin", "/art reload", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                ArtisticMaps.getScheduler().SYNC.run(() -> {
                    JavaPlugin plugin = ArtisticMaps.instance();
                    plugin.onDisable();
                    plugin.onEnable();
                    sender.sendMessage(Lang.PREFIX + ChatColor.GREEN + "Successfully reloaded ArtisticMaps!");
                });
            }
        });

        commands.put("lookup", new AsyncCommand("artisticmaps.admin", "/art lookup <id>", true) {
            @Override
            public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
                ArtisticMaps.getScheduler().SYNC.run(() -> {
                    if (!sender.hasPermission("artisticmaps.admin")) {
                        Lang.NO_PERM.send(sender);
                        return;
                    }
                    if (args.length != 2) {
                        Lang.COMMAND_LOOKUP.send(sender);
                        return;
                    }
                    int id = Integer.parseInt(args[1]);
                    MapArt art = ArtisticMaps.getArtDatabase().getArtwork((short) id);
                    CompressedMap map = ArtisticMaps.getArtDatabase().getMapTable().getMap((short) id);
                    if (art == null) {
                        sender.sendMessage(ChatColor.RED + "Failed to lookup artwork with id: " + id + ChatColor.RESET);
                    } else {
                        sender.sendMessage("Title: " + art.getTitle());
                        OfflinePlayer player = art.getArtistPlayer();
                        if (player != null) {
                            sender.sendMessage("Artist: " + player.getName());
                        } else {
                            sender.sendMessage("Artist: " + art.getArtist());
                        }
                        sender.sendMessage("Date: " + art.getDate());
                    }
                    if (map != null) {
                        sender.sendMessage("Map data exists in the database.");
                    } else {
                        sender.sendMessage(ChatColor.RED + "Map data is missing from the database!" + ChatColor.RESET);
                    }
                });
            }
        });
    }

    @Override
    //TODO: the Command command parameter almost certainly can just be declared as type "Command"
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (args.length > 0) {
            if (commands.containsKey(args[0].toLowerCase())) {
                commands.get(args[0].toLowerCase()).runPlayerCommand(sender, args);
            } else {
                Lang.HELP.send(sender);
            }
        } else {
            commands.get("help").runPlayerCommand(sender, args);
        }
        return true;
    }
}