package org.cataclysm.game.events.pantheon.orchestrator;

import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonBosses;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonWarper;

public class PantheonBattles {
    private final PantheonOrchestrator orchestrator;
    private final PantheonDispatcher dispatcher;

    private static final long WARP_DELAY = 7000L;

    public PantheonBattles(PantheonOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        this.dispatcher = orchestrator.getDispatcher();
    }

    public void startTwistedCityBattle() {
        scheduleBattle(PantheonLevels.TWISTED_CITY, PantheonBosses.TWISTED_WARDEN.getInstance());
    }

    public void startHydrasDungeonBattle() {
        scheduleBattle(PantheonLevels.HYDRAS_DUNGEON, PantheonBosses.CALAMITY_HYDRA.getInstance());
    }

    public void startPaleHeartBattle() {
        scheduleBattle(PantheonLevels.PALE_HEART, PantheonBosses.VOID_LORD.getInstance());
    }

    public void startCataclysmFinalBattle() {
        scheduleBattle(PantheonLevels.STORMS_EYE, PantheonBosses.THE_CATACLYSM.getInstance());
    }

    private void scheduleBattle(PantheonLevels level, PantheonBoss boss) {
        dispatcher.schedule(() -> PantheonWarper.warp(level));
        dispatcher.addDelay(WARP_DELAY);
        dispatcher.schedule(() -> startBattle(boss));
    }

    private void startBattle(PantheonBoss boss) {
        boss.setPantheon(Cataclysm.getPantheon());
        boss.setController(this.orchestrator.getPantheon().getController());
        boss.startPantheonFight();
    }
}