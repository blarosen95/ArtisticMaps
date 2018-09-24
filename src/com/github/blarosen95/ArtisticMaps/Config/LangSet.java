package com.github.blarosen95.ArtisticMaps.Config;

import org.bukkit.command.CommandSender;

interface LangSet<T> {
    void send(CommandSender sender);

    T get();
}
