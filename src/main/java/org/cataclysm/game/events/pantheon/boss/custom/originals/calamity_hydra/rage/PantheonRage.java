package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.rage;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PantheonRage {
    private static final @Getter int RAGE_LIMIT = 1000;
    private static final @Getter int OUTRAGE_LIMIT = RAGE_LIMIT * 2;

    private ScheduledFuture<?> task;
    private int current;
    private int queued;

    private final PantheonRageBar barManager;
    private final PantheonHydra hydra;

    public PantheonRage(PantheonHydra hydra) {
        this.hydra = hydra;
        this.barManager = new PantheonRageBar(this);
    }

    public void infuriate(int amount) {
        if (this.task != null) this.task.cancel(true);

        this.queued += amount;
        double target = this.current + this.queued;
        boolean increasing = amount > 0;

        this.hydra.getPantheon().getService().scheduleAtFixedRate(() -> {
            int step = increasing ? 1 : -1;
            this.operate(step);

            boolean done = increasing ? this.current >= target : this.current <= target;
            if (done) {
                this.task.cancel(true);
                this.task = null;
            }
        }, 0, (1000 / this.queued), TimeUnit.MILLISECONDS);
    }

    public void handleRageEvents() {
        if (this.current > RAGE_LIMIT && !this.hydra.isOutraged()) this.outrage(true);
        if (this.current <= RAGE_LIMIT && this.hydra.isOutraged()) this.outrage(false);
        this.barManager.tick(this.getPercentage());
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

    private void operate(int amount) {
        this.queued -= amount;

        if (this.current >= this.getRageLimit()) this.current = this.getRageLimit();
        else if (this.current < 0) this.current = 0;
        else this.current += amount;

        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::handleRageEvents);
    }

    private double getPercentage() {
        if (current <= 0) return 0f;
        if (current >= 1000) return 1f;

        BigDecimal percent = BigDecimal.valueOf(current)
                .divide(BigDecimal.valueOf(1000), 2, RoundingMode.HALF_UP); // 2 decimales

        return percent.doubleValue();
    }

    private int getRageLimit() {
        return this.hydra.getPhase() == 3
                ? RAGE_LIMIT : OUTRAGE_LIMIT;
    }
}
