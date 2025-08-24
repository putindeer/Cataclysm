package org.cataclysm.game.raids.bosses.calamity_hydra;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmBoss;
import org.cataclysm.api.boss.events.BossFightStopEvent;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.raids.bosses.calamity_hydra.rage.HydraRageManager;
import org.cataclysm.global.utils.math.MathUtils;

public class HydraListener implements Listener {
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        CataclysmBoss boss = Cataclysm.getBoss();

        if (!(boss instanceof CalamityHydra hydra) || !(damager instanceof Player player) || !CataclysmBoss.isController(player)) return;

        HydraRageManager rageManager = hydra.rage.getManager();
        double amount = (event.getDamage() / 35);
        rageManager.infuriate(MathUtils.round(amount, 2));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        var bossFight = Cataclysm.getBoss();
        if (bossFight == null || !bossFight.getController().equals(player)) return;

        if (!(bossFight instanceof CalamityHydra hydra)) return;

        HydraPhase phase = hydra.phase;
        if (phase.getCurrent() == 1) event.setDamage(event.getDamage() * 4);
    }

    @EventHandler
    public void onBossfightStop(BossFightStopEvent event) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            var incursionUpgrade = PersistentData.get(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER);
            PersistentData.set(player, "COMPLETED_INCURSIONS", PersistentDataType.INTEGER, incursionUpgrade != null ? incursionUpgrade + 1 : 1);
        });

        HandlerList.unregisterAll(this);
    }
}
