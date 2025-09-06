package org.cataclysm.game.events.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.bosses.PantheonBoss;
import org.cataclysm.game.events.pantheon.config.PantheonConfigurator;
import org.cataclysm.game.events.pantheon.orchestrator.PantheonOrchestrator;
import org.cataclysm.game.events.pantheon.orchestrator.fountain.PantheonFountain;
import org.cataclysm.game.events.pantheon.utils.PantheonBoard;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonTimer;
import org.cataclysm.server.tablist.CataclysmTablist;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;

@Getter
public class PantheonOfCataclysm {
    private final ScheduledExecutorService executor;
    private final PantheonDispatcher dispatcher;
    private final PantheonOrchestrator orchestrator;
    private final PantheonConfigurator configurator;
    private final PantheonBoard board;

    @Setter private PantheonFountain fountain;
    @Setter private PantheonTimer timer;
    @Setter private PantheonLevels level;
    @Setter private PantheonBoss boss;
    @Setter private Player controller;

    public PantheonOfCataclysm() {
        this.executor = Executors.newSingleThreadScheduledExecutor(new PantheonThreadFactory());
        this.dispatcher = new PantheonDispatcher(this);
        this.orchestrator = new PantheonOrchestrator(this);
        this.configurator = new PantheonConfigurator(this);
        this.board = new PantheonBoard(this);
    }

    @NotNull
    public static PantheonOfCataclysm initializePantheon() {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        pantheon.board.startTick();
        Cataclysm.setPantheon(pantheon);

        pantheon.loadProfilesAndUpdateTablist();
        return pantheon;
    }

    @NotNull
    public PantheonOfCataclysm terminate() {
        Cataclysm.setPantheon(null);
        board.stopTick();

        loadProfilesAndUpdateTablist();
        executor.shutdownNow(); // evita fugas de hilos

        return this;
    }

    private void loadProfilesAndUpdateTablist() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            configurator.getRegistry().loadProfile(player);
            CataclysmTablist.update(player);
        }
    }

    private static class PantheonThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(@NotNull Runnable r) {
            Thread thread = new Thread(r, "PantheonExecutor");
            thread.setDaemon(true);
            return thread;
        }
    }
}
