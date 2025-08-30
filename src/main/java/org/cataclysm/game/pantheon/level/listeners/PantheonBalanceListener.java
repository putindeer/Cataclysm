package org.cataclysm.game.pantheon.level.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;

public class PantheonBalanceListener extends PantheonListener {
    public PantheonBalanceListener(PantheonOfCataclysm pantheon) {
        super(pantheon);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
    }
}
