package org.cataclysm.game.events.pantheon.bosses.void_lord.orchestrator;

import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;

public class VoidOrchestraEvents {
    private final VoidLord lord;

    public VoidOrchestraEvents(VoidLord lord) {
        this.lord = lord;
    }

    // On buff
    protected void castVoidAbsorptionEvent() {
    }

    // On death
    protected void castVoidExpansionEvent() {
    }
}
