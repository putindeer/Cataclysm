package org.cataclysm.game.pantheon.handlers;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.world.PantheonLocations;
import org.cataclysm.game.pantheon.phase.PantheonPhase;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PantheonPhaseHandler {
    private @Getter PantheonPhase phase;
    private final PantheonOfCataclysm pantheon;

    private ScheduledFuture<?> entranceParticlesTask;

    public PantheonPhaseHandler(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    public void tryElapseWaitroom() {
        int ready = PantheonPlayerHandler.getReadyCount();
        int size = PantheonPlayerHandler.getParticipants().size();

        if (ready >= size) {
            //pantheon.getPhaseChanger().castEntranceTransition();
        }
    }

    public void changePhase(@NotNull PantheonPhase phase) {
        this.castDefaults(this.phase, phase);
        this.phase = phase;
        switch (phase) {
            case WAITING -> this.castWaiting();
            case WARDEN_FIGHT -> this.castWardenFight();
        }
    }

    public void castWardenFight() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonPlayerHandler.teleport(player, PantheonLocations.WARDEN_ARENA.getCoreLocation());
        }
    }

    public void castWaiting() {
        this.runEntranceParticles();
        for (Player player : Bukkit.getOnlinePlayers()) {
            //PantheonSender.sendPantheonMessage(player, "El Panteón de Cataclysm ha abierto sus puertas.");
        }
    }

    public void castDefaults(PantheonPhase previousPhase, PantheonPhase phase) {
        if (entranceParticlesTask != null) {
            entranceParticlesTask.cancel(true);
            entranceParticlesTask = null;
        }
        if (previousPhase == PantheonPhase.WAITING) {
            for (Player player : Bukkit.getOnlinePlayers()) PantheonPlayerHandler.setReady(player, false);
        }
    }

    private void runEntranceParticles() {
        ScheduledExecutorService service = pantheon.getService();
        entranceParticlesTask = service.scheduleAtFixedRate(PantheonTaskHandler::tickEntrance, 0, 350, TimeUnit.MILLISECONDS);
    }

    public enum PantheonPhase {
        WARDEN_FIGHT,
        HYDRA_FIGHT,
        PALE_KING_FIGHT,
        VOID_LORD_FIGHT,
        RAGNAROK_FIGHT,
        FINAL_PHASE,
        BREAK, //Used for a 5-minute break
        WAITING, //Used when waiting for players to start the pantheon
        IDDLE, //Used when the pantheon is not active
    }
}
