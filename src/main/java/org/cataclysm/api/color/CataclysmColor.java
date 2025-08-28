package org.cataclysm.api.color;

import lombok.Getter;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Getter
public enum CataclysmColor {
    VANILLA("#ebebeb"),

    PARAGON("#c6a96e", "#c3ac7e", "#c2b59b"),
    LEMEGETON("#cdcdcd", "#979797", "#686868"),
    ENDER("#5d368a", "#503273", "#5f5378"),

    TWISTED("#6C5B9A", "#7f749e", "#96959c"),
    ARCANE("#D5B56E", "#d3bc88", "#cdc4af"),
    CALAMITY("#caa207", "#ab9361", "#9e9073"),
    MIRAGE("#9649a6", "#703985", "#6c567a"),
    PALE("#D1D1D1", "#DEDEDE", "#C2C2C2"),
    VOID("#525252", "#454545", "#B0B0B0"),

    GOLD_EVENT("#c3ac7e", "#c6a96e", "#b0ada5"),
    SHULKER_SHOCK("#9649a6", "#703985", "#6c567a"),

    PANTHEON("#B5813F", "#9A7E5B", "#B0A887")

    ;

    private final String color;
    private final String color2;
    private final String color3;

    CataclysmColor(String color) {
        this(color, color, color);
    }

    CataclysmColor(String color, String color2, String color3) {
        this.color = color;
        this.color2 = color2;
        this.color3 = color3;
    }

    public @NotNull String wrap(int colorIndex) {
        switch (colorIndex) {
            case 1 -> {
                return "<" + this.color2 + ">";
            }
            case 2 -> {
                return "<" + this.color3 + ">";
            }
        }
        return "<" + this.color + ">";
    }
}
