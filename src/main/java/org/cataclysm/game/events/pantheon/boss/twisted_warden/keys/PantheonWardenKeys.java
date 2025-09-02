package org.cataclysm.game.events.pantheon.boss.twisted_warden.keys;

import lombok.Getter;

@Getter
public enum PantheonWardenKeys {
    BOOSTED_KEY("BOOSTED"),
    NIGHTMARE_KEY("NIGHTMARE"),
    DRAIN_KEY("DRAIN")

    ;

    private final String key;

    PantheonWardenKeys(String key) {
        this.key = key;
    }
}
