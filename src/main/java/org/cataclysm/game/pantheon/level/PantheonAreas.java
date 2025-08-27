package org.cataclysm.game.pantheon.level;

import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;

public enum PantheonAreas {

    PANTHEON_ENTRANCE(new Location(-145, 20, -648), 50),
    WARDEN_ARENA(new Location())

    ;

    private final CataclysmArea area;

    PantheonAreas(Location center, int radius) {
        this.area = new CataclysmArea(center, radius);
    }

}
