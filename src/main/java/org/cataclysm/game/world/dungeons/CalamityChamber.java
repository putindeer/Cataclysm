package org.cataclysm.game.world.dungeons;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.data.StructureLevel;
import org.cataclysm.api.structure.StructureUtils;

import java.util.List;

public class CalamityChamber extends CataclysmStructure {

    public CalamityChamber(StructureLevel level) {
        super(level);
        super.listener = new ChamberListener(this);
        super.setUp();
    }

    public CalamityChamber() {
        super("CALAMITY_CHAMBER");
        super.listener = new ChamberListener(this);
    }

    @Override
    public String getAdvancement() {
        return "the_nether/lets_go_gambling";
    }

    static class ChamberListener implements Listener {
        private final CalamityChamber chamber;

        public ChamberListener(CalamityChamber chamber) {
            this.chamber = chamber;
        }

        @EventHandler(priority = EventPriority.LOWEST)
        private void onPlayerDeath(PlayerDeathEvent event) {
            var player = event.getPlayer();

            if (!(StructureUtils.isEntityInStructure(player, this.chamber))) return;
            PersistentData.set(player, "DEATH-IN-CHAMBER", PersistentDataType.BOOLEAN, true);
        }

        @EventHandler(priority = EventPriority.LOWEST)
        private void onCreatureSpawn(CreatureSpawnEvent event) {
            LivingEntity entity = event.getEntity();
            var reason = event.getSpawnReason();
            List<CreatureSpawnEvent.SpawnReason> permittedReasons = List.of(
                    CreatureSpawnEvent.SpawnReason.SPAWNER_EGG,
                    CreatureSpawnEvent.SpawnReason.COMMAND,
                    CreatureSpawnEvent.SpawnReason.SPAWNER,
                    CreatureSpawnEvent.SpawnReason.TRIAL_SPAWNER);

            if (permittedReasons.contains(reason) || entity instanceof ArmorStand) return;
            if (StructureUtils.isEntityInStructure(entity, this.chamber)) entity.remove();
        }

        @EventHandler
        public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
            var bucket = event.getBucket();
            var location = event.getBlock().getLocation();

            if (!StructureUtils.isLocationInStructure(location, this.chamber)) return;
            if (bucket == Material.LAVA_BUCKET || bucket == Material.WATER_BUCKET) {
                event.setCancelled(true);
                event.getPlayer().playSound(Sound.sound(Key.key("block.netherite_block.break"), Sound.Source.BLOCK, 1.0F, 0.65F));
            }
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent event) {
            var player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) return;
            if (!StructureUtils.isLocationInStructure(player.getLocation(), this.chamber)) return;
            event.setCancelled(true);
            player.playSound(Sound.sound(Key.key("block.netherite_block.break"), Sound.Source.BLOCK, 1.0F, 0.65F));
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            var player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) return;
            var block = event.getBlock();
            List<Material> allowedBlocks = List.of(Material.FIRE, Material.SOUL_FIRE, Material.NETHER_WART);
            if (allowedBlocks.contains(block.getType())) return;
            if (!StructureUtils.isLocationInStructure(player.getLocation(), this.chamber)) return;
            event.setCancelled(true);
            player.playSound(Sound.sound(Key.key("block.netherite_block.break"), Sound.Source.BLOCK, 1.0F, 0.65F));
        }
    }
}
