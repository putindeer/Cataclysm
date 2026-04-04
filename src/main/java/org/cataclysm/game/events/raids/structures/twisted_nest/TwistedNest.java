package org.cataclysm.game.events.raids.structures.twisted_nest;

import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.world.Dimensions;

public class TwistedNest extends RaidStructure {
    public TwistedNest() {
        super("TWISTED_NEST");
    }

    @Override
    public CataclysmArea getArea() {
        return new CataclysmArea(new Location(Dimensions.OVERWORLD.createWorld(), 1344.5, -58, 9784), 70);
    }

    @Override
    public CataclysmArea getBossArena() {
        return getArea();
    }
}
