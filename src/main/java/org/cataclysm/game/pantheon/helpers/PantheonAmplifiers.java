package org.cataclysm.game.pantheon.helpers;

import lombok.Getter;
import lombok.Setter;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;

@Getter @Setter
public class PantheonAmplifiers {
    private double bossDamageAmplifier = 1;
    private double playerDamageAmplifier = 1;

    private final PantheonOfCataclysm pantheon;

    public PantheonAmplifiers(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }
}
