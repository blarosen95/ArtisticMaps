package com.github.blarosen95.ArtisticMaps.Compatibility;

import com.github.blarosen95.ArtisticMaps.Easel.EaselEvent;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class GriefPreventionCompat implements RegionHandler {

    private final boolean loaded;

    public GriefPreventionCompat() {
        GriefPrevention.instance.getName();
        loaded = true;
    }

    @Override
    public boolean checkBuildAllowed(Player player, Location location) {
        return (GriefPrevention.instance.allowBuild(player, location) == null); //GriefPrevention is weird here, allowBuild() returning null should really be "return true"
    }

    @Override
    public boolean checkInteractAllowed(Player player, Entity entity, EaselEvent.ClickType click) {
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(entity.getLocation(), false, null);
        return (claim.allowAccess(player) == null); //Same un-Java sort of returned data typing...
    }

    @Override
    public boolean isLoaded() {
        return loaded;
    }
}
