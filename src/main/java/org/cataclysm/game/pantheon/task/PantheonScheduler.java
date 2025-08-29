package org.cataclysm.game.pantheon.task;

import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public abstract class PantheonScheduler {
    public final List<TaskHolder> activeTasks;
    private final ScheduledExecutorService service;

    public PantheonScheduler(PantheonOfCataclysm pantheon) {
        this.activeTasks = new ArrayList<>();
        this.service = pantheon.getService();
    }

    public String schRepeatingTask(String identifier, Runnable command, int time, TimeUnit timeUnit) {
        ScheduledFuture<?> task = this.service.scheduleAtFixedRate(() -> Bukkit.getScheduler().runTask(Cataclysm.getInstance(), command), 0, time, timeUnit);
        return register(identifier, task);
    }

    public String register(String identifier, ScheduledFuture<?> task) {
        this.activeTasks.add(new TaskHolder(identifier, task));
        return identifier;
    }
}
