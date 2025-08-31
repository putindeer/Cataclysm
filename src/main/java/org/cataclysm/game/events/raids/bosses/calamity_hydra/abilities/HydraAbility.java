package org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities;

import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.CalamityHydra;

public abstract class HydraAbility extends Ability {
    public final CalamityHydra hydra;

    public HydraAbility(CalamityHydra hydra, Material triggerMaterial, String name, int channelTime, double cooldown) {
        super(triggerMaterial, "<#a38329>" + name, channelTime, cooldown);
        this.hydra = hydra;
    }
}
