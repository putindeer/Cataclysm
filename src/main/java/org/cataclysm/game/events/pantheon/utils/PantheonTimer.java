package org.cataclysm.game.events.pantheon.utils;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.global.utils.math.MathUtils;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter @Setter
public class PantheonTimer {
    private final PantheonOfCataclysm pantheon;
    private final BossBar bossBar;

    private ScheduledFuture<?> task;
    private Runnable stopTask;
    private String display;
    private int timeLeft;

    public PantheonTimer(PantheonOfCataclysm pantheon, int duration) {
        this.pantheon = pantheon;
        this.timeLeft = duration;
        this.bossBar = BossBar.bossBar(Component.text(""), 1, BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS);
    }

    public void start() {
        if (this.pantheon.getTimer() != null) this.pantheon.getTimer().stop();
        this.pantheon.setTimer(this);
        this.startUpdater();
        this.setGlobalVisibility(true);
    }

    public void stop() {
        if (this.pantheon.getTimer() == null) return;
        if (this.stopTask != null) Bukkit.getScheduler().runTask(Cataclysm.getInstance(), stopTask);
        this.pantheon.setTimer(null);
        this.stopUpdater();
        this.setGlobalVisibility(false);
    }

    public void update() {
        if (this.timeLeft <= 0) this.stop();
        this.timeLeft--;
        this.bossBar.name(this.getTitle());
    }

    public void setGlobalVisibility(boolean visibility) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            this.setPlayerVisibility(player, visibility);
        }
    }

    public void setPlayerVisibility(Player player, boolean visibility) {
        if (visibility) this.bossBar.addViewer(player);
        else this.bossBar.removeViewer(player);
    }

    protected void startUpdater() {
        this.task = this.pantheon.getExecutor().scheduleAtFixedRate(() ->
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), this::update), 0, 1, TimeUnit.SECONDS);
    }

    protected void stopUpdater() {
        if (this.task == null) return;
        this.task.cancel(true);
        this.task = null;
    }

    private Component getTitle() {
        String frmTime = MathUtils.formatSeconds(this.timeLeft);
        return MiniMessage.miniMessage().deserialize(TinyCaps.tinyCaps(this.display.replace("##:##", frmTime)));
    }
}
