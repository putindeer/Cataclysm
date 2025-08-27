package org.cataclysm.game.pantheon.level;

import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;

public enum PantheonAreas {

    ;

    private final CataclysmArea area;

    PantheonAreas(Location center, int radius) {
        this.area = new CataclysmArea(center, radius);
    }

}
