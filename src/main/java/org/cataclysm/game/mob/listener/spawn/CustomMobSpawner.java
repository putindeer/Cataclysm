package org.cataclysm.game.mob.listener.spawn;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.cataclysm.QuantumReactor;
import org.cataclysm.game.mob.custom.cataclysm.arcane.ArcaneBreeze;
import org.cataclysm.game.mob.custom.cataclysm.arcane.ArcaneEvoker;
import org.cataclysm.game.mob.custom.cataclysm.arcane.ArcaneSpider;
import org.cataclysm.game.mob.custom.cataclysm.calamity.*;
import org.cataclysm.game.mob.custom.cataclysm.twisted.*;
import org.cataclysm.game.mob.custom.vanilla.ghast.*;
import org.cataclysm.game.mob.custom.vanilla.skeleton.wither.Bowmaster;
import org.cataclysm.game.mob.custom.vanilla.skeleton.wither.NetherNightmare;
import org.cataclysm.game.mob.custom.vanilla.skeleton.wither.Swordmaster;
import org.cataclysm.game.world.Dimensions;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CustomMobSpawner {

    public boolean spawnCustomVariant(CreatureSpawnEvent event, SpawnContext ctx) {
        // Skip if event is already cancelled or from a custom spawn reason to prevent loops
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return false;
        }

        return spawnCalamityMobs(ctx) ||
                spawnTwistedMobs(ctx) ||
                spawnArcaneMobs(ctx) ||
                spawnCustomGhasts(ctx) ||
                spawnCustomPiglins(ctx) ||
                spawnCustomWitherSkeletons(ctx);
    }

    private boolean spawnCalamityMobs(SpawnContext ctx) {
        if (ctx.day < 21) return false;
        if (ctx.random.nextBoolean()) return false;
        if (!ctx.location.getWorld().equals(Dimensions.NETHER.createWorld())) return false;

        CataclysmMob mobToSpawn = null;

        switch (ctx.entity.getType()) {
            case BLAZE -> mobToSpawn = new CalamityBlaze(ctx.level);
            case ENDERMAN -> mobToSpawn = new CalamityEnderman(ctx.level);
            case GHAST -> mobToSpawn = new CalamityGhast(ctx.level);

            case PIGLIN, ZOMBIFIED_PIGLIN, PIGLIN_BRUTE -> {
                mobToSpawn = new CalamityPiglin(ctx.level);
                if (ctx.random.nextBoolean()) mobToSpawn = new CalamityEnderman(ctx.level);
            }

            case WITHER_SKELETON -> mobToSpawn = new CalamitySkeleton(ctx.level);
        }

        if (mobToSpawn != null) {
            ctx.entity.remove();
            mobToSpawn.addFreshEntity(ctx.location);
            SpawnUtils.setMobCap(mobToSpawn.getBukkitLivingEntity(), 10, 3, 1);
            return true;
        }

        return spawnTwistedMobs(ctx);
    }

    private boolean spawnTwistedMobs(SpawnContext ctx) {
        Set<EntityType> twistedTypes = Set.of(
                EntityType.ZOMBIE, EntityType.SKELETON, EntityType.CREEPER,
                EntityType.SPIDER, EntityType.ENDERMAN, EntityType.BLAZE,
                EntityType.PIGLIN_BRUTE, EntityType.ZOMBIE_VILLAGER, EntityType.STRAY, EntityType.BOGGED
        );

        if (!twistedTypes.contains(ctx.entity.getType())) return false;
        if (isOnSlab(ctx.location)) return false;

        var ragnarok = Cataclysm.getRagnarok();


        double spawnPercentage = calculateTwistedSpawnPercentage(ctx);
        boolean shouldAlwaysSpawn =
                (
                        ctx.entity.getType().equals(EntityType.ZOMBIE) ||
                        ctx.entity.getType().equals(EntityType.ZOMBIE_VILLAGER) ||
                        ctx.entity.getType().equals(EntityType.ENDERMAN)
                );

        if (ctx.day >= 21 && shouldAlwaysSpawn) spawnPercentage = 100;
        if (ragnarok != null && ragnarok.getData().getLevel() >= 5) spawnPercentage = 100;

        if (ctx.random.nextInt(100) < spawnPercentage) {
            CataclysmMob twistedMob = getTwistedMob(ctx);

            if (twistedMob instanceof TwistedCreeper && ctx.random.nextInt(0, 100) >= 75){
                if (ctx.day >= 21) twistedMob = new QuantumReactor(ctx.level);
            }

            if (twistedMob != null) {
                ctx.entity.remove();
                twistedMob.addFreshEntity(ctx.location.clone().add(0, 1.5, 0));
                SpawnUtils.setMobCap(twistedMob.getBukkitLivingEntity(), 8, 2, 1);
                return true;
            }
        }

        return false;
    }

    private static @Nullable CataclysmMob getTwistedMob(SpawnContext ctx) {
        CataclysmMob twistedMob = null;

        switch (ctx.entity.getType()) {
            case ZOMBIE, ZOMBIE_VILLAGER -> twistedMob = new TwistedZombie(ctx.level);
            case SKELETON, STRAY, BOGGED -> twistedMob = new TwistedSkeleton(ctx.level);
            case CREEPER -> twistedMob = new TwistedCreeper(ctx.level);
            case SPIDER -> twistedMob = new TwistedSpider(ctx.level);
            case ENDERMAN -> twistedMob = new TwistedEnderman(ctx.level);
            case BLAZE -> twistedMob = new TwistedBlaze(ctx.level);
            case PIGLIN_BRUTE -> twistedMob = new TwistedBrute(ctx.level);
        }

        return twistedMob;
    }

    private double calculateTwistedSpawnPercentage(SpawnContext ctx) {
        double basePercentage = 20.0;

        var ragnarok = Cataclysm.getRagnarok();
        if (ragnarok != null && ragnarok.getData().getLevel() >= 4) {
            basePercentage *= 2;
        }

        if (ctx.day >= 14 && ctx.entity.getType() == EntityType.BLAZE) {
            basePercentage = 50;
            if (ctx.day >= 21) basePercentage *= 2;
        }

        return basePercentage;
    }

    private boolean spawnArcaneMobs(SpawnContext ctx) {
        if (ctx.day < 14) return false;

        Set<EntityType> arcaneTypes = Set.of(EntityType.EVOKER, EntityType.SPIDER, EntityType.CAVE_SPIDER, EntityType.BREEZE);

        if (arcaneTypes.contains(ctx.entity.getType()) && ctx.random.nextInt(100) < 20) {
            CataclysmMob arcaneMob = null;

            switch (ctx.entity.getType()) {
                case EVOKER -> arcaneMob = new ArcaneEvoker(ctx.level);
                case SPIDER, CAVE_SPIDER -> arcaneMob = new ArcaneSpider(ctx.level);
                case BREEZE -> arcaneMob = new ArcaneBreeze(ctx.level);
            }

            if (arcaneMob != null) {
                ctx.entity.remove();
                arcaneMob.addFreshEntity(ctx.location.add(0, 2, 0));
                SpawnUtils.setMobCap(arcaneMob.getBukkitLivingEntity(), 8, 2, 1);
                return true;
            }
        }

        return false;
    }

    private boolean spawnCustomGhasts(SpawnContext ctx) {
        if (ctx.entity.getType() != EntityType.GHAST) return false;

        CataclysmMob randomGhast = null;
        var ragnarok = Cataclysm.getRagnarok();
        int probability = ctx.random.nextInt(100);

        if (ragnarok != null && ragnarok.getData().getLevel() >= 5) probability+=20;

        if (ctx.day >= 14 && ctx.biomeName.contains("QUARTZ") && probability > 90) {
            randomGhast = new Ur_Ghast(ctx.level);
        } else {
            switch (ctx.random.nextInt(4)) {
                case 0 -> randomGhast = new AtomicGhast(ctx.level);
                case 1 -> randomGhast = new WraithGhast(ctx.level);
                case 2 -> randomGhast = new TrinityGhast(ctx.level);
                case 3 -> randomGhast = new ElderGhast(ctx.level);
            }
        }

        if (randomGhast != null) {
            ctx.entity.remove();
            randomGhast.addFreshEntity(ctx.location);
            SpawnUtils.setMobCap(randomGhast.getBukkitLivingEntity(), 5, 2, 1);
            return true;
        }

        return false;
    }

    private boolean spawnCustomPiglins(SpawnContext ctx) {
        if (ctx.entity.getType() != EntityType.PIGLIN) return false;
        if (ctx.day < 14) return false;

        CataclysmMob piglinToSpawn = SpawnUtils.getRandomPiglin(ctx);

        if (piglinToSpawn != null) {
            ctx.entity.remove();
            piglinToSpawn.addFreshEntity(ctx.location);
            SpawnUtils.setMobCap(piglinToSpawn.getBukkitLivingEntity(), 15, 3, 1);
            return true;
        }

        return false;
    }

    private boolean spawnCustomWitherSkeletons(SpawnContext ctx) {
        if (ctx.entity.getType() != EntityType.WITHER_SKELETON || ctx.day < 14) return false;

        CataclysmMob randomSkeleton = new Swordmaster(ctx.level);
        if (ctx.random.nextBoolean()) randomSkeleton = new Bowmaster(ctx.level);

        // Special biome check for NetherNightmare
        var ragnarok = Cataclysm.getRagnarok();
        int probability = ctx.random.nextInt(100);

        if (ragnarok != null && ragnarok.getData().getLevel() >= 5) probability+=20;

        if (ctx.biomeName.contains("WITHERED") && probability > 90) randomSkeleton = new NetherNightmare(ctx.level);

        ctx.entity.remove();
        randomSkeleton.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(randomSkeleton.getBukkitLivingEntity(), 8, 3, 1);
        return true;
    }

    private boolean isOnSlab(Location location) {
        return location.add(0, -1, 0).getBlock().getType().name().toLowerCase().contains("slab");
    }
}