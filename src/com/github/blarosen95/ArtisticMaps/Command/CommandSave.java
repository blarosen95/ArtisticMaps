package com.github.blarosen95.ArtisticMaps.Command;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Easel.Canvas;
import com.github.blarosen95.ArtisticMaps.Easel.Easel;
import com.github.blarosen95.ArtisticMaps.Easel.EaselEffect;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.IO.TitleFilter;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;

class CommandSave extends AsyncCommand {
    private TitleFilter filter;

    CommandSave() {
        super("artisticmaps.artist", "/art save <title>", false);
        this.filter = new TitleFilter(Lang.Filter.ILLEGAL_EXPRESSIONS.get());
    }

    @Override
    public void runCommand(CommandSender sender, String[] args, ReturnMessage msg) {
        if (ArtisticMaps.getConfiguration().FORCE_GUI) {
            sender.sendMessage("Please use the Paint Brush to save.");
            return;
        }

        final String title = args[1];
        final Player player = (Player) sender;

        if (!this.filter.check(title)) {
            msg.message = Lang.BAD_TITLE.get();
            return;
        }

        if (!ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            Lang.NOT_RIDING_EASEL.send(player);
            return;
        }

        ArtisticMaps.getScheduler().SYNC.run(() -> {
            Easel easel;
            easel = ArtisticMaps.getArtistHandler().getEasel(player);

            if (easel == null) {
                Lang.NOT_RIDING_EASEL.send(player);
                return;
            }
            easel.playEffect(EaselEffect.SAVE_ARTWORK);
            ArtisticMaps.getArtistHandler().removePlayer(player);

            Canvas canvas = Canvas.getCanvas(easel.getItem());
            MapArt art1 = ArtisticMaps.getArtDatabase().saveArtwork(canvas, title, player);
            if (art1 != null) {
                easel.setItem(new ItemStack(Material.AIR));
                ItemUtils.giveItem(player, art1.getMapItem());
                player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_SUCCESS.get(), title));
            } else {
                player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_FAILURE.get(), title));
            }
        });
    }
}
