package org.cataclysm.game.world.dungeons;

import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.StructureUtils;
import org.cataclysm.api.structure.data.StructureLevel;

import java.util.List;

public class PaleGarden extends CataclysmStructure {
    public PaleGarden(StructureLevel level) {
        super(level);
        super.listener = new GardenListener(this);
        super.ignoreAirBlocks = false;
        super.setUp();
    }

    public PaleGarden() {
        super("PALE_GARDEN");
        super.listener = new GardenListener(this);
        super.ignoreAirBlocks = false;
    }

    @Override
    public String getAdvancement() {
        return "";
    }

    static class GardenListener implements Listener {
        private final PaleGarden paleGarden;

        public GardenListener(PaleGarden citadel) {
            this.paleGarden = citadel;
        }

        @EventHandler(priority = EventPriority.LOWEST)
        private void onCreatureSpawn(CreatureSpawnEvent event) {
            LivingEntity entity = event.getEntity();

            var reason = event.getSpawnReason();
            List<CreatureSpawnEvent.SpawnReason> permittedReasons = List.of(
                    CreatureSpawnEvent.SpawnReason.COMMAND,
                    CreatureSpawnEvent.SpawnReason.SPAWNER,
                    CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);

            if (permittedReasons.contains(reason) || entity instanceof ArmorStand) return;
            if (StructureUtils.isEntityInStructure(entity, this.paleGarden)) entity.remove();
        }
    }

}
