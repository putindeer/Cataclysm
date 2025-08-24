package org.cataclysm.game.raids.structures;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.structure.raid.RaidStructure;
import org.cataclysm.game.raids.structures.mother.Mother;
import org.cataclysm.game.raids.structures.the_abyss.TheAbyss;
import org.cataclysm.game.raids.structures.twisted_nest.TwistedNest;

@Getter
public enum RaidStructures {
    TWISTED_NEST(new TwistedNest()),
    MOTHER(new Mother()),
    THE_ABYSS(new TheAbyss()),

    ;

    private final RaidStructure structure;

    RaidStructures(RaidStructure raidStructure) {
        this.structure = raidStructure;
    }
}
