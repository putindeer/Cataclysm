package org.cataclysm.game.pantheon.service;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class PantheonScheduler {
    private final PantheonService service;
    private final HashMap<String, Runnable> tasks;

    public PantheonScheduler(PantheonService service) {
        this.service = service;
        this.tasks = new HashMap<>();
        this.registerTasks();
    }

    public abstract void registerTasks();

    public void schRepeatingTask(String identifier, int time, TimeUnit timeUnit) {
        schRepeatingTask(identifier, 0, time, timeUnit);
    }

    public void schRepeatingTask(String identifier, int delay, int time, TimeUnit timeUnit) {
        Runnable command = this.tasks.get(identifier);
        this.service.getExecutor().scheduleAtFixedRate(() ->
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), command), delay, time, timeUnit);
    }
}
