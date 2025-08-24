package org.cataclysm.game.world.ragnarok.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cataclysm.game.world.ragnarok.Ragnarok;
import org.jetbrains.annotations.NotNull;

@Getter
public class RagnarokStartEvent extends Event {
    private final Ragnarok ragnarok;

    public RagnarokStartEvent(Ragnarok ragnarok) {
        this.ragnarok = ragnarok;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    @NotNull
    public static HandlerList getHandlerList() {return handlers;}
}