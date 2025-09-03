package org.cataclysm.game.events.pantheon.boss;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.global.utils.text.font.TinyCaps;
import org.jetbrains.annotations.NotNull;

@Getter @Setter
public abstract class PantheonAbility extends Ability {
    public Title title;
    public String color;
    public String description;
    public final String hoverName;

    public PantheonAbility(Material triggerMaterial, String name, int channelTime) {
        this(triggerMaterial, name, "#d4a155", channelTime);
    }

    public PantheonAbility(Material triggerMaterial, String name, @NotNull String color, int channelTime) {
        super(triggerMaterial, name, channelTime, 7);
        this.color = color;
        this.hoverName = "<" + color.toUpperCase() + ">" + "[" + TinyCaps.tinyCaps(name) + "]";
    }
}