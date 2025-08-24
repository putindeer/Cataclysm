package org.cataclysm.game.mob.custom.vanilla.skeleton.standard;

import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.global.utils.color.ColorUtils;

public class SkeletonArmors {
    public static void setArcaneArmor(CataclysmMob mob) {
        mob.setLeatherArmor(ColorUtils.hexToColor("#ba8f49"));
    }

    public static void setWarlockArmor(CataclysmMob mob) {
        mob.setLeatherArmor(ColorUtils.hexToColor("#47663d"));
    }

    public static void setArbalistArmor(CataclysmMob mob) {
        mob.setLeatherArmor(ColorUtils.hexToColor("#662d2d"));
    }
}
