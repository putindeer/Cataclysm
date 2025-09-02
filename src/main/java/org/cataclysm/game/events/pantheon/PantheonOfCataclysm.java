package org.cataclysm.game.events.pantheon;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.cataclysm.game.events.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.pantheon.utils.PantheonDispatcher;
import org.cataclysm.game.events.pantheon.utils.PantheonSoundtrack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter @Setter
public class PantheonOfCataclysm {
    private final ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();

    private final PantheonDispatcher dispatcher = new PantheonDispatcher(this.service, Audience.audience(Bukkit.getOnlinePlayers()));;
    private final PantheonSoundtrack soundtrack = new PantheonSoundtrack();
    private PantheonBoss boss;

    public void start() {
    }

    public void stop() {
    }
}
