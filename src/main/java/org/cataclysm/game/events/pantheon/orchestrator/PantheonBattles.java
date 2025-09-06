package org.cataclysm.game.events.pantheon.orchestrator;

import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonLevels;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.PantheonBosses;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonWarper;

public class PantheonBattles {
    private final PantheonOrchestrator orchestrator;
    private final PantheonDispatcher dispatcher;

    public PantheonBattles(PantheonOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
        this.dispatcher = orchestrator.getDispatcher();
    }

    public void startTwistedCityBattle() {
        this.dispatcher.schedule(() -> PantheonWarper.warp(PantheonLevels.TWISTED_CITY));
        this.dispatcher.addDelay(7000);
        this.dispatcher.schedule(() -> this.startBattle(PantheonBosses.TWISTED_WARDEN.getInstance()));
    }

    public void startHydrasDungeonBattle() {
        this.dispatcher.schedule(() -> PantheonWarper.warp(PantheonLevels.HYDRAS_DUNGEON));
        this.dispatcher.addDelay(7000);
        this.dispatcher.schedule(() -> this.startBattle(PantheonBosses.CALAMITY_HYDRA.getInstance()));
    }

    public void startPaleHeartBattle() {
        this.dispatcher.schedule(() -> PantheonWarper.warp(PantheonLevels.PALE_HEART));
        this.dispatcher.addDelay(7000);
        this.dispatcher.schedule(() -> this.startBattle(PantheonBosses.VOID_LORD.getInstance()));
    }

    private void startBattle(PantheonBoss boss) {
        boss.setPantheon(Cataclysm.getPantheon());
        boss.setController(this.orchestrator.getPantheon().getController());
        boss.startPantheonFight();
    }
}
