package com.github.blarosen95.ArtisticMaps.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.inventory.ItemStack;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Color.ArtDye;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Event.PlayerCraftArtMaterialEvent;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtItem;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;

public class PlayerCraftListener implements RegisteredListener {

    @EventHandler
    public void onPlayerCraftEvent(CraftItemEvent event) {
        ItemStack result = event.getCurrentItem();
        //Prevent ArtisticMap maps from being copied like regular maps
        if (result.getType() == Material.FILLED_MAP) {
            MapArt art = ArtisticMaps.getArtDatabase().getArtwork(ItemUtils.getMapID(result));
            if (art != null) {
                if (event.getWhoClicked().getUniqueId().equals(art.getArtistPlayer().getUniqueId())) {
                    Player player = (Player) event.getWhoClicked();
                    ItemStack artworkItem = art.getMapItem();
                    if (event.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                        onShiftClick(artworkItem, player, event);
                    } else {
                        result.setItemMeta(artworkItem.getItemMeta());
                    }
                } else {
                    Lang.NO_CRAFT_PERM.send(event.getWhoClicked());
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                }
            }
            //Return old dye from paint bucket
        } else if (ArtMaterial.PAINT_BUCKET.isValidMaterial(result)) {
            PlayerCraftArtMaterialEvent craftEvent = new PlayerCraftArtMaterialEvent(event, ArtMaterial.PAINT_BUCKET);
            Bukkit.getPluginManager().callEvent(craftEvent);
            if (craftEvent.isCancelled()) return;
            boolean kitItem = false;

            //Tag any results from crafts involving ArtKit items
            for (ItemStack ingredient : event.getInventory().getMatrix()) {
                if (ItemUtils.hasKey(ingredient, ArtItem.KIT_KEY)) {
                    kitItem = true;
                    break;
                }
            }
            boolean craftSuccess = true;
            if (event.getClick() == ClickType.NUMBER_KEY) {
                ItemStack slot = event.getWhoClicked().getInventory().getContents()[event.getHotbarButton()];
                craftSuccess = (slot == null || slot.getType() == Material.AIR);
            }
            if (craftSuccess) {
                for (ItemStack ingredient : event.getInventory().getMatrix()) {
                    if (ArtMaterial.PAINT_BUCKET.isValidMaterial(ingredient)) {
                        ArtDye dye = ArtItem.DyeBucket.getColor(ingredient);
                        if (dye == null) continue;
                        ItemStack previousDye = dye.toItem();
                        if (kitItem) {
                            ItemUtils.addKey(previousDye, ArtItem.KIT_KEY);
                        }
                        ItemUtils.giveItem((Player) event.getWhoClicked(), previousDye);
                    }
                }
            }
            if (kitItem) {
                ItemStack newResult = ItemUtils.addKey(result, ArtItem.KIT_KEY);
                event.setCurrentItem(newResult);
            }
        }
    }

    private void onShiftClick(ItemStack artworkItem, Player player, CraftItemEvent event) {
        event.setCancelled(true);

        int i = 0;
        ItemStack[] items = event.getInventory().getMatrix();
        for (ItemStack item : items) {

            if (item != null) {
                i += item.getAmount();
            }
        }
        event.getInventory().setMatrix(new ItemStack[items.length]);
        artworkItem.setAmount(i);
        ItemUtils.giveItem(player, artworkItem);
    }

    @Override
    public void unregister() {
        CraftItemEvent.getHandlerList().unregister(this);
    }
}
