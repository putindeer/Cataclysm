package org.cataclysm.game.pantheon.world;

import lombok.Getter;
import org.bukkit.Location;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.world.Dimensions;
import org.jetbrains.annotations.NotNull;

@Getter
public enum PantheonLocations {

    PANTHEON_ENTRANCE(new Location(Dimensions.PALE_VOID.getWorld(), 700, 140, 700), 55),
    WARDEN_ARENA(new Location(Cataclysm.getPantheon().getWorld(), 0, 70, 0), 40),

    ;

    private final CataclysmArea area;

    PantheonLocations(Location center, int radius) {
        this.area = new CataclysmArea(center, radius);
    }

    public @NotNull Location getCoreLocation() {
        return area.center().getBlock().getLocation().clone().add(.5, 0, .5);
    }
}
