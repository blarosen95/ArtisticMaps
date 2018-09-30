package com.github.blarosen95.ArtisticMaps.Command;

import org.bukkit.command.CommandSender;

class ReturnMessage implements Runnable {
    String message;
    private final CommandSender sender;

    ReturnMessage(CommandSender sender) {
        this.sender = sender;
        this.message = null;
    }

    @Override
    public void run() {
        sender.sendMessage(message);
    }
}
