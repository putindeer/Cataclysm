package org.cataclysm.game.pantheon.level.levels;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.structure.schematic.SchematicLoader;
import org.cataclysm.game.world.Dimensions;

@Getter
public enum PantheonZones {
    PANTHEON_ENTRANCE(new Location(Bukkit.getWorld("world_pale_void"), 1000, 140, 100)),
    TWISTED_CITY(new Location(Bukkit.getWorld("world_pantheon"), 500, 100, 500)),
    HYDRA_DUNGEON(new Location(Bukkit.getWorld("world_pantheon"), 1000, 100, 1000)),
    PALE_PALACE(new Location(Bukkit.getWorld("world_pantheon"), 1500, 100, 1500)),
    STORM_EYE(new Location(Bukkit.getWorld("world_pantheon"), 2000, 100, 2000)),
    FOUNTAIN(new Location(Bukkit.getWorld("world_pantheon"), 0, 100, 0))

    ;

    private final Location location;

    PantheonZones(Location location) {
        this.location = location;
    }

    public CataclysmArea getArena() {
        return new CataclysmArea(this.location, 300);
    }

    public SchematicLoader getSchemLoader() {
        String path = "pantheon/schematics/" + this.name().toUpperCase() + ".schem";
        return new SchematicLoader(path);
    }
}
