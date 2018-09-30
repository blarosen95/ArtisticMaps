package com.github.blarosen95.ArtisticMaps.Command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.Preview.ArtPreview;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;

class CommandPreview extends AsyncCommand {

    CommandPreview() {
        super(null, "/art preview <title>", false);
    }

    private static boolean previewArtwork(final Player player, final MapArt art) {
        if (ArtisticMaps.getConfiguration().FORCE_GUI) {
            player.sendMessage("Please use the Paint Brush to access previews");
            return false;
        }

        if (player.hasPermission("artisticmaps.admin")) {
            ArtisticMaps.getScheduler().SYNC.run(() -> {
                ItemStack currentItem = player.getInventory().getItemInMainHand();
                player.getInventory().setItemInMainHand(art.getMapItem());

                if (currentItem != null) {
                    ItemUtils.giveItem(player, currentItem);
                }
            });

        } else {
            ArtisticMaps.getPreviewManager().endPreview(player);

            if (player.getInventory().getItemInMainHand().getType() != Material.AIR) {
                return false;
            }

            ArtisticMaps.getPreviewManager().startPreview(player, new ArtPreview(art));
        }
        return true;
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        Player player = (Player) sender;
        MapArt art = ArtisticMaps.getArtDatabase().getArtwork(args[1]);

        if (art == null) {
            msg.message = String.format(Lang.MAP_NOT_FOUND.get(), args[1]);
            return;
        }
        if (!previewArtwork(player, art)) {
            msg.message = Lang.EMPTY_HAND_PREVIEW.get();
            return;
        }
        msg.message = String.format(Lang.PREVIEWING.get(), args[1]);
    }
}
