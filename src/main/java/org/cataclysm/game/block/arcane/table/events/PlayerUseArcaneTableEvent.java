package org.cataclysm.game.block.arcane.table.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerUseArcaneTableEvent extends Event {
    private final Player player;
    private final ItemStack result;

    public PlayerUseArcaneTableEvent(Player player, ItemStack result) {
        this.player = player;
        this.result = result;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    @NotNull
    public static HandlerList getHandlerList() {return handlers;}
}
