package org.cataclysm.game.events.pantheon;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonSoundtrack;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter @Setter
public class PantheonOfCataclysm {
    private final ScheduledExecutorService service;
    private final PantheonDispatcher dispatcher;
    private final PantheonEvents event;

    private PantheonLevels level;
    private PantheonBoss boss;

    public PantheonOfCataclysm() {
        this.service = Executors.newSingleThreadScheduledExecutor();
        this.dispatcher = new PantheonDispatcher(this.service, Audience.audience(Bukkit.getOnlinePlayers()));
        this.event = new PantheonEvents(this);
    }

    public void changeLevel(PantheonLevels level) {
        this.level = level;
        this.event.handleEvents(level);
    }
}