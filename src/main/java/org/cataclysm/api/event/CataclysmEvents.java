package org.cataclysm.api.event;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import org.cataclysm.api.color.CataclysmColor;

@Getter
public enum CataclysmEvents {

    PARAGON_FEVER(new CataclysmEvent("PARAGON_FEVER", 5400, BossBar.Color.RED, CataclysmColor.GOLD_EVENT)),
    GREED_IS_GOOD(new CataclysmEvent("GREED_IS_GOOD", 10800, BossBar.Color.RED, CataclysmColor.GOLD_EVENT)),
    SHULKER_SHOCK(new CataclysmEvent("SHULKER_SHOCK", 10800, BossBar.Color.PURPLE, CataclysmColor.SHULKER_SHOCK)),

    ;

    private final CataclysmEvent event;

    CataclysmEvents(CataclysmEvent event) {
        this.event = event;
    }

}
