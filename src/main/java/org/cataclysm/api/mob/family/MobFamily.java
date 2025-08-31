package org.cataclysm.api.mob.family;

import lombok.Getter;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

@Getter
public enum MobFamily {
    PARAGON_TEMPLE("#c4c4c4"),
    MONOLITH("#D0D0D0"),
    ARCANE("#D5B56E"),
    TWISTED(CataclysmColor.TWISTED),
    GHAST("#E0A0A0"),
    PIGLIN("#C0A0A0"),
    WITHER_SKELETON("#C0A0A0"),

    VANILLA("#FFFFFF"),

    ;

    private final String color;
    private final CataclysmMob.SpawnTag spawnTag;

    MobFamily(@NotNull CataclysmColor color) {
        this(color.getColor());
    }

    MobFamily(String color) {
        this.color = color;
        this.spawnTag = CataclysmMob.SpawnTag.EMPTY;
    }
}