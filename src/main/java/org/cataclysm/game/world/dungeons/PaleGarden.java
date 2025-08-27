package org.cataclysm.game.world.dungeons;

import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
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

        public GardenListener(PaleGarden paleGarden) {
            this.paleGarden = paleGarden;
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

        @EventHandler
        private void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
            var bucket = event.getBucket();
            var location = event.getBlock().getLocation();

            if (!StructureUtils.isLocationInStructure(location, this.paleGarden)) return;
            if (bucket == Material.LAVA_BUCKET) event.setCancelled(true);
        }

    }

}
