package org.cataclysm.game.world.ragnarok;

import lombok.Getter;
import lombok.Setter;
import org.cataclysm.Cataclysm;
import org.jetbrains.annotations.NotNull;

@Setter @Getter
public class RagnarokData {
    private int duration;
    private int timeLeft;
    private int level;

    public RagnarokData(int duration, int level) {
        this(duration, duration, level);
    }

    public RagnarokData(int duration, int timeLeft, int level) {
        this.duration = duration;
        this.timeLeft = timeLeft;
        this.level = level;
    }

    public RagnarokData append(@NotNull RagnarokData data) {
        this.duration += data.getDuration();
        this.timeLeft += data.getTimeLeft();
        this.level = data.getLevel();

        return this;
    }

    public static @NotNull RagnarokData autoBuild() {
        int deathCount = Cataclysm.getGameManager().data().getDeathCount();

        int level = ((int) Math.floor(((double) deathCount / 10))) + 1;

        var baseAmplifier = 900;
        if (Cataclysm.getDay() >= 7) baseAmplifier *= 2;
        var duration = baseAmplifier * ((deathCount - 1) % 10 + 1);

        return new RagnarokData(duration, level);
    }
}