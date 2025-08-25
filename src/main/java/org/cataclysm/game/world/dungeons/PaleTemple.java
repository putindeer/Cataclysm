package org.cataclysm.game.world.dungeons;

import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.data.StructureLevel;

public class PaleTemple extends CataclysmStructure {

    public PaleTemple(StructureLevel level) {
        super(level);
        super.setUp();
    }

    public PaleTemple() {
        super("PALE_TEMPLE");
    }

    @Override
    public String getAdvancement() {
        return "the_beginning/stone_and_divinity";
    }
}
