package org.cataclysm.game.pantheon.bosses.calamity_hydra.rage;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.PantheonHydra;

@Getter @Setter
public class HydraRage {
    private final HydraRageManager manager;
    private final PantheonHydra hydra;
    protected BossBar rageBar;

    protected final double max;
    protected boolean overcharged;
    protected double current;
    protected double queued;

    public HydraRage(PantheonHydra hydra) {
        this.hydra = hydra;
        this.max = 100.0;
        this.current = 0.0;
        this.manager = new HydraRageManager(this);
    }

    public void reset() {
        this.hydra.getArena().getPlayersInArena().forEach(this.rageBar::removeViewer);

        this.hydra.rage = new HydraRage(this.hydra);
        BossBar bossBar = this.hydra.rage.getManager().createRageBar();
        this.hydra.getArena().getPlayersInArena().forEach(bossBar::addViewer);
    }
}
