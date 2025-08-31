package org.cataclysm.game.events.raids.bosses.twisted_warden.keys;

import lombok.Getter;

@Getter
public enum TwistedWardenKeys {
    BOOSTED_KEY("BOOSTED"),
    NIGHTMARE_KEY("NIGHTMARE"),
    DRAIN_KEY("DRAIN")

    ;

    private final String key;

    TwistedWardenKeys(String key) {
        this.key = key;
    }
}
