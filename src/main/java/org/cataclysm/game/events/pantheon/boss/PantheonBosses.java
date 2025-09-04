package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import org.cataclysm.game.events.pantheon.boss.custom.exclusives.the_cataclysm.TheCataclysm;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.PantheonWarden;

@Getter
public enum PantheonBosses {
    TWISTED_WARDEN(new PantheonWarden()),
    CALAMITY_HYDRA(new PantheonHydra()),
    THE_CATACLYSM(new TheCataclysm()),

    ;

    private final PantheonBoss instance;

    PantheonBosses(PantheonBoss boss) {this.instance = boss;}
}
