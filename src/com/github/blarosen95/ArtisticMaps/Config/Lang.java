package com.github.blarosen95.ArtisticMaps.Config;

import com.github.blarosen95.ArtisticMaps.IO.Protocol.Out.WrappedPacket;
import com.mysql.fabric.xmlrpc.base.Array;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;

public enum Lang implements LangSet<String> {

    COMMAND_SAVE, COMMAND_DELETE, COMMAND_PREVIEW, COMMAND_RESTORE, COMMAND_BACKUP, HELP, SAVE_SUCCESS, SAVE_FAILURE, DELETED,
    PREVIEWING, RECIPE_HEADER, NEED_CANVAS, NO_CONSOLE, PLAYER_NOT_FOUND, NO_PERM, NOT_RIDING_EASEL, NOT_YOUR_EASEL,
    BREAK_CANVAS, MAP_NOT_FOUND, NO_ARTWORKS, NO_CRAFT_PERM, BAD_TITLE, TITLE_USED, EMPTY_HAND_PREVIEW,
    INVALID_DATA_TABLES, CANNOT_BUILD_DATABASE, MAP_ID_MISSING, RESTORED_SUCCESSFULY, MENU_RECIPE, MENU_ARTIST,
    MENU_ARTWORKS, MENU_DYES, MENU_HELP, MENU_TOOLS, BUTTON_CLICK, BUTTON_CLOSE, BUTTON_BACK, RECIPE_BUTTON,
    ADMIN_RECIPE, RECIPE_HELP, RECIPE_EASEL_NAME, RECIPE_CANVAS_NAME, RECIPE_PAINTBUCKET_NAME, RECIPE_PAINT_BRUSH_NAME, RECIPE_ARTWORK_ARTIST, BUTTON_RENAME_NAME, BUTTON_DELETE_NAME, BUTTON_ACCEPT_NAME, BUTTON_RENAME_TEXT, BUTTON_DELETE_TEXT, BUTTON_ACCEPT_TEXT, RENAMED, TITLE_QUESTION, PAINTBRUSH_GROUND,
    DYE_BLACK, DYE_RED, DYE_GREEN, DYE_BROWN, DYE_BLUE, DYE_PURPLE, DYE_CYAN, DYE_SILVER, DYE_GRAY, DYE_PINK, DYE_LIME,
    DYE_YELLOW, DYE_LIGHT_BLUE, DYE_MAGENTA, DYE_ORANGE, DYE_WHITE, DYE_CREAM, DYE_COFFEE, DYE_GRAPHITE, DYE_GUNPOWDER,
    DYE_MAROON, DYE_AQUA, DYE_GRASS, DYE_GOLD, DYE_VOID, DYE_COAL, DYE_FEATHER, DYE_ICE, DYE_LEAVES, DYE_SNOW, DYE_BLACK_TERRACOTTA, DYE_RED_TERRACOTTA, DYE_GREEN_TERRACOTTA, DYE_BROWN_TERRACOTTA, DYE_BLUE_TERRACOTTA, DYE_PURPLE_TERRACOTTA, DYE_CYAN_TERRACOTTA, DYE_LIGHT_GRAY_TERRACOTTA, DYE_GRAY_TERRACOTTA, DYE_PINK_TERRACOTTA, DYE_LIME_TERRACOTTA, DYE_YELLOW_TERRACOTTA, DYE_LIGHT_BLUE_TERRACOTTA, DYE_MAGENTA_TERRACOTTA, DYE_ORANGE_TERRACOTTA, DYE_WHITE_TERRACOTTA, DYE_STONE, DYE_LIGHT_GRAY, DYE_BRICK, DYE_LAPIS, DYE_EMERALD, DYE_LIGHT_WOOD, DYE_WATER, DYE_DARK_WOOD;

    public static String PREFIX = "§b[ArtisticMaps] ";
    private String message = String.format("'%s'' NOT FOUND", name());

    public static void load(ArtisticMaps plugin, Configuration configuration) {
        LangLoader loader = new LangLoader(plugin, configuration);
        //Load basic messages
        for (Lang key : Lang.values()) {
            key.message = loader.loadString(key.name());
        }
        //Load action bar messages
        for (ActionBar key : ActionBar.values()) {
            String messageString = loader.loadString(key.name());
            if (configuration.DISABLE_ACTION_BAR) {
                String formattedMessage = PREFIX + messageString.replaceAll("§l", "").replaceAll("§3", "§6")
                        .replaceAll("§4", "§c").replaceAll("§b", "§6");
                key.message = new WrappedPacket<String>(formattedMessage) {
                    @Override
                    public void send(Player player) {
                        player.sendMessage(this.rawPacket);
                    }
                };
            } else {
                String formattedMessage = key.isError ?
                        "§c§l✷ " + messageString + " §c§l✷" : "§6✾ " + messageString + " §6✾";
                key.message = ArtisticMaps.getProtocolManager().PACKET_SENDER.buildChatPacket(formattedMessage);
            }
        }
        for (Array key : Array.values()) {
            key.messages = loader.loadArray(key.name());
        }
        loader.save();
        Filter.ILLEGAL_EXPRESSIONS.expressions = loader.loadRegex("ILLEGAL_EXPRESSIONS");
    }

    @Override
    public void send(CommandSender sender) {
        if (message != null) sender.sendMessage(message);
    }

    @Override
    public String get() {
        return message;
    }


    public enum ActionBar implements LangSet<WrappedPacket> {
        EASEL_HELP(false), NEED_CANVAS(true), PAINTING(false), SAVE_USAGE(false), ELSE_USING(true),
        NO_PERM_ACTION(true), NO_EDIT_PERM(true), INVALID_POS(true);

        private WrappedPacket message = null;
        private boolean isError;

        ActionBar(boolean isErrorMessage) {
            isError = isErrorMessage;
        }

        @Override
        public void send(CommandSender sender) {
            if (message != null && sender instanceof Player) {
                message.send((Player) sender);
            }
        }

        @Override
        public WrappedPacket get() {
            return message;
        }
    }

    public enum Array implements LangSet<String[]> {
        HELP_GETTING_STARTED, HELP_RECIPES, HELP_TOOLS, HELP_DYES, HELP_LIST, HELP_CLOSE, INFO_DYES, INFO_RECIPES,
        INFO_TOOLS, TOOL_DYE, TOOL_PAINTBUCKET, TOOL_COAL, TOOL_FEATHER, TOOL_COMPASS, RECIPE_EASEL, RECIPE_CANVAS,
        RECIPE_PAINTBUCKET, RECIPE_PAINT_BRUSH, CONSOLE_HELP;

        private String[] messages = null;

        @Override
        public void send(CommandSender sender) {
            if (messages != null) sender.sendMessage(messages);
        }

        @Override
        public String[] get() {
            return messages;
        }
    }

    public enum Filter implements LangSet<String[]> {
        ILLEGAL_EXPRESSIONS;

        private String[] expressions = null;

        @Override
        public void send(CommandSender sender) {

        }

        @Override
        public String[] get() {
            return expressions;
        }

    }

}
