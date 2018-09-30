package com.github.blarosen95.ArtisticMaps.Easel;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.IO.Database.Map;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;
import net.md_5.bungee.api.ChatColor;

public final class EaselEvent {
    private final Easel easel;
    private final ClickType click;
    private final Player player;

    public EaselEvent(Easel easel, ClickType click, Player player) {
        this.easel = easel;
        this.click = click;
        this.player = player;
    }

    public void callEvent() {
        if (!player.hasPermission("artisticmaps.artist")) {
            Lang.NO_PERM.send(player);
            return;
        }
        if (easel.isBeingUsed()) {
            Lang.ActionBar.ELSE_USING.send(player);
            easel.playEffect(EaselEffect.USE_DENIED);
            return;
        }
        if (ArtisticMaps.getPreviewManager().endPreview(player))
            return;

        switch (click) {
            case LEFT_CLICK:
                Lang.ActionBar.EASEL_HELP.send(player);
                return;

            case RIGHT_CLICK:
                if (easel.getItem().getType() == Material.FILLED_MAP) {
                    //If easel has a canvas, then player rides easel
                    ArtisticMaps.getArtistHandler().addPlayer(player, easel, new Map(ItemUtils.getMapID(easel.getItem())),
                            EaselPart.getYawOffset(easel.getFacing()));
                    return;
                } else if (easel.getItem().getType() != Material.AIR) {
                    //Remove items added when easel's instance was unloaded
                    easel.removeItem();
                    return;
                }
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                ArtMaterial material = ArtMaterial.getCraftItemType(itemInHand);

                if (material == ArtMaterial.CANVAS) {
                    //Mount the canvas
                    Map map = ArtisticMaps.getArtDatabase().createMap();
                    //noinspection ConstantConditions
                    if (map == null) {
                        player.sendMessage(ChatColor.RED + " Severe Error. Please contact either Bot, Pemi, or Dale! This shouldn't have been possible..." + ChatColor.RESET);
                        return;
                    }
                    map.update(player);
                    mountCanvas(itemInHand, new Canvas(map));
                } else if (material == ArtMaterial.MAP_ART) {
                    //Player wants to edit an art piece on the easel
                    ArtisticMaps.getScheduler().ASYNC.run(() -> {
                        MapArt art = ArtisticMaps.getArtDatabase().getArtwork(ItemUtils.getMapID(itemInHand));
                        ArtisticMaps.getScheduler().SYNC.run(() -> {
                            if (art != null) {
                                if (!player.getUniqueId().equals(art.getArtistPlayer().getUniqueId())) {
                                    Lang.ActionBar.NO_EDIT_PERM.send(player);
                                    easel.playEffect(EaselEffect.USE_DENIED);
                                    return;
                                }
                                Canvas canvas = new Canvas.CanvasCopy(art.getMap().cloneMap(), art);
                                mountCanvas(itemInHand, canvas);
                            } else {
                                Lang.ActionBar.NEED_CANVAS.send(player);
                                easel.playEffect(EaselEffect.USE_DENIED);
                            }
                        });
                    });
                } else {
                    Lang.ActionBar.NEED_CANVAS.send(player);
                    easel.playEffect(EaselEffect.USE_DENIED);
                }
                return;

            case SHIFT_RIGHT_CLICK:
                if (easel.hasItem()) {
                    ArtisticMaps.getArtDatabase().recycleMap(new Map(ItemUtils.getMapID(easel.getItem())));
                    easel.removeItem();
                }
                easel.breakEasel();
        }
    }

    private void mountCanvas(ItemStack itemInHand, Canvas canvas) {
        easel.mountCanvas(canvas);
        ItemStack removed = itemInHand.clone();
        removed.setAmount(1);
        player.getInventory().removeItem(removed);
    }

    public enum ClickType {
        LEFT_CLICK, RIGHT_CLICK, SHIFT_RIGHT_CLICK
    }
}
