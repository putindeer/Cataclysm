package org.cataclysm.game.pantheon.service;

import lombok.Getter;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;

import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class PantheonService {
    private final HashMap<String, PantheonScheduler> schedulers;
    private final ScheduledExecutorService executor;
    private final PantheonTaskRunner runner;

    public PantheonService() {
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.schedulers = new HashMap<>();
        this.runner = new PantheonTaskRunner(this);
    }
}
