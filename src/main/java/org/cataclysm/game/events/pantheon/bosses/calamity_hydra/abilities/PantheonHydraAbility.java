package org.cataclysm.game.events.pantheon.bosses.calamity_hydra.abilities;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;

@Getter @Setter
public abstract class PantheonHydraAbility extends PantheonAbility {
    private int cost;

    public PantheonHydraAbility(Material triggerMaterial, String name, int channelTime) {
        this(triggerMaterial, name, channelTime, 100);
    }

    public PantheonHydraAbility(Material triggerMaterial, String name, int channelTime, int cost) {
        super(triggerMaterial, name, channelTime);
        this.cost = cost;
    }
}
