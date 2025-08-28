package org.cataclysm.game.pantheon;

import org.bukkit.event.Listener;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.level.LevelHandler;
import org.cataclysm.game.pantheon.level.PantheonAreas;
import org.cataclysm.game.pantheon.listeners.PantheonPlayerListener;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PantheonHandler {
    private final Cataclysm instance = Cataclysm.getInstance();
    private final PantheonOfCataclysm pantheon;

    public PantheonHandler(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    public void setUp() {
        LevelHandler levelHandler = new LevelHandler(pantheon);
        levelHandler.setUpEntrance(PantheonAreas.PANTHEON_ENTRANCE.getCoreLocation());
        levelHandler.setUpWorld();
    }

    public void registerAll() {
        registerListeners();
        registerTasks();
    }

    public void registerTasks() {
        ScheduledExecutorService service = pantheon.service;
        service.scheduleAtFixedRate(PantheonTasks::tickPlayerTask, 0, 1, TimeUnit.SECONDS);
    }
    public void registerListeners() {
        List<Listener> listeners = List.of(
                new PantheonPlayerListener(pantheon)
        );
        listeners.forEach(listener -> instance.getServer().getPluginManager().registerEvents(listener, Cataclysm.getInstance()));
    }
}
