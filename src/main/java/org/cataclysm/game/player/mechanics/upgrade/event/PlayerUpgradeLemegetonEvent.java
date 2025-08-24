package org.cataclysm.game.player.mechanics.upgrade.event;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.cataclysm.game.player.mechanics.upgrade.Upgrades;
import org.jetbrains.annotations.NotNull;

@Getter
public class PlayerUpgradeLemegetonEvent extends Event {
    private final Player player;
    private final Upgrades upgrade;
    private final int level;
    private final int upgrades;

    public PlayerUpgradeLemegetonEvent(Player player, Upgrades upgrade, int level, int upgrades) {
        this.player = player;
        this.upgrade = upgrade;
        this.level = level;
        this.upgrades = upgrades;
    }

    private static final HandlerList handlers = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {return handlers;}

    @NotNull
    public static HandlerList getHandlerList() {return handlers;}
}