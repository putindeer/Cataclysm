package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.global.utils.text.font.TinyCaps;

@Getter
public abstract class PantheonAbility extends Ability {
    public String hoverName;

    public PantheonAbility(Material triggerMaterial, String name, int channelTime) {
        this(triggerMaterial, name, "#d4a155", channelTime);
    }

    public PantheonAbility(Material triggerMaterial, String name, String color, int channelTime) {
        super(triggerMaterial, name, channelTime, 7);
        this.hoverName = "<" + color.toUpperCase() + ">" + "[" + TinyCaps.tinyCaps(name) + "]";
    }
}
