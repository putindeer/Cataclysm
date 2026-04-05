package org.cataclysm.game.events.pantheon.config.context;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PantheonContext {
    private int activeTime;
    private int deathCount;
    private int totemsUsed;
    private int mortalityLost;
}
