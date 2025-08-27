package org.cataclysm.game.raids.structures.pantheon;

import org.bukkit.Location;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.world.Dimensions;

public class PantheonOfCataclysm extends RaidStructure {
    public PantheonOfCataclysm() {
        super("Pantheon Of Cataclysm");
    }

    @Override
    public CataclysmArea getArea() {
        return new CataclysmArea(new Location(Dimensions.PALE_VOID.getWorld(), -145, 20, -648), 200);
    }

    @Override
    public CataclysmArea getBossArena() {
        return null;
    }
}
