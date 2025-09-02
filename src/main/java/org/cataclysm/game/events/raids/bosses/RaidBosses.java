package org.cataclysm.game.events.raids.bosses;

import lombok.Getter;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.CalamityHydra;
import org.cataclysm.game.events.raids.bosses.pale_king.PaleKing;
import org.cataclysm.game.events.raids.bosses.twisted_warden.TwistedWarden;

@Getter
public enum RaidBosses {
    TWISTED_WARDEN(new TwistedWarden("Twisted Warden", 15000)),
    CALAMITY_HYDRA(new CalamityHydra("Calamity Hydra", 30000)),
    PALE_KING(new PaleKing("Pale King", 20000)),

    ;

    private final CataclysmBoss manager;

    RaidBosses(CataclysmBoss manager) {
        this.manager = manager;
    }
}
