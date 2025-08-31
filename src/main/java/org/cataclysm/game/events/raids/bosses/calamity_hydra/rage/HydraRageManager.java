package org.cataclysm.game.events.raids.bosses.calamity_hydra.rage;

import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.cataclysm.Cataclysm;
import org.cataclysm.global.utils.color.ColorUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HydraRageManager {
    private final HydraRage rage;
    private ScheduledFuture<?> currentTask;

    public HydraRageManager(HydraRage rage) {
        this.rage = rage;
    }

    public void operateCurrentRage(double operation) {
        double currentRage = this.rage.current;
        double currentPhase = this.rage.getHydra().phase.getCurrent();

        double heads = this.rage.getHydra().heads;
        if (currentRage >= 100 && currentPhase < 3 && heads < 5) return;
        if (currentRage < 0 || currentRage >= 200) return;

        this.rage.current += operation;
        this.rage.queued -= operation;

        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::updateRageBar);
    }

    public void operateQueued(double amount) {
        double currentRage = this.rage.current;
        double finalQueued = (this.rage.queued + amount);
        double currentPhase = this.rage.getHydra().phase.getCurrent();

        double heads = this.rage.getHydra().heads;
        if (currentRage >= 100 && currentPhase < 3 && heads < 5) return;

        double finalRange = currentRage + finalQueued;
        if (finalRange >= 100 && currentPhase < 3) return;
        if (finalRange < 0 || finalRange >= 200) return;

        this.rage.queued += amount;
    }

    public void infuriate(double amount) {
        this.stopCurrentTask();
        this.operateQueued(amount);

        final long interval = (long) (2000 / (this.rage.queued * 10));
        final double finalRage = this.rage.current + this.rage.queued;

        ScheduledExecutorService service = this.rage.getHydra().getThread().getService();
        if (service == null) return;
        this.currentTask = service.scheduleAtFixedRate(() -> {
            this.operateCurrentRage(.1);
            if (this.rage.current >= finalRage) this.stopCurrentTask();
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    public void reassure(double amount) {
        this.stopCurrentTask();
        this.operateQueued(-amount);

        final long interval = (long) (2000 / (Math.abs(this.rage.queued) * 10));
        final double finalRage = this.rage.current + this.rage.queued;

        ScheduledExecutorService service = this.rage.getHydra().getThread().getService();
        if (service == null) return;
        this.currentTask = service.scheduleAtFixedRate(() -> {
            this.operateCurrentRage(-.1);
            if (this.rage.current <= finalRage) this.stopCurrentTask();
        }, 0, interval, TimeUnit.MILLISECONDS);
    }

    public void setOverraged(boolean overraged) {
        double currentRage = this.rage.current;
        double currentPhase = this.rage.getHydra().phase.getCurrent();
        double heads = this.rage.getHydra().heads;

        if (currentRage >= 100 && currentPhase < 3 && heads < 5) return;

        this.rage.overcharged = overraged;
        this.rage.getHydra().getArena().getPlayersInArena().forEach(fighter -> {
            if (overraged) {
                this.rage.getHydra().phase.updateModel(5);
                fighter.playSound(fighter, Sound.ITEM_TRIDENT_THUNDER, SoundCategory.HOSTILE, 5.0F, 0.65F);
                fighter.playSound(fighter, Sound.ITEM_TRIDENT_THUNDER, SoundCategory.HOSTILE, 5.0F, 1.55F);
            } else {
                this.rage.getHydra().phase.updateModel(6);
                fighter.playSound(fighter, Sound.BLOCK_RESPAWN_ANCHOR_DEPLETE, SoundCategory.HOSTILE, 2.5F, 0.85F);
                fighter.playSound(fighter, Sound.BLOCK_BEACON_DEACTIVATE, SoundCategory.HOSTILE, 1.5F, 0.85F);
            }
        });
    }

    public BossBar createRageBar() {
        if (this.rage.rageBar != null) return this.rage.rageBar;
        this.rage.rageBar = BossBar.bossBar(this.getRageBarName(), 0, BossBar.Color.PURPLE, BossBar.Overlay.PROGRESS);
        return this.rage.rageBar;
    }

    public void updateRageBar() {
        this.rage.rageBar.name(this.getRageBarName());
        if (this.rage.current >= 100) {
            if (!this.rage.overcharged) this.setOverraged(true);
        } else {
            this.rage.rageBar.progress(this.getPercentage());
            if (this.rage.overcharged) this.setOverraged(false);
        }
    }

    public void stopCurrentTask() {
        if (this.currentTask == null) return;
        this.currentTask.cancel(true);
        this.currentTask = null;
    }

    private double roundDecimals(int decimalPlaces, double value) {
        BigDecimal bd = new BigDecimal(Double.toString(value));
        bd = bd.setScale(decimalPlaces, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private @NotNull Component getRageBarName() {
        String display = "<#50345e>Furia";
        if (this.rage.current >= 100) display = "<gradient:#581787:#8d55b5>OVERRAGED</gradient>";

        float percentage = (float) this.roundDecimals(2, this.rage.current);
        float pending = (float) this.roundDecimals(2, this.rage.queued);
        String fire = this.getSkull(percentage * 0.01);
        return MiniMessage.miniMessage().deserialize(fire + " " + display + ": <#725182>" + percentage + "<#50345e>% " + fire + " <#636363>(" + pending + ")");
    }

    private String getSkull(double interpolation) {
        String hex = ColorUtils.interpolateColorHex("#fcfcfc", "#581787", interpolation);
        return "<" + hex + ">\uD83D\uDD25";
    }

    private float getPercentage() {
        return (float) this.roundDecimals(2, this.rage.current/this.rage.max);
    }
}
