package org.cataclysm.game.events.pantheon.orchestrator;

import lombok.Getter;
import lombok.Setter;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.PantheonBosses;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;

@Getter @Setter
public class PantheonOrchestrator {
    private final PantheonOfCataclysm pantheon;
    private final PantheonDispatcher dispatcher;

    private final PantheonBattles battleManager;
    private final PantheonEvents eventManager;

    public PantheonOrchestrator(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
        this.dispatcher = pantheon.getDispatcher();
        this.battleManager = new PantheonBattles(this);
        this.eventManager = new PantheonEvents(this);
    }

    public void startFountain(PantheonLevels level) {
        this.pantheon.setLevel(PantheonLevels.PANTHEON_FOUNTAIN);
        this.eventManager.castFountain(level);
        this.pantheon.getDispatcher().resetDelay();
    }

    public void startLevel(boolean autoElapse, PantheonLevels level) {
        this.pantheon.setLevel(level);
        switch (level) {
            case PALE_TREE -> this.eventManager.castEventCountdown(autoElapse);
            case PANTHEON_ENTRANCE -> this.eventManager.castPantheonReopenEvent(autoElapse);
            case TWISTED_CITY -> this.eventManager.castStartEvent(autoElapse);
            case PALE_HEART -> this.battleManager.startPaleHeartBattle();
        }
        this.pantheon.getDispatcher().resetDelay();
    }

    public void startBossFight(PantheonBosses boss) {
        switch (boss) {
            case TWISTED_WARDEN -> this.battleManager.startTwistedCityBattle();
            case CALAMITY_HYDRA -> this.battleManager.startHydrasDungeonBattle();
            case VOID_LORD -> this.battleManager.startPaleHeartBattle();
        }
        this.pantheon.getDispatcher().resetDelay();
    }
}
