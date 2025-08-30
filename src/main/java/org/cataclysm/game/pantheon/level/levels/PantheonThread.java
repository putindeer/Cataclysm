package org.cataclysm.game.pantheon.level.levels;

import lombok.Getter;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;

@Getter
public abstract class PantheonThread {
    protected final List<ScheduledFuture<?>> futures;
    protected final PantheonOfCataclysm pantheon;

    public PantheonThread(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
        this.futures = new ArrayList<>();
    }

    public abstract void handle();

    public void cancelAll() {
        futures.forEach(future -> future.cancel(true));
        futures.clear();
    }
}
