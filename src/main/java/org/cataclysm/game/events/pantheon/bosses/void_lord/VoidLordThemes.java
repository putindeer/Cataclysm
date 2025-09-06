package org.cataclysm.game.events.pantheon.bosses.void_lord;

import lombok.Getter;
import net.kyori.adventure.key.Key;
import org.cataclysm.api.Soundtrack;

@Getter
public enum VoidLordThemes {
    PALE_KING(true, 174),
    HEART_OF_THE_ABYSS(false, 79),
    VOID_LORD(true, 104),
    DREAM_NO_MORE(true, 33),

    ;

    private final boolean loopable;
    private final int themeDuration;

    VoidLordThemes(boolean loopable, int themeDuration) {
        this.loopable = loopable;
        this.themeDuration = themeDuration;
    }

    public int playTheme(Soundtrack soundtrack) {
        soundtrack.play(soundtrack.getSound(this.name()));
        return this.themeDuration;
    }

    public void loopTheme(Soundtrack soundtrack) {
        soundtrack.loop(this.name(), this.themeDuration);
    }

    public static void registerTracks(Soundtrack soundtrack) {
        for (VoidLordThemes theme : VoidLordThemes.values()) {
            String sound = "cataclysm.boss.void_lord." + theme.name().toLowerCase();
            soundtrack.addTrack(theme.name(), Key.key(sound));
        }
    }
}
