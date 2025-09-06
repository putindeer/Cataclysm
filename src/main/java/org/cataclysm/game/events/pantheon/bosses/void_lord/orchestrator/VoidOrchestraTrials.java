package org.cataclysm.game.events.pantheon.bosses.void_lord.orchestrator;

import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.Soundtrack;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLordThemes;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VoidOrchestraTrials {
    private ScheduledFuture<?> task;
    private int duration;

    private final Soundtrack soundtrack;
    private final VoidLord lord;

    public VoidOrchestraTrials(VoidLord lord) {
        this.soundtrack = lord.getSoundtrack();
        this.lord = lord;
    }

    protected void startDreamNoMoreTrial() {
        this.soundtrack.stopAll();
        this.lord.handleBossBar(4);
        this.duration = VoidLordThemes.DREAM_NO_MORE.playTheme(this.soundtrack);
        this.healGradually(this.duration, this.lord::stopPantheonFight);
    }

    protected void startHeartOfTheAbyssTrial() {
        this.soundtrack.stopAll();
        this.lord.handleBossBar(2);
        this.duration = VoidLordThemes.HEART_OF_THE_ABYSS.playTheme(this.soundtrack);
        this.healGradually(this.duration, ()
                -> this.lord.getOrchestrator().startPhase(2));
    }

    private void healGradually(int time, Runnable task) {
        long period = 10;
        long totalSteps = (time * 1000L) / period;

        double missingHealth = this.lord.maxHealth - this.lord.health;
        double increment = missingHealth / totalSteps;

        this.task = this.lord.getExecutor().scheduleAtFixedRate(() -> {
            this.lord.health += increment;
            this.lord.updateBar();

            if (this.lord.health >= this.lord.maxHealth) {
                Bukkit.getConsoleSender().sendMessage("Ending heal gradually task");
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task);
                this.lord.health = this.lord.maxHealth;
                this.lord.updateBar();
                this.task.cancel(true);
                this.task = null;
            }
        }, 0, period, TimeUnit.MILLISECONDS);
    }
}
