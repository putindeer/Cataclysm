package org.cataclysm.game.events.pantheon;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.cataclysm.game.world.Dimensions;

@Getter
public enum PantheonZones {
    PANTHEON_ENTRANCE(new Location(Dimensions.PALE_VOID.getWorld(), 1000, 140, 1000)),
    PANTHEON_FOUNTAIN(new Location(Dimensions.PANTHEON.getWorld(), 0, 1, 0)),
    TWISTED_CITY(new Location(Dimensions.PANTHEON.getWorld(), -500, 1, 0)),
    HYDRAS_DUNGEON(new Location(Dimensions.PANTHEON.getWorld(), 500, 1, 0)),
    PALE_HEART(new Location(Dimensions.PANTHEON.getWorld(), 0, 1, -500)),
    STORMS_EYE(new Location(Dimensions.PANTHEON.getWorld(), 0, 1, 500)),

    ;

    private final Location location;

    PantheonZones(Location location) {this.location = location;}

    public static World getPantheonWorld() {
        return Dimensions.PANTHEON.createWorld().getSpawnLocation().getWorld();
    }
}