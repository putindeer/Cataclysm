package org.cataclysm.game.mob.listener.spawn;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.WardenAi;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.cataclysm.arcane.ArcaneBreeze;
import org.cataclysm.game.mob.custom.dungeon.monolith.Termite;
import org.cataclysm.game.mob.custom.vanilla.*;
import org.cataclysm.game.mob.custom.vanilla.slimes.PaleSlime;
import org.cataclysm.game.mob.utils.MobUtils;
import org.cataclysm.game.world.Dimensions;

import java.util.Set;

public class BiomeBasedSpawner {

    public void handleBiomeSpawns(SpawnContext ctx) {
        handleColdBiomes(ctx);
        handleWarmBiomes(ctx);
        handleSpecialBiomes(ctx);
        handlePlainsSpawns(ctx);
        handleDesertSpawns(ctx);
    }

    private void handleColdBiomes(SpawnContext ctx) {
        if (ctx.day < 7 || !MobUtils.isInColdBiome(ctx.entity)) return;

        if (ctx.random.nextInt(100) < 20 && !MobUtils.isInWater(ctx.entity)) {
            CataclysmMob cataclysmMob = null;
            EntityType spawnType = ctx.random.nextBoolean() ? EntityType.SNOW_GOLEM : EntityType.BREEZE;
            switch (spawnType) {
                case BREEZE -> cataclysmMob = new ArcaneBreeze(ctx.level);
                case SNOW_GOLEM -> {
                    cataclysmMob = new GiantSnowGolem(ctx.level);
                    if (ctx.random.nextBoolean()) cataclysmMob = new AggressiveSnowGolem(ctx.level);
                }
            }

            cataclysmMob.addFreshEntity(ctx.location);
            SpawnUtils.setMobCap(cataclysmMob.getBukkitLivingEntity(), 10, 2, 1);
        }

        if (ctx.day >= 14) {
            if (ctx.random.nextInt(100) < 10) {
                ctx.entity.remove();
                EntityType spawnType = ctx.random.nextBoolean() ? EntityType.POLAR_BEAR : EntityType.GOAT;
                var spawnedEntity = ctx.location.getWorld().spawnEntity(ctx.location, spawnType, CreatureSpawnEvent.SpawnReason.NATURAL);
                SpawnUtils.setMobCap(spawnedEntity, 10, 2, 1);
            }
        }
    }

    private void handleWarmBiomes(SpawnContext ctx) {
        if (ctx.day < 14 || !MobUtils.isInWarmBiome(ctx.entity)  || ctx.location.getBlock().isLiquid()) return;

        if (ctx.random.nextInt(100) < 10) {
            ctx.entity.remove();
            int termiteCount = ctx.random.nextInt(3) + 3 * (ctx.day >= 21 ? 2 : 1);
            for (int i = 0; i < termiteCount; i++) {
                CataclysmMob termite = new Termite(ctx.level);
                termite.addFreshEntity(ctx.location);
                SpawnUtils.setMobCap(termite.getBukkitLivingEntity(), 20, 2, 1);
            }
        }
    }

    private void handleSpecialBiomes(SpawnContext ctx) {
        if (ctx.day < 7) return;
        if (ctx.location.getBlock().isLiquid()) return;
        Biome biome = ctx.location.getBlock().getBiome();

        // Pale Garden - PaleSlime spawning
        if (biome == Biome.PALE_GARDEN && ctx.random.nextInt(100) >= 96) {
            ctx.entity.remove();
            new PaleSlime(ctx.level).addFreshEntity(ctx.location);
            return;
        }

        if (biome.equals(Biome.PALE_GARDEN)) {
            if (spawnWarden(ctx, 5)) return;
        }

        if (ctx.day < 14) return;

        if (biome.equals(Biome.MUSHROOM_FIELDS)) {
            if (spawnWarden(ctx, 50)) return;
        }

        if (spawnForestMob(ctx, 25)) return;

        // Dark Forest and Swamp - Witch spawning
        if (ctx.day >= 21 && (biome == Biome.DARK_FOREST || ctx.biomeName.contains("SWAMP")) && ctx.random.nextInt(100) < 10) {
            ctx.entity.remove();
            var ent = ctx.location.getWorld().spawnEntity(ctx.location, EntityType.WITCH, CreatureSpawnEvent.SpawnReason.CUSTOM);
            SpawnUtils.setMobCap(ent, 25, 2, 1);
        }
    }

    private void handlePlainsSpawns(SpawnContext ctx) {
        if (ctx.day < 14 || !ctx.biomeName.contains("PLAINS")) return;

        Set<EntityType> slimeReplaceable = Set.of(EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER);
        if (slimeReplaceable.contains(ctx.entity.getType()) && ctx.random.nextInt(100) < 10) {
            ctx.entity.remove();
            var ent = ctx.location.getWorld().spawnEntity(ctx.location, EntityType.SLIME, CreatureSpawnEvent.SpawnReason.DEFAULT);
            SpawnUtils.setMobCap(ent, 25, 2, 1);
        }
    }

    private void handleDesertSpawns(SpawnContext ctx) {
        if (ctx.day < 14 || !ctx.biomeName.contains("DESERT")) return;

        Set<EntityType> magmaReplaceable = Set.of(EntityType.HUSK, EntityType.SKELETON, EntityType.CREEPER);
        if (magmaReplaceable.contains(ctx.entity.getType()) && ctx.random.nextInt(100) < 10) {
            ctx.entity.remove();
            var ent = ctx.location.getWorld().spawnEntity(ctx.location, EntityType.MAGMA_CUBE, CreatureSpawnEvent.SpawnReason.DEFAULT);
            SpawnUtils.setMobCap(ent, 10, 2, 1);
        }
    }

    private boolean spawnWarden(SpawnContext ctx, int probability) {
        if (ctx.random.nextInt(100) < probability) {
            ctx.entity.remove();
            CustomWarden warden = new CustomWarden(ctx.level);
            warden.addFreshEntity(ctx.location);
            var nmsWarden = warden.getEntity();
            nmsWarden.setPose(net.minecraft.world.entity.Pose.EMERGING);
            nmsWarden.playSound(SoundEvents.WARDEN_AGITATED, 5.0F, 1.0F);
            nmsWarden.getBrain().setMemoryWithExpiry(MemoryModuleType.IS_EMERGING, net.minecraft.util.Unit.INSTANCE, WardenAi.EMERGE_DURATION);
            nmsWarden.getBrain().setMemoryWithExpiry(MemoryModuleType.DIG_COOLDOWN, Unit.INSTANCE, Long.MAX_VALUE);
            SpawnUtils.setMobCap(warden.getBukkitLivingEntity(), 10, 2, 1);
            return true;
        }
        return false;
    }

    private boolean spawnForestMob(SpawnContext ctx, int probability) {
        if (ctx.location.getWorld().equals(Dimensions.NETHER.getWorld())) return false;
        if (ctx.location.getY() < 60) return false;

        if (SpawnUtils.isForestBiome(ctx.biomeName)) {
            if (ctx.random.nextInt(100) < probability) {

                switch (ctx.random.nextInt(3)) {
                    case 0 -> {
                        CataclysmMob llama = new AggressiveLlama(ctx.level);
                        llama.addFreshEntity(ctx.location);
                        SpawnUtils.setMobCap(llama.getBukkitLivingEntity(), 8, 3, 1);
                        return true;
                    }

                    case 1 -> {
                        CataclysmMob wasp = new AggressiveBee(ctx.level);
                        wasp.addFreshEntity(ctx.location);
                        SpawnUtils.setMobCap(wasp.getBukkitLivingEntity(), 8, 3, 1);
                        return true;
                    }

                    case 2 -> {
                        var creaking = ctx.location.getWorld().spawnEntity(ctx.location, EntityType.CREAKING);
                        MobUtils.damageBoost((LivingEntity) creaking, 9999);

                        if (ctx.day >= 21) {
                            MobUtils.healthBoost((LivingEntity) creaking, 15);
                            MobUtils.multiplyAttribute((LivingEntity) creaking, Attribute.MOVEMENT_SPEED, 1.5);
                        }

                        SpawnUtils.setMobCap(creaking, 15, 3, 1);
                    }
                }

                ctx.entity.remove();
            }
        }
        return false;
    }

}
