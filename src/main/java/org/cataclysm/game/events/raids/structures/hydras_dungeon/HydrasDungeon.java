package org.cataclysm.game.events.raids.structures.hydras_dungeon;

import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.world.Dimensions;

public class HydrasDungeon extends RaidStructure {
    public HydrasDungeon() {
        super("HYDRAS_DUNGEON");
    }

    @Override
    public CataclysmArea getArea() {
        return new CataclysmArea(Dimensions.THE_END.getWorld().getSpawnLocation(), 170);
    }

    @Override
    public CataclysmArea getBossArena() {
        return getArea();
    }
}