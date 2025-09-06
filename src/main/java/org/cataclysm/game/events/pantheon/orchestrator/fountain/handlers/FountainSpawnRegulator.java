package org.cataclysm.game.events.pantheon.orchestrator.fountain.handlers;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.events.pantheon.PantheonOfCataclysm;

import java.util.List;

@Registrable
public class FountainSpawnRegulator implements Listener {
    private static final List<EntityType> skippableTypes = List.of(
            EntityType.ARMOR_STAND
    );

    private void handleSpawnedEntity(LivingEntity entity) {
        if (skippableTypes.contains(entity.getType())) return;
        entity.remove();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        PantheonOfCataclysm pantheon = Cataclysm.getPantheon();
        if (pantheon == null) return;

        if (pantheon.getFountain() != null) handleSpawnedEntity(event.getEntity());
    }
}
