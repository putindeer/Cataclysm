package org.cataclysm.api.boss.ability;

import org.bukkit.Material;

public abstract class AbilityBooster extends Ability {
    public AbilityBooster(Material triggerMaterial, String name, int channelTime, int cooldown) {
        super(triggerMaterial, name, channelTime, cooldown);
    }
}
