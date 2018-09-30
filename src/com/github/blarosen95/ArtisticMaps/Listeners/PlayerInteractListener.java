package com.github.blarosen95.ArtisticMaps.Listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryCreativeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Compatibility.CompatibilityManager;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Easel.Easel;
import com.github.blarosen95.ArtisticMaps.Easel.EaselEffect;
import com.github.blarosen95.ArtisticMaps.IO.MapArt;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import com.github.blarosen95.ArtisticMaps.Utils.ItemUtils;
import com.github.blarosen95.ArtisticMaps.Utils.LocationHelper;

class PlayerInteractListener implements RegisteredListener {

    private static BlockFace getFacing(Player player) {
        int yaw = ((int) player.getLocation().getYaw()) % 360;

        if (yaw < 0) {
            yaw += 360;
        }

        if (yaw >= 135 && yaw < 225) {
            return BlockFace.SOUTH;

        } else if (yaw >= 225 && yaw < 315) {
            return BlockFace.WEST;

        } else if (yaw >= 315 || yaw < 45) {
            return BlockFace.NORTH;

        } else {
            return BlockFace.EAST;

        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerInteractEvent(PlayerInteractEvent event) {

        if (ArtisticMaps.getPreviewManager().endPreview(event.getPlayer())) event.setCancelled(true);

        // Don't place paint brushes in the world
        if (ArtMaterial.getCraftItemType(event.getItem()) == ArtMaterial.PAINT_BRUSH) {
            if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(Lang.PAINTBRUSH_GROUND.get());
                return;
            }
        }

        if (!ArtMaterial.EASEL.isValidMaterial(event.getItem())
                || !event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            return;
        }
        event.setCancelled(true);

        if (!event.getBlockFace().equals(BlockFace.UP)) {
            return;
        }
        Player player = event.getPlayer();
        Location baseLocation = event.getClickedBlock().getLocation().clone().add(.5, 1.25, .5);
        Location easelLocation = event.getClickedBlock().getLocation().clone().add(0, 2, 0);
        CompatibilityManager compat = ArtisticMaps.getCompatManager();
        if (!player.hasPermission("artisticmaps.artist")
                || !compat.checkBuildAllowed(player, baseLocation)
                || !compat.checkBuildAllowed(player, easelLocation)) {
            Lang.ActionBar.NO_PERM_ACTION.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
            return;
        }
        BlockFace facing = getFacing(player);
        Location frameBlock = new LocationHelper(easelLocation).shiftTowards(facing);

        if (easelLocation.getBlock().getType() != Material.AIR
                || baseLocation.getBlock().getType() != Material.AIR
                || frameBlock.getBlock().getType() != Material.AIR
                || Easel.checkForEasel(easelLocation)) {
            Lang.ActionBar.INVALID_POS.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
            return;
        }
        Easel easel = Easel.spawnEasel(easelLocation, facing);

        // remove 1 easel from either hand
        removeEaselFromHandle(player);

        if (easel == null) {
            Lang.ActionBar.INVALID_POS.send(player);
            EaselEffect.USE_DENIED.playEffect(baseLocation);
        } else {
            EaselEffect.SPAWN.playEffect(new LocationHelper(baseLocation).shiftTowards(facing, .5));
        }
    }

    private void removeEaselFromHandle(Player player) {
        // check main hand
        if (ArtMaterial.EASEL.isValidMaterial(player.getInventory().getItemInMainHand())) {
            ItemStack items = player.getInventory().getItemInMainHand();
            if (items.getAmount() > 1) {
                items.setAmount(items.getAmount() - 1);
            } else {
                items = null;
            }
            player.getInventory().setItemInMainHand(items);
            // check off hand
        } else if (ArtMaterial.EASEL.isValidMaterial(player.getInventory().getItemInOffHand())) {
            ItemStack items = player.getInventory().getItemInOffHand();
            if (items.getAmount() > 1) {
                items.setAmount(items.getAmount() - 1);
            } else {
                items = null;
            }
            player.getInventory().setItemInOffHand(items);
        }
    }

    @EventHandler
    public void onInventoryCreativeEvent(final InventoryCreativeEvent event) {
        final ItemStack item = event.getCursor();

        if (event.getClick() != ClickType.CREATIVE || event.getClickedInventory() == null
                || item == null || item.getType() != Material.FILLED_MAP) {
            return;
        }

        ArtisticMaps.getScheduler().ASYNC.run(() -> {

            ItemMeta meta = item.getItemMeta();

            if (!meta.hasLore()) {

                MapArt art = ArtisticMaps.getArtDatabase().getArtwork(ItemUtils.getMapID(item));

                if (art != null) {

                    ItemStack correctLore = art.getMapItem();
                    event.getClickedInventory().setItem(event.getSlot(), correctLore);
                }
            }
        });
    }

    @SuppressWarnings("static-access")
    @Override
    public void unregister() {
        PlayerInteractEvent.getHandlerList().unregister(this);
        InventoryCreativeEvent.getHandlerList().unregister(this);
    }
}