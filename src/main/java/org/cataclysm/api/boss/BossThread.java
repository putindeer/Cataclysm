package org.cataclysm.api.boss;

import lombok.Getter;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BossThread {
    protected @Getter ScheduledExecutorService service;
    protected final CataclysmBoss bossFight;

    public BossThread(CataclysmBoss bossFight) {
        this.bossFight = bossFight;
    }

    public void startTickTask() {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.service.scheduleAtFixedRate(this.bossFight::tick, 0, 1, TimeUnit.SECONDS);
    }
}
