package org.cataclysm.game.pantheon.level.timer;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.global.utils.math.MathUtils;

public class PantheonTimer {
    private final PantheonOfCataclysm pantheon;
    private @Setter String display;
    private @Setter Runnable stopTask;

    private final @Getter TimerThread thread;
    private final BossBar bossBar;
    private @Setter int timeLeft;

    public PantheonTimer(PantheonOfCataclysm pantheon, int duration) {
        this.timeLeft = duration;
        this.pantheon = pantheon;
        this.thread = new TimerThread(pantheon);
        this.bossBar = BossBar.bossBar(Component.text(""), 1, BossBar.Color.WHITE, BossBar.Overlay.PROGRESS);
    }

    public void start() {
        if (pantheon.getTimer() != null) pantheon.getTimer().stop();
        handleAllView(true);
        pantheon.setTimer(this);
        thread.handle();
    }
    public void stop() {
        if (pantheon.getTimer() == null) return;
        thread.handle();
        handleAllView(false);
        if (stopTask != null) Bukkit.getScheduler().runTask(Cataclysm.getInstance(), stopTask);
        pantheon.setTimer(null);
    }

    public void handleAllView(boolean visibility) {
        for (Player player : Bukkit.getOnlinePlayers()) handlePlayerView(player, visibility);
    }

    public void handlePlayerView(Player player, boolean visibility) {
        if (visibility) bossBar.addViewer(player);
        else bossBar.removeViewer(player);
    }

    public void update() {
        if (timeLeft > 0) {
            timeLeft--;
            bossBar.name(getTitle());
        }
        else stop();
    }

    private Component getTitle() {
        String frmTime = MathUtils.formatSeconds(this.timeLeft);
        return MiniMessage.miniMessage().deserialize(display.replace("##:##", frmTime));
    }
}
