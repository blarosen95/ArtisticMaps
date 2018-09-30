package com.github.blarosen95.ArtisticMaps.Command;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;

abstract class AsyncCommand {
    final String usage;
    private final String permission;
    private final boolean consoleAllowed;
    private int minArgs;
    private int maxArgs;

    AsyncCommand(String permission, String usage, boolean consoleAllowed) {
        this.permission = permission;
        this.consoleAllowed = consoleAllowed;

        if (usage == null) {
            throw new IllegalArgumentException("Usage must not be null");
        }
        String[] args = usage.replace("/art ", "").split("\\s+");
        maxArgs = args.length;
        minArgs = maxArgs - StringUtils.countMatches(usage, "[");
        this.usage = usage;
    }

    void runPlayerCommand(final CommandSender sender, final String args[]) {

        ArtisticMaps.getScheduler().ASYNC.run(() -> {
            ReturnMessage returnMsg = new ReturnMessage(sender);

            if (permission != null && !sender.hasPermission(permission)) {
                returnMsg.message = Lang.NO_PERM.get();
            } else if (!consoleAllowed && !(sender instanceof Player)) {
                returnMsg.message = Lang.NO_CONSOLE.get();
            } else if (args.length < minArgs || args.length > maxArgs) {
                returnMsg.message = Lang.PREFIX + ChatColor.RED + " " + usage;
            } else {
                runCommand(sender, args, returnMsg);
            }

            if (returnMsg.message != null) {
                ArtisticMaps.getScheduler().SYNC.run(returnMsg);
            }
        });
    }

    protected abstract void runCommand(CommandSender sender, String[] args, ReturnMessage msg);
}
