package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import org.cataclysm.game.events.pantheon.boss.the_cataclysm.TheCataclysm;

@Getter
public enum PantheonBosses {
    THE_CATACLYSM(new TheCataclysm())

    ;

    private final PantheonBoss instance;

    PantheonBosses(PantheonBoss boss) {this.instance = boss;}
}
