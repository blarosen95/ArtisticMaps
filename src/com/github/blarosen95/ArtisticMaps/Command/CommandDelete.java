package com.github.blarosen95.ArtisticMaps.Command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;

class CommandDelete extends AsyncCommand {

    CommandDelete() {
        super(null, "/art delete <title>", true);
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        if (ArtisticMaps.getConfiguration().FORCE_GUI) {
            sender.sendMessage("Please use the Paint Brush in order to delete the artwork.");
            return;
        }

        MapArt art = ArtisticMaps.getArtDatabase().getArtwork(args[1]);

        if (art == null) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
            return;
        }
        if (sender instanceof Player &&
                !(art.getArtistPlayer().getUniqueId().equals(((Player) sender).getUniqueId())
                        || sender.hasPermission("artisticmaps.admin"))) {
            msg.message = Lang.NO_PERM.get();
            return;
        }
        if (ArtisticMaps.getArtDatabase().deleteArtwork(art)) {
            msg.message = String.format(Lang.DELETED.get(), args[1]);
        } else {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
        }
    }
}
