package org.cataclysm.game.events.pantheon.bosses.calamity_hydra.rage;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.PantheonHydra;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PantheonRage {
    private static final @Getter int RAGE_LIMIT = 1000;
    private static final @Getter int OUTRAGE_LIMIT = RAGE_LIMIT * 2;

    private ScheduledFuture<?> task;
    private boolean freezed;
    private int current;
    private int queued;

    private final PantheonRageBar barManager;
    private final PantheonHydra hydra;

    public PantheonRage(PantheonHydra hydra) {
        this.hydra = hydra;
        this.barManager = new PantheonRageBar(this);
    }

    public void handleTickEvents() {
        if (this.hydra.getPhase() >= 3) {
            if (this.current > RAGE_LIMIT && !this.hydra.isOutraged()) this.outrage(true);
            if (this.current <= RAGE_LIMIT && this.hydra.isOutraged()) this.outrage(false);
        }

        if (this.current < 0) this.current = 0;
        if (this.current > this.getRageLimit()) this.current = this.getRageLimit();

        this.barManager.tick((float) this.current/RAGE_LIMIT);
    }

    public void infurate(int amount) {
        if (this.task != null) this.task.cancel(true);

        this.queued += amount;

        double target = this.clamp(this.current + this.queued, 0, this.getRageLimit());
        boolean increasing = amount > 0;

        this.freezed = true;
        this.task = this.hydra.getPantheon().getExecutor().scheduleAtFixedRate(() -> {
            int step = increasing ? 1 : -1;
            this.operate(step);

            boolean done = increasing ? this.current >= target : this.current <= target;
            if (done) {
                this.queued = 0;
                this.freezed = false;
                this.task.cancel(true);
                this.task = null;
            }
        }, 0, Math.abs((1000 / this.queued)), TimeUnit.MILLISECONDS);
    }

    private void operate(int amount) {
        this.queued -= amount;

        this.current += amount;
        this.current = this.clamp(this.current, 0, this.getRageLimit());

        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::handleTickEvents);
    }

    public void outrage(boolean outrage) {
        Audience audience = Audience.audience(Bukkit.getOnlinePlayers());
        if (outrage) {
            this.hydra.updateModel("outrage");
            audience.playSound(Sound.sound(Key.key("item.trident.thunder"), Sound.Source.MASTER, 4F, 0.6F));
            audience.playSound(Sound.sound(Key.key("item.trident.thunder"), Sound.Source.MASTER, 4F, 1.6F));
        }
        else {
            this.hydra.changeHeads(this.hydra.getHeads(), this.hydra.getHeads());
            audience.playSound(Sound.sound(Key.key("block.respawn_anchor.deplete"), Sound.Source.MASTER, 4F, 0.86F));
            audience.playSound(Sound.sound(Key.key("block.beacon.deactivate"), Sound.Source.MASTER, 4F, 0.86F));
        }
        this.hydra.setOutraged(outrage);
    }

    protected int getRageLimit() {return this.hydra.getPhase() == 3 ? OUTRAGE_LIMIT : RAGE_LIMIT;}

    public int clamp(int value, int min, int max) {
        if (value < min) return min;
        return Math.min(value, max);
    }
}
