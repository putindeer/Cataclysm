package org.cataclysm.api.boss.ability;

import org.bukkit.Material;

public abstract class AbilityUltimate extends Ability {
    public AbilityUltimate(Material triggerMaterial, String name, int channelTime, int cooldown) {
        super(triggerMaterial, name, channelTime, cooldown);
    }
}
