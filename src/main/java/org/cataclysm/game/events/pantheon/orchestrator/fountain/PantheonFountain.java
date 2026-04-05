package org.cataclysm.game.events.pantheon.orchestrator.fountain;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.config.player.PantheonProfile;
import org.cataclysm.game.events.pantheon.orchestrator.fountain.gui.InteractableMob;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PantheonFountain {
    private ScheduledFuture<?> loop;
    private Runnable stopTask;

    private final StatusNotifier statusNotifier;
    private final PantheonOfCataclysm pantheon;
    private final Location location;

    public PantheonFountain(PantheonOfCataclysm pantheon, Location location) {
        this.pantheon = pantheon;
        this.location = location;
        this.statusNotifier = new StatusNotifier(this);
    }

    public void start() {
        resetProfilesReady(false);
        InteractableMob.handleExistance(location, true);
        statusNotifier.startLoop();
        startLoop();
        pantheon.setFountain(this);
    }

    public void stop() {
        resetProfilesReady(false);
        InteractableMob.handleExistance(location, false);
        this.statusNotifier.stopLoop();
        stopLoop();
        pantheon.setFountain(null);
        if (stopTask != null) runSync(stopTask);
    }

    private void tick() {
        World world = location.getWorld();
        if (world == null) return;

        world.spawnParticle(Particle.SQUID_INK, location, 80, 0.15F, 2.5F, 0.15F, 0.01F, null, true);
        world.spawnParticle(Particle.SMOKE, location, 90, 0.35F, 2.5F, 0.35F, 0.01F, null, true);

        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.5F, 0.5F);
    }

    public void startLoop() {
        stopLoop(); // evita duplicados
        loop = pantheon.getExecutor().scheduleAtFixedRate(
                () -> runSync(this::tick),
                0, 500, TimeUnit.MILLISECONDS
        );
    }

    public void stopLoop() {
        if (loop != null && !loop.isCancelled()) {
            loop.cancel(true);
            loop = null;
        }
    }

    public void resetProfilesReady(boolean ready) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonProfile.fromPlayer(pantheon, player).setReady(ready);
        }
    }

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task);
    }
}