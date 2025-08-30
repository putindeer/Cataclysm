package org.cataclysm.game.pantheon.level.timer;

import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.levels.PantheonThread;

import java.util.concurrent.TimeUnit;

public class TimerThread extends PantheonThread {
    private PantheonTimer timer;

    public TimerThread(PantheonOfCataclysm pantheon) {
        super(pantheon);
    }

    @Override
    public void handle() {
        futures.add(pantheon.scheduleLoop(this::tick, 1, TimeUnit.SECONDS));
    }

    private void tick() {
        if (this.timer == null) this.timer = pantheon.getTimer();
        this.timer.update();
    }
}
