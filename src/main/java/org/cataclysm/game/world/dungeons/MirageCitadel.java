package org.cataclysm.game.world.dungeons;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.structure.CataclysmStructure;
import org.cataclysm.api.structure.StructureUtils;
import org.cataclysm.api.structure.data.StructureLevel;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MirageCitadel extends CataclysmStructure {
    public MirageCitadel(StructureLevel level) {
        super(level);
        super.listener = new CitadelListener(this);
        super.ignoreAirBlocks = false;
        super.setUp();
    }

    public MirageCitadel() {
        super("MIRAGE_CITADEL");
        super.listener = new CitadelListener(this);
        super.ignoreAirBlocks = false;
    }

    @Override
    public String getAdvancement() {
        return "the_end/an_unrealistic_hope";
    }

    static class CitadelListener implements Listener {
        private final MirageCitadel citadel;

        public CitadelListener(MirageCitadel citadel) {
            this.citadel = citadel;
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
            if (StructureUtils.isEntityInStructure(entity, this.citadel)) entity.remove();
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent event) {
            var player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) return;

            if (!StructureUtils.isLocationInStructure(player.getLocation(), this.citadel)) return;

            StructureLevel level = this.citadel.getLevel();
            if (!this.hasSculpture(level.getLocation(), level.radius)) return;

            event.setCancelled(true);
            player.playSound(Sound.sound(Key.key("block.netherite_block.break"), Sound.Source.BLOCK, 1.0F, 0.65F));
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
            Player player = event.getPlayer();
            if (player.getGameMode() == GameMode.CREATIVE) return;

            Block block = event.getBlock();
            List<Material> allowedBlocks = List.of(Material.SPAWNER, Material.FIRE, Material.SOUL_FIRE);
            if (allowedBlocks.contains(block.getType())) return;

            if (!StructureUtils.isLocationInStructure(player.getLocation(), this.citadel)) return;

            StructureLevel level = this.citadel.getLevel();
            if (!this.hasSculpture(level.getLocation(), level.radius)) return;

            event.setCancelled(true);
            player.playSound(Sound.sound(Key.key("block.netherite_block.break"), Sound.Source.BLOCK, 1.0F, 0.65F));
        }

        private boolean hasSculpture(@NotNull Location center, double radius) {
            boolean hasSculpture = false;
            int amountLeft = 0;

            for (LivingEntity livingEntity : center.getNearbyLivingEntities(radius)) {
                String id = CataclysmMob.getID(livingEntity);
                if (id != null && id.equalsIgnoreCase("MirageSculpture")) {
                    amountLeft++;
                    hasSculpture = true;
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0));
                }
            }

            if (!hasSculpture) return hasSculpture;

            int finalAmountLeft = amountLeft;
            center.getNearbyLivingEntities(radius).forEach(livingEntity -> {
                if (livingEntity instanceof Player player) player.sendActionBar(MiniMessage.miniMessage().deserialize("Mirage Sculptures restantes: " + finalAmountLeft));
            });

            return hasSculpture;
        }
    }

}
