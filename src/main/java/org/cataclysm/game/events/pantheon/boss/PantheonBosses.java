package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import org.cataclysm.game.events.pantheon.boss.ragnarok.TheRagnarok;

@Getter
public enum PantheonBosses {

    THE_RAGNAROK(new TheRagnarok())

    ;

    private final PantheonBoss instance;

    PantheonBosses(PantheonBoss boss) {this.instance = boss;}
}
