package org.cataclysm.game.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.level.audience.PantheonAudience;
import org.cataclysm.game.pantheon.level.levels.LevelBuilder;
import org.cataclysm.game.pantheon.level.levels.entrance.PantheonEntrance;
import org.cataclysm.game.pantheon.level.timer.PantheonTimer;
import org.cataclysm.game.pantheon.level.levels.PantheonStates;
import org.cataclysm.game.pantheon.level.levels.PantheonLevel;
import org.cataclysm.game.pantheon.helpers.PantheonDispatcher;
import org.cataclysm.game.pantheon.helpers.PantheonTeleport;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PantheonOfCataclysm {
    private static @Getter final World world = LevelBuilder.buildWorld();
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private PantheonTimer timer;
    private PantheonStates state;
    private PantheonLevel level;

    private final PantheonDispatcher dispatcher;
    private final PantheonAudience audience;
    private final PantheonHandler handler;

    public PantheonOfCataclysm() {
        this.dispatcher = new PantheonDispatcher(this);
        this.audience = new PantheonAudience(this);
        this.handler = new PantheonHandler(this);
    }

    public void startLevel(PantheonLevel level) {
        if (this.level != null) this.level.stop();
        this.level = level;
        this.state = level.getState();
        this.level.start();
    }

    public void cancelEvent() {
        if (this.level != null) this.level.shutdown();
        this.teleport(PantheonEntrance.getLocation());
        this.handler.unregisterListeners();
        LevelBuilder.restore();
        Cataclysm.setPantheon(null);
    }


    public void schedule(Runnable runnable, int ticks) {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), runnable, ticks);
    }

    public ScheduledFuture<?> scheduleLoop(Runnable runnable, int time, TimeUnit unit) {
        return this.executor.scheduleAtFixedRate(() ->
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), runnable), 0, time, unit);
    }

    public void teleport(Location location) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            PantheonTeleport.teleport(player, location);
        }
    }

    public static @NotNull PantheonOfCataclysm createPantheon() {
        PantheonOfCataclysm pantheon = new PantheonOfCataclysm();
        pantheon.audience.globalAssistanceVerify();
        pantheon.handler.registerListeners();

        LevelBuilder.adapt();
        Cataclysm.setPantheon(pantheon);

        return pantheon;
    }
}
