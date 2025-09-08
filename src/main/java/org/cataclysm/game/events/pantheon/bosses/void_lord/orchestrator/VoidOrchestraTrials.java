package org.cataclysm.game.events.pantheon.bosses.void_lord.orchestrator;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.Soundtrack;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLordThemes;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class VoidOrchestraTrials {
    private ScheduledFuture<?> task;
    private final Soundtrack soundtrack;
    private final VoidLord lord;

    public VoidOrchestraTrials(VoidLord lord) {
        this.soundtrack = lord.getSoundtrack();
        this.lord = lord;
    }

    protected void startDreamNoMoreTrial() {
        startTrial(
                VoidLordThemes.DREAM_NO_MORE,
                4,
                this.lord::stopPantheonFight
        );
    }

    protected void startHeartOfTheAbyssTrial() {
        startTrial(
                VoidLordThemes.HEART_OF_THE_ABYSS,
                2,
                () -> this.lord.getOrchestrator().startPhase(2)
        );
    }

    /**
     * Método centralizado para lanzar un "trial".
     */
    private void startTrial(VoidLordThemes theme, int bossBarStyle, Runnable onComplete) {
        this.soundtrack.stopAll();
        this.lord.handleBossBar(bossBarStyle);

        int duration = theme.playTheme(this.soundtrack);
        healGradually(duration, onComplete);
    }

    /**
     * Cura al Void Lord gradualmente hasta el 100% de vida.
     * Durante la curación queda en modo "espectador".
     */
    private void healGradually(int time, Runnable onComplete) {
        long period = 10; // ms
        long totalSteps = (time * 1000L) / period;

        double missingHealth = this.lord.maxHealth - this.lord.health;
        double increment = missingHealth / totalSteps;

        // Poner en modo espectador mientras dura la curación
        this.lord.getController().setGameMode(GameMode.SPECTATOR);

        this.task = this.lord.getExecutor().scheduleAtFixedRate(() -> {
            this.lord.health = Math.min(this.lord.health + increment, this.lord.maxHealth);
            this.lord.updateBar();

            if (this.lord.health >= this.lord.maxHealth) {
                Bukkit.getConsoleSender().sendMessage("[VoidLord] Finalizó la curación gradual.");
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    this.lord.getController().setGameMode(GameMode.SURVIVAL); // Regresar al estado normal
                    onComplete.run();
                });

                this.task.cancel(true);
                this.task = null;
            }
        }, 0, period, TimeUnit.MILLISECONDS);
    }
}