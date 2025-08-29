package org.cataclysm.game.pantheon.service;

import java.util.concurrent.TimeUnit;

public class PantheonTaskRunner {
    private final PantheonService service;

    public PantheonTaskRunner(PantheonService service) {
        this.service = service;
    }

    public void runEntranceTasks() {
        PantheonScheduler scheduler = this.service.getSchedulers().get("ENTRANCE");
        scheduler.schRepeatingTask("EFFECTS", 1, TimeUnit.SECONDS);
        scheduler.schRepeatingTask("VISUALS", 350, TimeUnit.MILLISECONDS);
    }
}
