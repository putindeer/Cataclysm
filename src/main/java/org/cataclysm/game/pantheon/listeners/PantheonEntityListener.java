package org.cataclysm.game.pantheon.listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.PantheonLevels;

import java.util.List;

@Registrable
public class PantheonEntityListener implements Listener {

    @EventHandler
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();

        if (pantheon == null) return;

        Location entLoc = event.getLocation();
        Location coreLoc = PantheonLevels.PANTHEON_ENTRANCE.getCoreLocation();
        List<EntityType> skippableTypes = List.of(EntityType.ARMOR_STAND);

        if (entLoc.distance(coreLoc) <= 200 && !skippableTypes.contains(entity.getType())) {
            entity.remove();
        }
    }

}
