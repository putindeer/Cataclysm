package org.cataclysm.game.items;

import lombok.Getter;
import org.cataclysm.api.color.CataclysmColor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Getter
public enum ItemFamily {
    ARCANE_TOOLS(CataclysmColor.ARCANE, "the_twisted/elthe"),
    TWISTED_TOOLS(CataclysmColor.TWISTED, "the_twisted/you_feel_twisted"),
    CALAMITY_ARMOR(CataclysmColor.CALAMITY, "the_nether/calamity"),
    MIRAGE_TOOLS(CataclysmColor.MIRAGE, "the_end/mirage"),
    PALE_ARMOR(CataclysmColor.PALE, "the_pale/final_form"),

    ;

    private final CataclysmColor color;
    private final @Nullable String advancement;

    ItemFamily(CataclysmColor color, @Nullable String advancement) {
        this.color = color;
        this.advancement = advancement;
    }

    public @NotNull List<CataclysmItems> getMembers() {
        var result = new ArrayList<CataclysmItems>();

        for (var item : CataclysmItems.values()) {
            var builder = item.getBuilder();
            if (builder.getFamily() != null && builder.getFamily().equals(this)) result.add(item);
        }

        return result;
    }



    public @NotNull String getKey() {
        var parts = this.name().split("_");
        return parts.length > 0 ? parts[0].toLowerCase() : "";
    }
}
