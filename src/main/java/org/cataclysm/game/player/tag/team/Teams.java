package org.cataclysm.game.player.tag.team;

import lombok.Getter;
import org.cataclysm.global.utils.text.TextUtils;
import org.jetbrains.annotations.Nullable;

@Getter
public enum Teams {

    LOS_CHAVOS,
    LOS_QUICOS,
    YA_SABEN_QUIENES,
    MOMAZOS_RALSEI,
    FUMADOS,
    MANGO,
    FILO_V,
    FET,
    PANADINO,
    LOS_WAXOS,
    WARPED_GUYS,
    FAZE_DISFORIA,
    YHOLGA,
    QUEEE,
    EYS,
    OUROBOROS,
    GRU,
    CATACLYSM_MEDAL,
    NONE,

    ;

    public @Nullable String getBadge() {
        if (this == NONE) return null;
        var prefix = "\\uE80";
        if (this.ordinal() > 9) prefix = "\\uE8";
        return TextUtils.convertUnicode(prefix + this.ordinal());
    }

}
