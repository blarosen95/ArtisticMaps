package com.github.blarosen95.ArtisticMaps.Painting;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.Easel.Easel;
import com.github.blarosen95.ArtisticMaps.Event.PlayerMountEaselEvent;
import com.github.blarosen95.ArtisticMaps.IO.Database.Map;
import com.github.blarosen95.ArtisticMaps.Painting.Brushes.Dye;
import com.github.blarosen95.ArtisticMaps.Painting.Brushes.Fill;
import com.github.blarosen95.ArtisticMaps.Painting.Brushes.Flip;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtItem;
import com.github.blarosen95.ArtisticMaps.Utils.VersionHandler;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class ArtSession {
private final CanvasRenderer canvas;
    private final Brush DYE;
    private final Brush FILL;
    private final Brush FLIP;
    private final Easel easel;
    private final Map map;
    private Brush currentBrush;
    private long lastStroke;
    private ItemStack[] inventory;
    private boolean active = false;
    private boolean dirty = true;
	private int artkitPage = 0;

    ArtSession(Easel easel, Map map, int yawOffset) {
        this.easel = easel;
        canvas = new CanvasRenderer(map, yawOffset);
        currentBrush = null;
        lastStroke = System.currentTimeMillis();
        DYE = new Dye(canvas);
        FILL = new Fill(canvas);
        FLIP = new Flip(canvas);
        this.map = map;
    }

    boolean start(Player player) {
        PlayerMountEaselEvent event = new PlayerMountEaselEvent(player, easel);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return false;

        easel.seatUser(player);
        //Run tasks
        ArtisticMaps.getArtDatabase().restoreMap(map);
        ArtisticMaps.getScheduler().SYNC.runLater(() -> {
            if (player.getVehicle() != null) Lang.ActionBar.PAINTING.send(player);
        }, 30);
        if (ArtisticMaps.getConfiguration().FORCE_ART_KIT && player.hasPermission("artisticmaps.artkit")) {
            addKit(player);
        }
        map.setRenderer(canvas);
        persistMap(false);
        return true;
    }

    void paint(ItemStack brush, Brush.BrushAction action) {
        if (!dirty) dirty = true;
        if (currentBrush == null || !currentBrush.checkMaterial(brush)) {
            if (currentBrush != null) currentBrush.clean();
            currentBrush = getBrushType(brush);
        }
        if (currentBrush == null || canvas.isOffCanvas()) return;

        long currentTime = System.currentTimeMillis();
        long strokeTime = currentTime - lastStroke;
        if (strokeTime > currentBrush.getCooldown()) {
            currentBrush.paint(action, brush, strokeTime);
        }
        lastStroke = System.currentTimeMillis();
    }

    private Brush getBrushType(ItemStack item) {
        for (Brush brush : new Brush[]{DYE, FILL, FLIP}) {
            if (brush.checkMaterial(item)) {
                return brush;
            }
        }
        return null;
    }

    void updatePosition(float yaw, float pitch) {
        canvas.setYaw(yaw);
        canvas.setPitch(pitch);
    }

    private void addKit(Player player) {
        PlayerInventory inventory = player.getInventory();
        if (ArtisticMaps.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
            ItemStack leftOver = inventory.addItem(inventory.getItemInOffHand()).get(0);
            inventory.setItemInOffHand(new ItemStack(Material.AIR));
            if (leftOver != null && leftOver.getType() != Material.AIR)
                player.getWorld().dropItemNaturally(player.getLocation(), leftOver);
            this.inventory = inventory.getStorageContents().clone();
			inventory.setStorageContents(ArtItem.getArtKit(0));
        } else {
			this.inventory = inventory.getContents().clone();
			inventory.setContents(ArtItem.getArtKit(0));
        }
    }

	public void nextKitPage(Player player) {
		this.artkitPage++;
		this.setKitPage(player, this.artkitPage);
	}

	public void prevKitPage(Player player) {
		if (this.artkitPage > 0) {
			this.artkitPage--;
			this.setKitPage(player, this.artkitPage);
		}
	}

	/*
	 * Set the contents of the inventory without replacing the hotkey bar.
	 */
	private void setKitPage(Player player, int page) {
		ItemStack[] kit = ArtItem.getArtKit(this.artkitPage);
		if (ArtisticMaps.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
			ItemStack[] current = player.getInventory().getStorageContents();
			for (int i = 0; i < 9; i++) {
				kit[i] = current[i];
			}
			player.getInventory().setStorageContents(kit);
		} else {
			ItemStack[] current = player.getInventory().getContents();
			for (int i = 0; i < 9; i++) {
				kit[i] = current[i];
			}
			player.getInventory().setContents(kit);
		}
	}

	public boolean removeKit(Player player) {
        if (inventory != null) {
            if (ArtisticMaps.getBukkitVersion().getVersion() != VersionHandler.BukkitVersion.v1_8) {
                player.getInventory().setStorageContents(inventory);
                player.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
				inventory = null;
				return true;
            } else {
                player.getInventory().setContents(inventory);
				inventory = null;
				return true;
            }
        }
		return false;
    }

	/**
	 * @return True if the artsession has the artkit in use.
	 */
	public boolean isInArtKit() {
		return this.inventory != null;
	}

    public Easel getEasel() {
        return easel;
    }

    void end(Player player) {
        player.leaveVehicle();
        removeKit(player);
        easel.removeUser();
        canvas.stop();
        persistMap(true);
        active = false;
        //todo map renderer getting killed after save
    }

    public void persistMap(boolean resetRenderer) {
        if (!dirty) return; //no caching required
        byte[] mapData = canvas.getMap();
        map.setMap(mapData, resetRenderer);
        ArtisticMaps.getArtDatabase().cacheMap(this.map, mapData);
        dirty = false;
    }

    boolean isActive() {
        return active;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    void sendMap(Player player) {
        if (dirty) map.update(player);
    }
}
