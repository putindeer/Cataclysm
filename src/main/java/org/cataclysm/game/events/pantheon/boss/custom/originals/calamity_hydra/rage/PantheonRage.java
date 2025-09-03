package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.rage;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;

@Getter @Setter
public class PantheonRage {
    private final PantheonRageManager manager;
    private final PantheonHydra hydra;
    protected BossBar rageBar;

    protected final double max;
    protected boolean overcharged;
    protected double current;
    protected double queued;

    public PantheonRage(PantheonHydra hydra) {
        this.hydra = hydra;
        this.max = 100.0;
        this.current = 0.0;
        this.manager = new PantheonRageManager(this);
    }

    public void reset() {
        this.hydra.getArena().getPlayersInArena().forEach(this.rageBar::removeViewer);

        this.hydra.rageManager = new PantheonRage(this.hydra);
        BossBar bossBar = this.hydra.rageManager.getManager().createRageBar();
        this.hydra.getArena().getPlayersInArena().forEach(bossBar::addViewer);
    }
}
