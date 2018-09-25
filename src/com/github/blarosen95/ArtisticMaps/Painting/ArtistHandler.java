package com.github.blarosen95.ArtisticMaps.Painting;

import com.github.blarosen95.ArtisticMaps.ArtisticMaps;
import com.github.blarosen95.ArtisticMaps.Config.Lang;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.In.Packet.ArtistPacket;
import com.github.blarosen95.ArtisticMaps.IO.Protocol.In.Packet.PacketType;
import com.github.blarosen95.ArtisticMaps.Recipe.PaintBrushItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArtistHandler {
    //TODO: better way of getting custom items' meta than below
    public PaintBrushItem paintBrushItem = new PaintBrushItem();

    private final ConcurrentHashMap<UUID, ArtSession> artists;

    public ArtistHandler() {
        artists = new ConcurrentHashMap<>();
    }

    public boolean handlePacket(Player sender, ArtistPacket packet) {
        if (packet == null) {
            return true;
        }
        if (artists.containsKey(sender.getUniqueId())) {
            ArtSession session = artists.get(sender.getUniqueId());
            PacketType type = packet.getType();

            if (type == PacketType.LOOK) {
                ArtistPacket.PacketLook packetLook = (ArtistPacket.PacketLook) packet;
                session.updatePosition(packetLook.getYaw(), packetLook.getPitch());
                return true;
                //Handle the save brush
            } else if (type == PacketType.INTERACT && sender.getInventory().getItemInMainHand().getItemMeta().equals(paintBrushItem.paintBrushMeta)) {
                //Handle clicks with paint brush saving art
                if (sender.isInsideVehicle() && ArtisticMaps.getArtistHandler().containsPlayer(sender)) {
                    ArtisticMaps.getScheduler().ASYNC.run(() -> {
                      new AnvilGUI(ArtisticMaps.instance(), sender, "Title?", (player, title) -> {
                          TitleFilter filter = new TitleFilter(Lang.Filter.ILLEGAL_EXPRESSIONS.get());
                          if (!filter.check(title)) {
                              player.sendMessage(Lang.BAD_TITLE.get());
                              return null;
                          }
                          Easel easel = session.getEasel();
                          ArtisticMaps.getScheduler().SYNC.run(() -> {
                              easel.playEffect(EaselEffect.SAVE_ARTWORK);
                              Canvas canvas = Canvas.getCanvas(easel.getItem());
                              MapArt art1 = ArtisticMaps.getArtDatabase().saveArtwork(canvas, title, player);
                              if (art1 != null) {
                                  ArtisticMaps.getArtistHandler().removePlayer(player);
                                  easel.setItem(new ItemStack(Material.AIR));
                                  ItemUtils.giveItem(player, art1.getMapItem());
                                  player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_SUCCESS.get(), title));
                              } else {
                                  player.sendMessage(String.format(Lang.PREFIX + Lang.SAVE_FAILURE.get(), title));
                              }
                          });
                          return null;
                      });
                    });
                }
                return false;
            } else if (type == PacketType.INTERACT) {
                ArtistPacket.PacketInteract.InteractType click = ((ArtistPacket.PacketInteract) packet).getInteraction();
                session.paint(sender.getInventory().getItemInMainHand(),
                        (click == ArtistPacket.PacketInteract.InteractType.ATTACK) ? BrushAction.LEFT_CLICK : BrushAction.RIGHT_CLICK);
                session.sendMap(sender);
                return false;
            }
        } else {
            removePlayer(sender);
        }
        return true;
    }

    public synchronized void addPlayer(final Player player, Easel easel, Map map, int yawOffset) {
        ArtSession session = new ArtSession(easel, map, yawOffset);
        if (session.start(player) && ArtisticMaps.getProtocolManager().PACKET_RECEIVER.injectPlayer(player)) {
            artists.put(player.getUniqueId(), session);
            session.setActive(true);
        }
    }

    public Easel getEasel(Player player) {
        if (artists.containsKey(player.getUniqueId())) {
            return artists.get(player.getUniqueId()).getEasel();
        }
        return null;
    }

    public boolean containsPlayer(Player player) {
        return (artists.containsKey(player.getUniqueId()));
    }

    public boolean containsPlayer(UUID player) {
        return artists.containsKey(player)
    }

    public synchronized void removePlayer(final Player player) {
        if (!containsPlayer(player))
            return;
        ArtSession session = artists.get(player.getUniqueId());
        if (!session.isActive())
            return;
        artists.remove(player.getUniqueId());
        session.end(player);
        ArtisticMaps.getProtocolManager().PACKET_RECEIVER.uninjectPlayer(player);
    }

    public ArtSession getCurrentSession(Player player) {
        return artists.get(player.getUniqueId());
    }

    public ArtSession getCurrentSession(UUID player) {
        return artists.get(player);
    }

    private synchronized void clearPlayers() {
        for (UUID uuid : artists.keySet()) {
            removePlayer(Bukkit.getPlayer(uuid));
        }
    }

    public Set<UUID> getArtists() {
        return artists.keySet();
    }

    public void stop() {
        clearPlayers();
        ArtisticMaps.getProtocolManager().PACKET_RECEIVER.close();
    }
}
