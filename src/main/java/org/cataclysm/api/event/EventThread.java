package org.cataclysm.api.event;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.cataclysm.Cataclysm;

public class EventThread {

    private final @Getter CataclysmEvent event;
    private BukkitTask task;

    public EventThread(CataclysmEvent event) {
        this.event = event;
    }

    public void runTimer() {
        // Runs every 20 ticks (1 second)
        this.task = Bukkit.getScheduler().runTaskTimer(Cataclysm.getInstance(), () -> {
            if (this.event.timeLeft <= 0) {
                Cataclysm.getEvent().stop();
            } else {
                this.event.timeLeft -= 1;
                this.event.data.timeLeft = this.event.timeLeft;

                var barManager = this.event.barManager;
                barManager.updateProgress();
                barManager.updateName();
            }
        }, 0L, 20L);
    }

    public void shutdown() {
        if (this.task != null) {
            this.task.cancel();
            this.task = null;
        }
    }
}
