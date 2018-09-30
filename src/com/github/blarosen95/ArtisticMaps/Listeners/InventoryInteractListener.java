package com.github.blarosen95.ArtisticMaps.Listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtItem;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;

public class InventoryInteractListener implements RegisteredListener {

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        checkPreviewing(event.getPlayer(), event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        checkPreviewing(((Player) event.getWhoClicked()), event);
        checkSlotClicked((Player) event.getWhoClicked(), event);
        checkArtKitPagination(((Player) event.getWhoClicked()), event.getCurrentItem(), event);
    }

    @EventHandler
    public void onPlayDropItem(PlayerDropItemEvent event) {
        if (ArtisticMaps.getPreviewManager().endPreview(event.getPlayer())) event.getItemDrop().remove();
        if (isKitDrop(event.getPlayer(), event.getItemDrop().getItemStack(), event)) {
            event.getItemDrop().remove();
        }
    }

    private void checkPreviewing(Player player, Cancellable event) {
        if (ArtisticMaps.getPreviewManager().endPreview(player)) event.setCancelled(true);
    }

    private void checkArtKitPagination(Player player, ItemStack itemStack, Cancellable event) {
        if (ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            if (ItemUtils.hasKey(itemStack, "Artkit:Next")) {
                event.setCancelled(true);
                ArtisticMaps.getArtistHandler().getCurrentSession(player).nextKitPage(player);
            }
            if (ItemUtils.hasKey(itemStack, "Artkit:Back")) {
                event.setCancelled(true);
                ArtisticMaps.getArtistHandler().getCurrentSession(player).prevKitPage(player);
            }
        }
    }

    private void checkSlotClicked(Player player, InventoryClickEvent event) {
        //If player is in an ArtKit session
        if (ArtisticMaps.getArtistHandler().containsPlayer(player) && ArtisticMaps.getArtistHandler().getCurrentSession(player).isInArtKit()) {
            //If player is attempting to interact with non-inventory slot then cancel
            if (event.getSlot() > 35 || event.getSlot() < 0) {
                event.setResult(Event.Result.DENY);
                event.setCancelled(true);
            }
        }
    }

    private boolean isKitDrop(Player player, ItemStack itemStack, Cancellable event) {
        if (ArtisticMaps.getArtistHandler().containsPlayer(player)) {
            return ItemUtils.hasKey(itemStack, ArtItem.KIT_KEY) || ItemUtils.hasKey(itemStack, "Artkit:Next") || ItemUtils.hasKey(itemStack, "Artkit:Back");
        }
        return false;
    }

    @Override
    public void unregister() {
        PlayerItemHeldEvent.getHandlerList().unregister(this);
        InventoryClickEvent.getHandlerList().unregister(this);
        PlayerDropItemEvent.getHandlerList().unregister(this);
    }

}
