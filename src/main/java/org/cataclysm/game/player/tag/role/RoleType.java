package org.cataclysm.game.player.tag.role;

import lombok.Getter;
import org.cataclysm.global.utils.text.TextUtils;
import org.jetbrains.annotations.NotNull;

@Getter
public enum RoleType {
    ADMIN("#D5B56E"),
    STAFF("#ab8dd9"),
    VIP("#8dcaf6"),
    MEMBER("#76b254"),
    TURKEY("#a47854"),
    SURVIVOR("#AB958A");

    private final String hex;

    RoleType(String hex) {
        this.hex = hex;
    }

    public @NotNull String getHex() {
        return "<" + this.hex + ">";
    }

    public @NotNull String getBadge() {
        return TextUtils.convertUnicode("\\uE90" + this.ordinal());
    }

    public boolean hasPerms() {
        return this == ADMIN || this == STAFF;
    }
}