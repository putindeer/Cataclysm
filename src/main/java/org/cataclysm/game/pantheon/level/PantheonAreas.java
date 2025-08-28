package org.cataclysm.game.pantheon.level;

import lombok.Getter;
import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.world.Dimensions;
import org.jetbrains.annotations.NotNull;

@Getter
public enum PantheonAreas {

    PANTHEON_ENTRANCE(new Location(Dimensions.PALE_VOID.getWorld(), 700, 140, 700), 55)

    ;

    private final CataclysmArea area;

    PantheonAreas(Location center, int radius) {
        this.area = new CataclysmArea(center, radius);
    }

    public @NotNull Location getCoreLocation() {
        return area.center().getBlock().getLocation().clone().add(.5, 0, .5);
    }
}
