package org.cataclysm.game.events.raids.structures.pale_palace;

import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.world.Dimensions;

public class PalePalace extends RaidStructure {
    public PalePalace() {
        super("THE_ABYSS");
    }

    @Override
    public CataclysmArea getArea() {
        return new CataclysmArea(new Location(Dimensions.THE_END.createWorld(), -145, 20, -648), 200);
    }

    @Override
    public CataclysmArea getBossArena() {
        return new CataclysmArea(new Location(Dimensions.THE_END.createWorld(), -145, -58, -648), 65);
    }
}
