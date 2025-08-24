package org.cataclysm.game.raids.structures.the_abyss;

import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.world.Dimensions;

public class TheAbyss extends RaidStructure {
    public TheAbyss() {
        super("THE_ABYSS");
    }

    @Override
    public CataclysmArea getArea() {
        return new CataclysmArea(new Location(Dimensions.THE_END.getWorld(), -145, 20, -648), 200);
    }

    @Override
    public CataclysmArea getBossArena() {
        return new CataclysmArea(new Location(Dimensions.THE_END.getWorld(), -145, -58, -648), 65);
    }
}
