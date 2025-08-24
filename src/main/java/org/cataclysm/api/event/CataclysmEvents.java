package org.cataclysm.api.event;

import lombok.Getter;
import net.kyori.adventure.bossbar.BossBar;
import org.cataclysm.api.color.CataclysmColor;

@Getter
public enum CataclysmEvents {

    PARAGON_FEVER(new EventManager("PARAGON_FEVER", 5400, BossBar.Color.RED, CataclysmColor.GOLD_EVENT)),
    GREED_IS_GOOD(new EventManager("GREED_IS_GOOD", 10800, BossBar.Color.RED, CataclysmColor.GOLD_EVENT)),
    SHULKER_SHOCK(new EventManager("SHULKER_SHOCK", 10800, BossBar.Color.PURPLE, CataclysmColor.SHULKER_SHOCK)),

    ;

    private final EventManager event;

    CataclysmEvents(EventManager event) {
        this.event = event;
    }

}
