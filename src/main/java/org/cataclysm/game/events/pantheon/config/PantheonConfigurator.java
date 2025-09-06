package org.cataclysm.game.events.pantheon.config;

import lombok.Getter;
import lombok.Setter;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.config.context.PantheonContext;
import org.cataclysm.game.events.pantheon.config.player.ProfileRegistry;

@Getter @Setter
public class PantheonConfigurator {
    private final PantheonRegulator regulator;
    private final ProfileRegistry registry;
    private final PantheonContext context;

    private final PantheonOfCataclysm pantheon;

    public PantheonConfigurator(PantheonOfCataclysm pantheon) {
        this.regulator = new PantheonRegulator();
        this.registry = new ProfileRegistry();
        this.context = new PantheonContext();
        this.pantheon = pantheon;
    }

    public void save() {
        this.registry.saveAll();
    }
}
