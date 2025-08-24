package org.cataclysm.game.player.survival.resurrect.totems.events;

import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerUseTotemEvent extends Event {
    private final Player player;
    private final Component cause;
    private final String totemId;
    private final int number;
    private final String mortality;

    public PlayerUseTotemEvent(Player player, Component cause, String totemId, int number, String mortality) {
        this.player = player;
        this.cause = cause;
        this.totemId = totemId;
        this.number = number;
        this.mortality = mortality;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    @NotNull
    public static HandlerList getHandlerList() {return handlers;}
}
