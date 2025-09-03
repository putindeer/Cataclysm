package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;

@Registrable
public class PantheonHydraListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDamage(EntityDamageEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || pantheon.getBoss() == null
                || !(pantheon.getBoss() instanceof PantheonHydra hydra)
                || !(event.getEntity() instanceof Player player)
                || !CataclysmBoss.isController(player)) return;

        PantheonHydraPhase phase = hydra.phaseManager;
        if (phase.getPhase() == 1) event.setDamage(event.getDamage() * 4);
    }

}
