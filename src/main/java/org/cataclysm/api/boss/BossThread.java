package org.cataclysm.api.boss;

import lombok.Getter;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.cataclysm.game.raids.bosses.calamity_hydra.CalamityHydra;

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
        //this.service.scheduleAtFixedRate(this::tickController, 0, 1, TimeUnit.SECONDS);
        this.service.scheduleAtFixedRate(this.bossFight::tick, 0, 1, TimeUnit.SECONDS);
    }

    private void tickController() {
        var controller = this.bossFight.controller;
        if (controller == null || !controller.isOnline()) return;

        var actionBar = MiniMessage.miniMessage().deserialize(this.bossFight.health + "/" + this.bossFight.maxHealth);
        if (this.bossFight instanceof CalamityHydra hydra) {
            double current = hydra.rage.getCurrent();
            actionBar = MiniMessage.miniMessage().deserialize(this.bossFight.health + "/" + this.bossFight.maxHealth);
        }
        controller.sendActionBar(actionBar);
    }
}
