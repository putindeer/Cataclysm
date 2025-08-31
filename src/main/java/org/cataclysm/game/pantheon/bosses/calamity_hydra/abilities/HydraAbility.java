package org.cataclysm.game.pantheon.bosses.calamity_hydra.abilities;

import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.PantheonHydra;

public abstract class HydraAbility extends Ability {
    public final PantheonHydra hydra;

    public HydraAbility(PantheonHydra hydra, Material triggerMaterial, String name, int channelTime, double cooldown) {
        super(triggerMaterial, "<#a38329>" + name, channelTime, cooldown);
        this.hydra = hydra;
    }
}
