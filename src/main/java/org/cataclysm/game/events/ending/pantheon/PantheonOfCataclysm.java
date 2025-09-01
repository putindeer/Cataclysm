package org.cataclysm.game.events.ending.pantheon;

import lombok.Getter;
import lombok.Setter;
import org.cataclysm.game.events.ending.pantheon.boss.PantheonBoss;
import org.cataclysm.game.events.ending.pantheon.utils.PantheonSoundtrack;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class PantheonOfCataclysm {
    private final PantheonSoundtrack soundtrack = new PantheonSoundtrack();
    private List<PantheonBoss> activeBosses = new ArrayList<>();

    public void stop() {}

    public PantheonBoss getBoss() {
        return activeBosses.getFirst();
    }
}
