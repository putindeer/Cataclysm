package org.cataclysm.game.pantheon.handlers;

import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PantheonHandler {
    private final ScheduledExecutorService service;

    public PantheonHandler(PantheonOfCataclysm pantheon) {
        this.service = pantheon.getService();
    }

    public void registerAll() {
        registerTasks();
    }

    public void unregisterAll() {
        Cataclysm.getPantheon().getService().shutdownNow();
    }

    public void registerTasks() {
    }
}