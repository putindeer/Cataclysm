package org.cataclysm.game.events.pantheon.bosses.void_lord.orchestrator;

import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;

public class VoidLordOrchestrator {
    private final VoidOrchestraTrials trials;
    private final VoidOrchestraPhases phases;
    private final VoidOrchestraEvents events;
    private final VoidLord lord;

    public VoidLordOrchestrator(VoidLord lord) {
        this.trials = new VoidOrchestraTrials(lord);
        this.phases = new VoidOrchestraPhases(lord);
        this.events = new VoidOrchestraEvents(lord);
        this.lord = lord;
    }

    public void castEvent(int event) {
        switch (event) {
            case 1 -> {}
            case 2 -> this.events.castVoidExpansionEvent();
            case 3 -> this.events.castVoidAbsorptionEvent();
        }
    }

    public void startTrial(int phaseTrial) {
        this.lord.setElapsing(true);
        switch (phaseTrial) {
            case 1 -> this.trials.startHeartOfTheAbyssTrial();
            case 2 -> this.trials.startDreamNoMoreTrial();
        }
    }

    public void startPhase(int phase) {
        this.lord.setCurrentPhase(phase);
        this.lord.setElapsing(false);
        switch (phase) {
            case 1 -> this.phases.startPaleKingPhase();
            case 2 -> this.phases.startVoidLordPhase();
        }
    }
}