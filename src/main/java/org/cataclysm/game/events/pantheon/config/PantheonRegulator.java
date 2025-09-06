package org.cataclysm.game.events.pantheon.config;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class PantheonRegulator {
    private double playerResistance;
    private double bossResistance;

    public PantheonRegulator() {
        this.playerResistance = 1.0F;
        this.bossResistance = 1.0F;
    }
}
