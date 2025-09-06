package org.cataclysm.game.events.pantheon;

import java.util.Arrays;

public enum PantheonList {
    INFERNAL_GUY,
    METEORCRY,
    LEITOMC,
    ITZPEPETIME,
    FABOHERRERA,
    LECHUGAMC,
    JIROWOO,
    __ECLYPSE__,
    TANASOFIA,
    ITHEWRAITH,
    DXEGOOO,
    LAWOFBALANCE,
    JOHANBIGCUMBY,
    KINI97,
    LETZTEPRINZESSIN,
    WYUUSH,
    XEMANKJ,
    _9ZLUCAS,
    DIEGOT_MANC,
    SERGIDRV,
    DARTHG_,
    AVELHUNTER,
    RULDES,
    THE_WITHEROVO,
    TOM_555,
    GRYMPS,
    CUBIKMC,
    FELOXWTF,
    SMAURITO,
    THEBESTPUPPER,
    CARMENMOMAZER;

    /**
     * Verifica si un nombre está en la whitelist.
     * Case-insensitive.
     */
    public static boolean isListed(String name) {
        if (name == null) return false;
        String normalized = name.replaceAll("[^A-Za-z0-9_]", "").toUpperCase();
        return Arrays.stream(values())
                .anyMatch(entry -> entry.name().equalsIgnoreCase(normalized));
    }
}
