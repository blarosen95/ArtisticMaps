package com.github.blarosen95.ArtisticMaps.Easel;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Recipe.ArtMaterial;
import com.github.blarosen95.ArtisticMaps.Utils.ChunkLocation;
import com.github.blarosen95.ArtisticMaps.Utils.LocationHelper;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

public class Easel {

    private final Location location;
    private final ChunkLocation chunk;
    private final WeakEntity<ArmorStand> stand = new WeakEntity<>(EaselPart.STAND);
    private final WeakEntity<ItemFrame> frame = new WeakEntity<>(EaselPart.FRAME);
    private final WeakEntity<ArmorStand> seat = new WeakEntity<>(EaselPart.SEAT);
    private final WeakEntity<ArmorStand> marker = new WeakEntity<>(EaselPart.MARKER);
    private final AtomicBoolean spawned;
    private UUID user;

    private Easel(Location location, boolean hasBeenSpawned) {
        this.location = location;
        this.chunk = new ChunkLocation(location.getChunk());
        user = null;
        spawned = new AtomicBoolean(hasBeenSpawned);
    }

    /**
     * Attempts to spawn an easel at the location provided, facing the direction provided.
     *
     * @param location The location where the easel will be spawned.
     * @param facing   The direction the easel will face. Valid directions are NORTH, SOUTH, EAST and WEST.
     * @return A reference to the spawned easel if it was spawned successfully, or null if the area is obstructed.
     */
    public static Easel spawnEasel(Location location, BlockFace facing) {
        Easel easel = new Easel(location, false);
        easel.place(location, facing);

        if (easel.exists()) {
            ArtisticMaps.getEasels().put(easel);
            easel.spawned.set(true);
            return easel;
        } else {
            easel.breakEasel();
            return null;
        }
    }

    /**
     * Attempts to get an easel from one of its parts.
     *
     * @param partLocation The location of the easel part.
     * @param part         The easel part being used to find an easel.
     * @return A reference to the part's easel, or null if none can be found.
     */
    public static Easel getEasel(Location partLocation, EaselPart part) {
        Location easelLocation = part.getEaselPos(partLocation, EaselPart.getFacing(partLocation.getYaw()));

        if (ArtisticMaps.getEasels().contains(easelLocation)) {
            return ArtisticMaps.getEasels().get(easelLocation);
        } else {
            Easel easel = new Easel(easelLocation, true);
            easel.spawned.set(true);
            if (easel.hasSign() && easel.exists()) {
                ArtisticMaps.getEasels().put(easel);
                easel.spawned.set(true);
                return easel;
            }
        }
        return null;
    }

    /**
     * Attempts to find an easel at the location provided.
     *
     * @param location The location at which to check for an easel.
     * @return True if an easel spawned at this location, or false if not.
     */
    public static boolean checkForEasel(Location location) {
        if (new Easel(location, true).exists()) {
            return true;
        } else {

            if (ArtisticMaps.getEasels().contains(location)) {
                ArtisticMaps.getEasels().remove(location);
            }
            return false;
        }
    }

    private void place(Location location, BlockFace facing) {
        if (exists()) breakEasel();
        EaselPart.SIGN.spawn(location, facing);
        stand.spawn(location, facing);
        frame.spawn(location, facing);
        spawned.set(true);
    }

    private boolean exists() {
        if (!spawned.get()) return false;
        Collection<Entity> entities = getNearbyEntities();
        if (stand.exists(entities) && frame.exists(entities)) {
            return true;
        } else {
            spawned.set(false);
            return false;
        }
    }

    private boolean hasSign() {
        BlockState state = location.getBlock().getState();
        return (location.getBlock().getType() == Material.WALL_SIGN
                && state instanceof Sign
                && ((Sign) state).getLine(3).equals(EaselPart.ARBITRARY_SIGN_ID));
    }

    /**
     * Mounts a canvas on the easel, with an id defined by the canvas provided.
     *
     * @param canvas The canvas that will be placed on the easel.
     */
    public void mountCanvas(Canvas canvas) {
        if (getItem() != null) removeItem();
        setItem(canvas.getEaselItem());
        EaselEffect.MOUNT_CANVAS.playEffect(getCentreLocation());
    }

    /**
     * Removes the current item mounted on the easel.
     * If the item is an unsaved canvas, a canvas will be dropped at the easel.
     * If the item is an edited artwork, a copy of the original artwork wil be dropped.
     */
    public void removeItem() {
        ItemStack item = getItem().clone();
        Canvas canvas = Canvas.getCanvas(item);

        setItem(new ItemStack(Material.AIR));
        if (canvas != null) {
            location.getWorld().dropItem(location, canvas.getDroppedItem());
        } else {
            if (item != null && item.getType() != Material.AIR)
                location.getWorld().dropItemNaturally(location, item);
        }
    }

    /**
     * Breaks the easel, dropping it along with any mounted items.
     */
    public void breakEasel() {
        if (!spawned.getAndSet(false)) return;
        ArtisticMaps.getEasels().remove(location);
        final Collection<Entity> entities = getNearbyEntities();

        ArtisticMaps.getScheduler().SYNC.run(() -> {
            location.getBlock().setType(Material.AIR);
            EaselEffect.BREAK.playEffect(getCentreLocation());
            if (stand.remove(entities)) location.getWorld().dropItemNaturally(location, ArtMaterial.EASEL.getItem());
            if (frame.exists(entities)) {
                removeItem();
                frame.remove(entities);
            }
        });
    }

    /**
     * @return The direction this easel is facing.
     */
    public BlockFace getFacing() {
        ItemFrame frame = this.frame.get();
        return (frame != null) ? frame.getFacing() : null;
    }

    /**
     * Plays an effect at the easel.
     *
     * @param interactionType the type of interaction.
     */
    public void playEffect(EaselEffect interactionType) {
        interactionType.playEffect(getCentreLocation());
    }

    private Location getCentreLocation() {
        Location location = this.location.clone();
        BlockFace facing = getFacing();
        return (facing == null) ? location : new LocationHelper(location).center().shiftTowards(getFacing(), 0.65);
    }

    public Location getLocation() {
        return location;
    }

    public boolean seatUser(Player user) {
        ArmorStand seat = this.seat.spawn(location, getFacing());
        ArmorStand marker = this.marker.spawn(location, getFacing());

        if (seat == null || marker == null) return false;
        Location location = user.getEyeLocation();
        EaselEffect.START_RIDING.playEffect(location);
        seat.addPassenger(user);
        if (seat.getPassengers() == null || seat.getPassengers().contains(user)) {
            return false;
        }

        this.user = user.getUniqueId();
        return true;
    }

    public boolean isBeingUsed() {
        return getUser() != null;
    }

    private UUID getUser() {
        if (user == null) return null;
        if (!ArtisticMaps.getArtistHandler().containsPlayer(user)) removeUser();
        return user;
    }

    public void removeUser() {
        Collection<Entity> entities = getNearbyEntities();
        seat.remove(entities);
        marker.remove(entities);
        this.user = null;
        playEffect(EaselEffect.STOP_RIDING);
    }

    private Collection<Entity> getNearbyEntities() {
        return location.getWorld().getNearbyEntities(location, 2, 2, 2);
    }

    /**
     * @return The item currently mounted on the easel, or null if there is none.
     */
    public ItemStack getItem() {
        return (frame.get() != null) ? frame.get().getItem() : new ItemStack(Material.AIR);
    }

    public void setItem(ItemStack itemStack) {
        frame.get().setItem(itemStack);
    }

    /**
     * @return True if an item is currently mounted on the easel.
     */
    public boolean hasItem() {
        return frame.get() != null && frame.get().getItem().getType() != Material.AIR;
    }

    public ChunkLocation getChunk() {
        return chunk;
    }

    class WeakEntity<T extends Entity> {
        private final EaselPart type;
        private WeakReference<T> entityRef;

        WeakEntity(EaselPart type) {
            this.type = type;
            entityRef = new WeakReference<>(null);
        }

        WeakEntity(EaselPart type, T entity) {
            this.type = type;
            this.entityRef = new WeakReference<>(entity);
        }

        T spawn(Location location, BlockFace facing) {
            if (exists(getNearbyEntities())) remove();
            T entity = (T) type.spawn(location, facing);
            entityRef = new WeakReference<>(entity);
            return entity;
        }

        boolean remove(Collection<Entity> entities) {
            Entity entity = get(entities);
            if (entity != null && entity.isValid()) entity.remove();
            else return false;
            entityRef = new WeakReference<T>(null);
            return true;
        }

        boolean remove() {
            return remove(getNearbyEntities());
        }

        boolean exists(Collection<Entity> entities) {
            return get(entities) != null;
        }

        T get(Collection<Entity> entities) {
            T entity = entityRef.get();
            if (entity != null && entity.isValid()) {
                return entity;
            }
            if (entities == null) entities = getNearbyEntities();

            for (Entity e : entities) {
                if (EaselPart.getPartType(e) == type) {
                    BlockFace facing = EaselPart.getFacing(e.getLocation().getYaw());
                    if (type.getEaselPos(e.getLocation(), facing).equals(location)) {
                        entityRef = new WeakReference<>((T) e);
                        return entityRef.get();
                    }
                }
            }
            return null;
        }

        T get() {
            return get(getNearbyEntities());
        }
    }
}
