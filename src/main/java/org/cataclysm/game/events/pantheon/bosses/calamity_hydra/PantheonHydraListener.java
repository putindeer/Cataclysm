package org.cataclysm.game.events.pantheon.bosses.calamity_hydra;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.boss.events.BossChannelAbilityEvent;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.rage.PantheonRage;

@Registrable
public class PantheonHydraListener implements Listener {

    @EventHandler
    private void onBossChannelAbility(BossChannelAbilityEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || !(event.getBoss() instanceof PantheonHydra hydra)) return;

        PantheonRage rage = hydra.getRage();
        rage.infurate(-100);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDamage(EntityDamageEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null || pantheon.getBoss() == null
                || !(pantheon.getBoss() instanceof PantheonHydra hydra)
                || !(event.getEntity() instanceof Player player)
                || !CataclysmBoss.isController(player)) return;

        double amplifier = hydra.getResistance();
        event.setDamage(event.getDamage()/amplifier);
    }

}
