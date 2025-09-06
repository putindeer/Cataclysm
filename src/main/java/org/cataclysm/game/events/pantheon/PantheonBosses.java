package org.cataclysm.game.events.pantheon;

import lombok.Getter;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.bosses.twisted_warden.PantheonWarden;

@Getter
public enum PantheonBosses {
    TWISTED_WARDEN(new PantheonWarden()),
    CALAMITY_HYDRA(new PantheonHydra()),
    VOID_LORD(new VoidLord()),
    THE_CATACLYSM(new TheCataclysm()),

    ;

    private final PantheonBoss instance;

    PantheonBosses(PantheonBoss boss) {this.instance = boss;}
}
