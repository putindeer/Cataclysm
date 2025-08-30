package org.cataclysm.game.pantheon.level.listeners.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.jetbrains.annotations.NotNull;

@Getter
public class PantheonUnregisterRequestEvent extends Event {
    private final PantheonOfCataclysm pantheon;

    public PantheonUnregisterRequestEvent(PantheonOfCataclysm pantheon) {
        this.pantheon = pantheon;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    @NotNull
    public static HandlerList getHandlerList() {return handlers;}
}
