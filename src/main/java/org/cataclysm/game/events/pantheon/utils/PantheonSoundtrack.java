package org.cataclysm.game.events.pantheon.utils;

import net.kyori.adventure.key.Key;
import org.cataclysm.api.Soundtrack;

public class PantheonSoundtrack {
    private final Soundtrack soundtrack = new Soundtrack();

    public PantheonSoundtrack() {
        this.registerTracks();
    }

    public void stopAll() {soundtrack.stopAll();}

    public void loopRagnarokTracks() {
        loop("RAGNAROK_THEME", 195);
    }
    public void loopCataclysmTracks(int theme) {
        if (theme == 1) loop("CATACLYSM_THEME_1", 217);
        if (theme == 2) loop("CATACLYSM_THEME_2", 318);
    }

    private void loop(String key, int duration) {
        stopAll();
        soundtrack.loop(key, duration);
    }

    public void registerTracks() {
        soundtrack.addTrack("RAGNAROK_THEME", Key.key("cataclysm.ragnarok.theme"));
        soundtrack.addTrack("CATACLYSM_THEME_1", Key.key("cataclysm.cataclysm.theme_1"));
        soundtrack.addTrack("CATACLYSM_THEME_2", Key.key("cataclysm.ragnarok.theme_2"));
    }
}
