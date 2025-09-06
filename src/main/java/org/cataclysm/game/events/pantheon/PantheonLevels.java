package org.cataclysm.game.events.pantheon;

import lombok.Getter;
import org.bukkit.Location;
import org.cataclysm.game.world.Dimensions;

@Getter
public enum PantheonLevels {
    PALE_TREE(Dimensions.PALE_VOID.getWorld().getSpawnLocation()),
    PANTHEON_ENTRANCE(new Location(Dimensions.PALE_VOID.getWorld(), 1000.5, 140, 1000.5)),
    PANTHEON_FOUNTAIN(new Location(Dimensions.PANTHEON.getWorld(), 0, 1, 0)),
    TWISTED_CITY(new Location(Dimensions.PANTHEON.getWorld(), -500, 1, 0)),
    HYDRAS_DUNGEON(new Location(Dimensions.PANTHEON.getWorld(), 500, 1, 0)),
    PALE_HEART(new Location(Dimensions.PANTHEON.getWorld(), 0, 1, -500)),
    STORMS_EYE(new Location(Dimensions.PANTHEON.getWorld(), 0, 1, 500)),

    ;

    private final Location location;

    PantheonLevels(Location location) {this.location = location;}
}