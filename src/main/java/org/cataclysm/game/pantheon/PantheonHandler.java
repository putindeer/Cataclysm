package org.cataclysm.game.pantheon;

import org.bukkit.Bukkit;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.level.listeners.PantheonListener;
import org.cataclysm.game.pantheon.level.listeners.PantheonBalanceListener;
import org.cataclysm.game.pantheon.level.listeners.PantheonEntityListener;
import org.cataclysm.game.pantheon.level.listeners.PantheonPlayerListener;
import org.cataclysm.game.pantheon.level.listeners.events.PantheonUnregisterRequestEvent;

import java.util.ArrayList;
import java.util.List;

public class PantheonHandler {
    private final List<PantheonListener> listeners =  new ArrayList<>();

    private final PantheonOfCataclysm pantheon;

    public PantheonHandler(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
        this.pantheonListeners();
    }

    public void registerListeners() {
        listeners.forEach(listener ->
                Bukkit.getPluginManager().registerEvents(listener, Cataclysm.getInstance()));
    }

    public void unregisterListeners() {
        new PantheonUnregisterRequestEvent(pantheon).callEvent();
    }

    private void pantheonListeners() {
        this.listeners.add(new PantheonBalanceListener(pantheon));
        this.listeners.add(new PantheonEntityListener(pantheon));
        this.listeners.add(new PantheonPlayerListener(pantheon));
    }
}
