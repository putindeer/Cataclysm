package org.cataclysm.game.events.raids.structures;

import lombok.Getter;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.events.raids.structures.hydras_dungeon.HydrasDungeon;
import org.cataclysm.game.events.raids.structures.mother.Mother;
import org.cataclysm.game.events.raids.structures.pale_palace.PalePalace;
import org.cataclysm.game.events.raids.structures.twisted_nest.TwistedNest;

@Getter
public enum RaidStructures {
    TWISTED_NEST(new TwistedNest()),
    HYDRAS_DUNGEON(new HydrasDungeon()),
    MOTHER(new Mother()),
    PALE_PALACE(new PalePalace()),

    ;

    private final RaidStructure structure;

    RaidStructures(RaidStructure raidStructure) {
        this.structure = raidStructure;
    }
}
