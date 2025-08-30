package org.cataclysm.game.pantheon.level.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.levels.PantheonLevel;
import org.cataclysm.game.pantheon.level.levels.entrance.PantheonEntrance;

import java.util.List;

public class PantheonEntityListener extends PantheonListener {
    public PantheonEntityListener(PantheonOfCataclysm pantheon) {
        super(pantheon);
    }

    private void handleNaturalSpawn(CreatureSpawnEvent event) {
        List<EntityType> skippableTypes = List.of(EntityType.ARMOR_STAND);

        LivingEntity entity = event.getEntity();

        if (skippableTypes.contains(entity.getType())) return;
        entity.remove();
    }

    @EventHandler(priority = EventPriority.LOWEST)
    private void onCreatureSpawn(CreatureSpawnEvent event) {
        if (getLevel() instanceof PantheonEntrance) handleNaturalSpawn(event);
    }
}
