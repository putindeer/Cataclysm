package org.cataclysm.api.boss.events;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.boss.ability.Ability;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

@Getter
public class BossChannelAbilityEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Ability ability;
    private final Collection<Player> fighters;
    private final CataclysmBoss boss;

    public BossChannelAbilityEvent(Ability ability, Collection<Player> fighters, CataclysmBoss boss) {
        this.ability = ability;
        this.fighters = fighters;
        this.boss = boss;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    @NotNull
    public static HandlerList getHandlerList() {return handlers;}

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {

    }
}
