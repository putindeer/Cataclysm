package org.cataclysm.game.pantheon.level.listeners;

import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.levels.PantheonLevel;
import org.cataclysm.game.pantheon.level.listeners.events.PantheonUnregisterRequestEvent;

@Getter
public abstract class PantheonListener implements Listener {
    private final PantheonOfCataclysm pantheon;
    private final PantheonLevel level;

    public PantheonListener(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
        this.level = pantheon.getLevel();
    }

    @EventHandler
    private void onStop(PantheonUnregisterRequestEvent event) {
        HandlerList.unregisterAll(this);
    }
}