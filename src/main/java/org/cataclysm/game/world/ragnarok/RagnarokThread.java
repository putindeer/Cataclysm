package org.cataclysm.game.world.ragnarok;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RagnarokThread {
    private final @Getter ScheduledExecutorService thread;
    private final Ragnarok ragnarok;

    public RagnarokThread(@NotNull Ragnarok ragnarok) {
        this.ragnarok = ragnarok;
        this.thread = Executors.newSingleThreadScheduledExecutor();
    }

    public void run() {
        this.thread.scheduleAtFixedRate(() -> {
            var data = this.ragnarok.getData();
            if (data.getTimeLeft() <= 0) {
                assert Cataclysm.getRagnarok() != null;
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> new RagnarokManager(Cataclysm.getRagnarok()).stop());
            } else {
                data.setTimeLeft(data.getTimeLeft() - 1);

                var bossBar = this.ragnarok.getBossBar();
                bossBar.getBlueBar().progress(bossBar.getProgress());
                bossBar.getTimeBar().name(RagnarokUtils.getFormattedTime(data));
            }
        }, 0, 1, TimeUnit.SECONDS);
    }

    public void shutdown() {
        this.thread.shutdown();
    }
}