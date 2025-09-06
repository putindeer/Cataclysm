package org.cataclysm.game.events.pantheon.config.context;

public record PantheonSnapshot(
        int activeTime,
        int deathCount,
        int totemsUsed,
        int mortalityLost
) {

    public static PantheonSnapshot fromContext(PantheonContext context) {
        return new PantheonSnapshot(
                context.getActiveTime(),
                context.getDeathCount(),
                context.getTotemsUsed(),
                context.getMortalityLost()
        );
    }

    public PantheonContext toContext() {
        PantheonContext context = new PantheonContext();
        context.setActiveTime(activeTime);
        context.setDeathCount(deathCount);
        context.setTotemsUsed(totemsUsed);
        context.setMortalityLost(mortalityLost);
        return context;
    }

}