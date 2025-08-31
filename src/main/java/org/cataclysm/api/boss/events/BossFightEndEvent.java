package org.cataclysm.api.boss.events;

import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cataclysm.api.boss.CataclysmBoss;
import org.jetbrains.annotations.NotNull;

@Getter
public class BossFightEndEvent extends Event {
    private static final HandlerList handlers = new HandlerList();
    private final CataclysmBoss boss;

    public BossFightEndEvent(CataclysmBoss boss) {
        this.boss = boss;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    @NotNull
    public static HandlerList getHandlerList() {return handlers;}
}
