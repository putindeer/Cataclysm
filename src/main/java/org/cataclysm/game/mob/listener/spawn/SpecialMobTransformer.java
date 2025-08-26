package org.cataclysm.game.mob.listener.spawn;

import org.bukkit.World;
import org.bukkit.entity.Endermite;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.custom.cataclysm.QuantumReactor;
import org.cataclysm.game.mob.custom.cataclysm.arcane.ArcaneEvoker;
import org.cataclysm.game.mob.custom.cataclysm.mirage.*;
import org.cataclysm.game.mob.custom.cataclysm.twisted.TwistedBlaze;
import org.cataclysm.game.mob.custom.cataclysm.twisted.TwistedEnderman;
import org.cataclysm.game.mob.custom.cataclysm.wandering.WanderingFaith;
import org.cataclysm.game.mob.custom.cataclysm.wandering.WanderingSoul;
import org.cataclysm.game.mob.custom.dungeon.monolith.Termite;
import org.cataclysm.game.mob.custom.dungeon.monolith.Trickster;
import org.cataclysm.game.mob.custom.dungeon.temple.Enchanter;
import org.cataclysm.game.mob.custom.dungeon.temple.Headsman;
import org.cataclysm.game.mob.custom.dungeon.temple.Sentinel;
import org.cataclysm.game.mob.custom.vanilla.AggressiveLlama;
import org.cataclysm.game.mob.custom.vanilla.CustomElderGuardian;
import org.cataclysm.game.mob.custom.vanilla.CustomWarden;
import org.cataclysm.game.mob.custom.vanilla.ExplosivePufferfish;
import org.cataclysm.game.mob.custom.vanilla.ghast.Ur_Ghast;
import org.cataclysm.game.mob.custom.vanilla.phantom.ToxicTerror;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.CataclystSkeleton;
import org.cataclysm.game.mob.custom.vanilla.skeleton.wither.NetherNightmare;
import org.cataclysm.game.world.Dimensions;

public class SpecialMobTransformer {

    public boolean replace(SpawnContext ctx) {
        return replacePaleMobs(ctx) || replaceEndMobs(ctx) ||  replaceNetherMobs(ctx) || replaceOverworldMobs(ctx);
    }

    private boolean replacePaleMobs(SpawnContext ctx) {
        if (ctx.entity instanceof Endermite) return false;
        if (!ctx.location.getWorld().equals(Dimensions.PALE_VOID.getWorld())) return false;
        if (Math.abs(ctx.location.getX()) < 100 && Math.abs(ctx.location.getZ()) < 100) { // Avoid spawning mobs in a radius of 100 blocks of 0 0
            ctx.entity.remove();
            return true;
        }

        CataclysmMob mobToSpawn = null;
        int mobsPerPlayer = 20;
        int maxPerChunk = 5;
        CataclysmMob[] mobList = {
                new Headsman(ctx.level),
                new Sentinel(ctx.level),
                new CustomWarden(ctx.level),
                new TwistedEnderman(ctx.level),
                new AggressiveLlama(ctx.level),
                new Termite(ctx.level),
                new NetherNightmare(ctx.level),
                new Ur_Ghast(ctx.level),
                new CataclystSkeleton(ctx.level),
                new ToxicTerror(ctx.level),
                new MirageWhale(ctx.level),
                new WanderingFaith(ctx.level),
                new WanderingSoul(ctx.level),
                new TwistedBlaze(ctx.level)
        };
        int randomIndex = ctx.random.nextInt(mobList.length + 1);
        if (randomIndex < mobList.length) mobToSpawn = mobList[randomIndex];

        ctx.entity.remove();
        LivingEntity livingEntity;
        if (mobToSpawn == null) livingEntity = (LivingEntity) ctx.location.getWorld().spawnEntity(ctx.location, EntityType.CREAKING, CreatureSpawnEvent.SpawnReason.DEFAULT);
        else {
            mobToSpawn.addFreshEntity(ctx.location);
            livingEntity = mobToSpawn.getBukkitLivingEntity();
        }
        switch (livingEntity.getType()) {
            case ENDERMAN -> mobsPerPlayer = mobsPerPlayer / 5;
            case PHANTOM -> {
                mobsPerPlayer = mobsPerPlayer / 6;
                maxPerChunk = 4;

                var fixedLocation = ctx.location.add(0, 30, 0);
                livingEntity.teleport(fixedLocation);
            }
            case SKELETON, ZOMBIE -> {
                mobsPerPlayer = mobsPerPlayer / 6;
            }
            case BLAZE, WITHER_SKELETON -> {
                mobsPerPlayer = mobsPerPlayer / 8;
            }
            case GHAST -> {
                mobsPerPlayer = mobsPerPlayer / 10;
                maxPerChunk = 2;

                ctx.location.add(0, 30, 0);
                livingEntity.teleport(ctx.location);
            }
            case CREAKING, WARDEN -> {
                mobsPerPlayer = mobsPerPlayer / 12;
                maxPerChunk = 1;
            }
            case RAVAGER, WITHER -> {
                mobsPerPlayer = 2;
                maxPerChunk = 1;
            }
        }

        var ragnarok = Cataclysm.getRagnarok();
        if (ragnarok != null && ragnarok.getData().getLevel() >= 9) {
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 1));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        }

        SpawnUtils.setMobCap(livingEntity, mobsPerPlayer, maxPerChunk, 1.0);

        return true;
    }

    private boolean replaceEndMobs(SpawnContext ctx) {
        if (ctx.entity instanceof Endermite) return false;
        if (!ctx.location.getWorld().equals(Dimensions.THE_END.getWorld())) return false;

        CataclysmMob mobToSpawn = null;
        int mobsPerPlayer = 20;
        int maxPerChunk = 5;
        switch (ctx.random.nextInt(10)) {
            case 0, 1, 2, 3 -> mobToSpawn = new MirageEnderman(ctx.level);

            case 4 -> mobToSpawn = new MirageEye(ctx.level);

            case 5, 6, 7 -> {
                mobToSpawn = new MirageSkeleton(ctx.level);
                if (ctx.random.nextBoolean()) mobToSpawn = new MirageBeast(ctx.level);
            }

            case 8, 9 -> {
                mobToSpawn = new MirageCreeper(ctx.level);
                if (ctx.random.nextBoolean()) mobToSpawn = new MirageWhale(ctx.level);
            }
        }

        if (mobToSpawn != null) {
            ctx.entity.remove();
            mobToSpawn.addFreshEntity(ctx.location);
            var livingEntity = mobToSpawn.getBukkitLivingEntity();
            switch (livingEntity.getType()) {
                case ENDERMAN -> mobsPerPlayer = mobsPerPlayer / 2;

                case PHANTOM -> {
                    mobsPerPlayer = mobsPerPlayer / 6;
                    maxPerChunk = 4;
                }

                case SKELETON, ZOMBIE -> {
                    mobsPerPlayer = mobsPerPlayer / 6;
                    maxPerChunk = 2;
                }
                case GHAST, CREEPER -> {
                    mobsPerPlayer = mobsPerPlayer / 10;
                    maxPerChunk = 2;
                }
            }

            var ragnarok = Cataclysm.getRagnarok();
            if (ragnarok != null && ragnarok.getData().getLevel() >= 9) {
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 1));
                livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
            }

            SpawnUtils.setMobCap(livingEntity, mobsPerPlayer, maxPerChunk, 1.0);
        }

        return true;
    }

    private boolean replaceNetherMobs(SpawnContext ctx) {
        if (ctx.day < 21) return false;
        if (ctx.location.getWorld().getEnvironment() != World.Environment.NETHER) return false;

        if (ctx.biomeName.contains("ASH_BARRENS")) {
            if (ctx.random.nextInt(0, 100) < 5) {
                ctx.entity.remove();
                WanderingSoul mobToSpawn = new WanderingSoul(ctx.level);
                mobToSpawn.addFreshEntity(ctx.location);
                SpawnUtils.setMobCap(mobToSpawn.getBukkitLivingEntity(), 5, 2, 1);
                return true;
            }
        }

        if (ctx.random.nextInt(0, 100) >= 85) {
            CataclysmMob mobToSpawn = null;

            switch (ctx.entity.getType()) {
                case GHAST -> mobToSpawn = new Ur_Ghast(ctx.level);
                case WITHER_SKELETON, SKELETON, PIGLIN, ZOMBIFIED_PIGLIN  -> mobToSpawn = new NetherNightmare(ctx.level);
                case PHANTOM -> mobToSpawn = new ToxicTerror(ctx.level);
            }

            var ragnarok = Cataclysm.getRagnarok();
            if (ragnarok != null && ragnarok.getData().getLevel() >= 8) {
                if (ctx.random.nextBoolean()) mobToSpawn = new QuantumReactor(ctx.level);
            }

            if (mobToSpawn != null) {
                ctx.entity.remove();
                mobToSpawn.addFreshEntity(ctx.location);
                SpawnUtils.setMobCap(mobToSpawn.getBukkitLivingEntity(), 8, 2, 1);
                return true;
            }
        }

        return false;
    }

    private boolean replaceOverworldMobs(SpawnContext ctx) {
        if (ctx.day < 21) return false;
        if (ctx.location.getWorld().getEnvironment() != World.Environment.NORMAL) return false;
        if (ctx.location.getY() <= 60) return false;
        if (ctx.random.nextInt(0, 100) >= 70)  {
            CataclysmMob mobToSpawn = null;
            int maxPerPlayer = 15;
            int maxPerChunk = 2;

            if (ctx.entity.getLocation().add(0, -1, 0).getBlock().isLiquid()) {
                mobToSpawn = ctx.random.nextBoolean() ? new CustomElderGuardian(ctx.level) : new ExplosivePufferfish(ctx.level);
            } else if (ctx.random.nextInt(0, 100) < 3 && ctx.location.getY() >= 64 && !ctx.entity.getType().equals(EntityType.PHANTOM)) {
                mobToSpawn = new WanderingFaith(ctx.level);
                maxPerPlayer = 3;
            } else {
                switch (ctx.random.nextInt(4)) {
                    case 0 -> mobToSpawn = new AggressiveLlama(ctx.level);
                    case 1 -> mobToSpawn = new Trickster(ctx.level);
                    case 2 -> mobToSpawn = new ArcaneEvoker(ctx.level);
                    case 3 -> mobToSpawn = new Enchanter(ctx.level);
                }
            }

            if (mobToSpawn != null) {
                ctx.entity.remove();
                mobToSpawn.addFreshEntity(ctx.location);
                mobToSpawn.getBukkitLivingEntity().setPersistent(false);
                SpawnUtils.setMobCap(mobToSpawn.getBukkitLivingEntity(), maxPerPlayer, maxPerChunk, 1);
                return true;
            }
        }
        return false;
    }
}
