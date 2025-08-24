package org.cataclysm.game.mob.listener.types;

import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;

@Registrable
public class WanderingMobsListener implements Listener {

    @EventHandler
    public void onWanderingSkullExplosion(EntityExplodeEvent event) {
        if (!(event.getEntity() instanceof WitherSkull witherSkull)) return;
        if (!(witherSkull.getShooter() instanceof LivingEntity shooter)) return;
        String mobId = CataclysmMob.getID(shooter);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("WANDERING")) return;

        event.setYield(5f);
    }


}
