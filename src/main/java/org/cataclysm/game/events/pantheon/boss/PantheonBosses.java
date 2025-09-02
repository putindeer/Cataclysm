package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import org.cataclysm.game.events.pantheon.boss.the_cataclysm.TheCataclysm;
import org.cataclysm.game.events.pantheon.boss.twisted_warden.PantheonWarden;

@Getter
public enum PantheonBosses {
    THE_CATACLYSM(new TheCataclysm()),
    PRIMORDIAL_WARDEN(new PantheonWarden()),

    ;

    private final PantheonBoss instance;

    PantheonBosses(PantheonBoss boss) {this.instance = boss;}
}
