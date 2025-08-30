package org.cataclysm.game.pantheon.level.levels;

import lombok.Getter;
import org.bukkit.Location;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.helpers.PantheonDispatcher;

@Getter
public abstract class PantheonLevel {
    protected PantheonThread thread;
    protected PantheonStates state;

    protected boolean fastStart;
    protected int stopDelay;

    protected final PantheonOfCataclysm pantheon;
    protected final PantheonDispatcher dispatcher;

    public PantheonLevel(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
        this.dispatcher = pantheon.getDispatcher();
        this.defaults();
    }

    private void defaults() {
        this.state = PantheonStates.ACTIVE;
        this.fastStart = false;
        this.stopDelay = 80;
    }

    public void start() {
        if (!fastStart) {
            this.warp();
            this.pantheon.schedule(this::onStart, 120);
        } else this.onStart();
    }

    public void stop() {
        this.shutdown();
        this.pantheon.schedule(this::onStop, stopDelay);
    }

    public void shutdown() {
        if (this.thread != null) this.thread.cancelAll();
    }

    public abstract void onStart();
    public abstract void onStop();
    public abstract Location location();

    public void warp() {pantheon.teleport(location());}
}
