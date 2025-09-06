package org.cataclysm.game.events.pantheon.config.context;

import lombok.Getter;
import lombok.Setter;
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;

import java.util.HashMap;

@Getter @Setter
public class PantheonContext {
    private int activeTime;
    private int deathCount;
    private int totemsUsed;
    private int mortalityLost;
}
