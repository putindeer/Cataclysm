package org.cataclysm.game.mob.listener.spawn;

import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.custom.vanilla.*;
import org.cataclysm.game.mob.custom.vanilla.enhanced.BlackSpider;
import org.cataclysm.game.mob.custom.vanilla.ghast.AtomicGhast;
import org.cataclysm.game.mob.custom.vanilla.ghast.ElderGhast;
import org.cataclysm.game.mob.custom.vanilla.ghast.TrinityGhast;
import org.cataclysm.game.mob.custom.vanilla.ghast.WraithGhast;
import org.cataclysm.game.mob.custom.vanilla.phantom.PhantomWyrm;
import org.cataclysm.game.mob.custom.vanilla.phantom.ToxicTerror;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.ArbalistSkeleton;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.ArcaneSkeleton;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.CataclystSkeleton;
import org.cataclysm.game.mob.custom.vanilla.skeleton.standard.WarlockSkeleton;
import org.cataclysm.game.mob.custom.vanilla.skeleton.bogged.ArbalistBogged;
import org.cataclysm.game.mob.custom.vanilla.skeleton.bogged.ArcaneBogged;
import org.cataclysm.game.mob.custom.vanilla.skeleton.bogged.CataclystBogged;
import org.cataclysm.game.mob.custom.vanilla.skeleton.bogged.WarlockBogged;
import org.cataclysm.game.mob.custom.vanilla.skeleton.stray.ArbalistStray;
import org.cataclysm.game.mob.custom.vanilla.skeleton.stray.ArcaneStray;
import org.cataclysm.game.mob.custom.vanilla.skeleton.stray.CataclystStray;
import org.cataclysm.game.mob.custom.vanilla.skeleton.stray.WarlockStray;
import org.cataclysm.game.mob.custom.vanilla.slimes.ColossalMagmaCube;
import org.cataclysm.game.mob.custom.vanilla.slimes.ColossalSlime;
import org.cataclysm.game.mob.custom.vanilla.slimes.GoldenMagmaCube;
import org.cataclysm.game.mob.utils.MobUtils;
import org.cataclysm.game.world.Dimensions;

public class VanillaMobTransformer {

    public boolean transform(CreatureSpawnEvent event, SpawnContext ctx) {
        return switch (ctx.entity.getType()) {
            case PIG -> transformPig(ctx);
            case CHICKEN -> transformChicken(ctx);
            case CREEPER -> transformCreeper(ctx);
            case PHANTOM -> transformPhantom(ctx);
            case SLIME -> transformSlime(event, ctx);
            case MAGMA_CUBE -> transformMagmaCube(event, ctx);
            case DROWNED -> transformDrowned(ctx);
            case RABBIT -> transformRabbit(ctx);
            case ENDERMAN -> transformEnderman(ctx);
            case IRON_GOLEM -> transformIronGolem(ctx);
            case HOGLIN -> transformHoglin(ctx);
            case ZOMBIFIED_PIGLIN -> transformPiglin(ctx);
            case SKELETON, STRAY, BOGGED -> transformSkeleton(ctx);
            case SQUID -> transformSquid(ctx);
            case GUARDIAN -> transformGuardian(ctx);
            case ELDER_GUARDIAN -> transformElderGuardian(ctx);
            case FROG -> transformFrog(ctx);
            case ENDERMITE -> transformEndermite(ctx);
            case SPIDER, CAVE_SPIDER -> transformSpiders(ctx);
            case LLAMA -> transformLlama(ctx);
            case ARMADILLO -> transformArmadillo(ctx);
            case SNOW_GOLEM -> transformSnowGolem(ctx);
            case GOAT -> transformGoat(ctx);
            case PUFFERFISH -> transformPufferfish(ctx);
            case COD, SALMON, TROPICAL_FISH -> transformFish(ctx);
            case AXOLOTL -> transformAxolotl(ctx);
            case CREAKING -> transformCreaking(ctx);
            case CAMEL -> transformCamel(ctx);
            case BREEZE -> transformBreeze(ctx);
            default -> false;
        };
    }

    private boolean transformBreeze(SpawnContext ctx) {
        if (ctx.day < 21) return false;
        ctx.entity.remove();
        CataclysmMob arcaneBreeze = new ArcaneStray(ctx.level);
        arcaneBreeze.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(arcaneBreeze.getBukkitLivingEntity(), 15, 2, 1);
        return true;
    }

    private boolean transformEndermite(SpawnContext ctx) {
        if (ctx.day < 14) return false;
        MobUtils.damageBoost(ctx.entity, 10);
        MobUtils.speedBoost(ctx.entity, 3);
        MobUtils.healthBoost(ctx.entity, 3);
        return true;
    }

    private boolean transformFrog(SpawnContext ctx) {
        if (ctx.day < 14) return false;
        ctx.entity.remove();
        CataclysmMob explosiveFrog = new ExplosiveFrog(ctx.level);
        explosiveFrog.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(explosiveFrog.getBukkitLivingEntity(), 15, 2, 1);
        return true;
    }

    private boolean transformCreaking(SpawnContext ctx) {
        var creaking = (Creaking) ctx.entity;
        MobUtils.damageBoost(creaking, 999);

        if (ctx.day >= 21) {
            MobUtils.multiplyAttribute(creaking, Attribute.MOVEMENT_SPEED, 1.5);
            MobUtils.healthBoost(creaking, 15);
        }

        SpawnUtils.setMobCap(creaking, 15, 3, 1);
        return true;
    }

    private boolean transformArmadillo(SpawnContext ctx) {
        if (ctx.day < 21) return false;
        ctx.entity.remove();
        CataclysmMob explosiveArmadillo = new ExplosiveArmadillo(ctx.level);
        explosiveArmadillo.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(explosiveArmadillo.getBukkitLivingEntity(), 15, 2, 1);
        return true;
    }

    private boolean transformLlama(SpawnContext ctx) {
        ctx.entity.remove();
        CataclysmMob aggressiveLlama = new AggressiveLlama(ctx.level);
        aggressiveLlama.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(aggressiveLlama.getBukkitLivingEntity(), 15, 2, 1);
        return true;
    }

    private boolean transformGoat(SpawnContext ctx) {
        ctx.entity.remove();
        CataclysmMob aggressiveGoat = new AggressiveGoat(ctx.level);
        aggressiveGoat.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(aggressiveGoat.getBukkitLivingEntity(), 15, 2, 1);
        return true;
    }

    private boolean transformSnowGolem(SpawnContext ctx) {
        ctx.entity.remove();
        CataclysmMob aggressiveSnowGolem = new AggressiveSnowGolem(ctx.level);
        if (ctx.day >= 21 && ctx.random.nextBoolean()) aggressiveSnowGolem = new GiantSnowGolem(ctx.level);
        aggressiveSnowGolem.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(aggressiveSnowGolem.getBukkitLivingEntity(), 15, 2, 1);
        return true;
    }

    private boolean transformPufferfish(SpawnContext ctx) {
        if (ctx.day < 14) return false;
        spawnPufferfish(ctx);
        return true;
    }

    private boolean transformAxolotl(SpawnContext ctx) {
        if (ctx.day < 21) return false;
        spawnPufferfish(ctx);
        return true;
    }

    private boolean transformFish(SpawnContext ctx) {
        if (ctx.day < 14) return false;

        if (ctx.random.nextInt(0, 100) >= 95) {
            spawnPufferfish(ctx);
            return true;
        }

        return false;
    }

    private boolean transformPiglin(SpawnContext ctx) {
        if (ctx.day >= 14) {
            ctx.entity.remove();
            var ent = ctx.location.getWorld().spawnEntity(ctx.location, EntityType.PIGLIN_BRUTE, CreatureSpawnEvent.SpawnReason.DEFAULT);
            ent.setPersistent(false);
            SpawnUtils.setMobCap(ent, 5, 2, 1);
            return true;
        }
        return false;
    }

    private boolean transformPig(SpawnContext ctx) {
        if (ctx.day >= 7 && ctx.random.nextInt(100) < 40) {
            ctx.entity.remove();
            var ent = ctx.location.getWorld().spawnEntity(ctx.location, EntityType.ZOMBIFIED_PIGLIN, CreatureSpawnEvent.SpawnReason.DEFAULT);
            SpawnUtils.setMobCap(ent, 25, 3, 1);
            return true;
        }

        return false;
    }

    private boolean transformChicken(SpawnContext ctx) {
        if (ctx.day >= 7) {
            int jockeyPercentage = ctx.day >= 14 ? 100 : 50;
            if (ctx.random.nextInt(100) < jockeyPercentage) {
                Zombie jockey = (Zombie) ctx.entity.getWorld().spawnEntity(ctx.location, EntityType.ZOMBIE, CreatureSpawnEvent.SpawnReason.CUSTOM);
                jockey.setAge(-1);
                ctx.entity.addPassenger(jockey);
                return true;
            }
        }
        return false;
    }

    private boolean transformSkeleton(SpawnContext ctx) {
        EntityType finalEntity = ctx.entity.getType();
        var ragnarok = Cataclysm.getRagnarok();

        if (ctx.day >= 7 && MobUtils.isInColdBiome(ctx.entity)) finalEntity = EntityType.STRAY;
        if (ctx.day >= 14 && MobUtils.isInWarmBiome(ctx.entity)) finalEntity = EntityType.BOGGED;

        if (ragnarok != null && ragnarok.getData().getLevel() >= 5) {
            switch (ctx.random.nextInt(3)) {
                case 0 -> finalEntity = EntityType.SKELETON;
                case 1 -> finalEntity = EntityType.STRAY;
                case 2 -> finalEntity = EntityType.BOGGED;
            }
        }

        if (ctx.day >= 14) {
            CataclysmMob skeletonToSpawn = null;
            switch (finalEntity) {
                case SKELETON -> {
                    switch (ctx.random.nextInt(4)) {
                        case 0 -> skeletonToSpawn = new CataclystSkeleton(ctx.level);
                        case 1 -> skeletonToSpawn = new ArbalistSkeleton(ctx.level);
                        case 2 -> skeletonToSpawn = new ArcaneSkeleton(ctx.level);
                        case 3 -> skeletonToSpawn = new WarlockSkeleton(ctx.level);
                    }
                }

                case STRAY -> {
                    switch (ctx.random.nextInt(4)) {
                        case 0 -> skeletonToSpawn = new CataclystStray(ctx.level);
                        case 1 -> skeletonToSpawn = new ArbalistStray(ctx.level);
                        case 2 -> skeletonToSpawn = new ArcaneStray(ctx.level);
                        case 3 -> skeletonToSpawn = new WarlockStray(ctx.level);
                    }
                }

                case BOGGED  -> {
                    switch (ctx.random.nextInt(4)) {
                        case 0 -> skeletonToSpawn = new CataclystBogged(ctx.level);
                        case 1 -> skeletonToSpawn = new ArbalistBogged(ctx.level);
                        case 2 -> skeletonToSpawn = new ArcaneBogged(ctx.level);
                        case 3 -> skeletonToSpawn = new WarlockBogged(ctx.level);
                    }
                }
            }

            if (skeletonToSpawn != null) {
                ctx.entity.remove();
                skeletonToSpawn.addFreshEntity(ctx.location);
                SpawnUtils.setMobCap(skeletonToSpawn.getBukkitLivingEntity(), 8, 3, 1);
                ctx.entity = skeletonToSpawn.getBukkitLivingEntity();
            }
        }

        return false;
    }

    private boolean transformCreeper(SpawnContext ctx) {
        Creeper creeper = (Creeper) ctx.entity;

        if (ctx.day >= 7) {
            creeper.setPowered(true);
        }

        if (ctx.day >= 14) {
            int fuseDivisor = ctx.day >= 21 ? 4 : 2;
            creeper.setMaxFuseTicks(creeper.getMaxFuseTicks() / fuseDivisor);
            creeper.setFuseTicks(creeper.getFuseTicks() / fuseDivisor);
        }

        if (ctx.day >= 21) {
            creeper.setExplosionRadius(creeper.getExplosionRadius() * 2);
        }

        SpawnUtils.setMobCap(creeper, 10, 3, 1);
        return false;
    }

    private boolean transformPhantom(SpawnContext ctx) {
        if (ctx.day < 7) return false;
        var ragnarok = Cataclysm.getRagnarok();
        CataclysmMob phantom = new PhantomWyrm(ctx.level);
        int probability = ctx.random.nextInt(100);
        int mobCap = ctx.day >= 21 ? 20 : 10;

        if (ragnarok != null) {
            var level = ragnarok.getData().getLevel();
            if (level >= 5) {
                probability += 20;
                if (probability >= 90) phantom = new ToxicTerror(ctx.level);
            }

            if (level >= 7) {
                if (level >= 8) probability += 20;
                if (ctx.entity.getWorld().equals(Dimensions.OVERWORLD.getWorld()) && probability >= 75) {
                    switch (ctx.random.nextInt(4)) {
                        case 0 -> phantom = new TrinityGhast(ctx.level);
                        case 1 -> phantom = new WraithGhast(ctx.level);
                        case 2 -> phantom = new AtomicGhast(ctx.level);
                        case 3 -> phantom = new ElderGhast(ctx.level);
                    }
                }
            }

        }

        if (ctx.day >= 14) {
            if (ctx.biomeName.contains("TOXIC") && probability >= 90) phantom = new ToxicTerror(ctx.level);
        }

        ctx.entity.remove();
        phantom.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(phantom.getBukkitLivingEntity(), mobCap, 4, 1);

        if (ctx.day >= 21) {
            if (ctx.location.getWorld().getEnvironment() == World.Environment.NORMAL) {
                for (int x = 0; x < 2; x++) {
                    phantom = ctx.random.nextBoolean() ? new PhantomWyrm(ctx.level) : new ToxicTerror(ctx.level);
                    phantom.addFreshEntity(ctx.location);
                }
            }
        }

        return true;
    }

    private boolean transformSlime(CreatureSpawnEvent event, SpawnContext ctx) {
        if (ctx.day < 7) return false;

        if (ctx.location.getBlockY() >= 64 && event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) {
            ctx.entity.remove();
            var slime = new ColossalSlime(ctx.level);
            slime.addFreshEntity(ctx.location);
            SpawnUtils.setMobCap(slime.getBukkitLivingEntity(), 5, 2, 1);

            if (ctx.day >= 21) {
                MobUtils.multiplyAttribute(slime.getBukkitLivingEntity(), Attribute.MOVEMENT_SPEED, 2);
                MobUtils.multiplyAttribute(slime.getBukkitLivingEntity(), Attribute.ATTACK_DAMAGE, 2);
            }

            return true;
        }

        return false;
    }

    private boolean transformMagmaCube(CreatureSpawnEvent event, SpawnContext ctx) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SLIME_SPLIT) return false;
        if (ctx.day >= 7 && ctx.location.getBlockY() >= 64) {
            ctx.entity.remove();
            var mobToSpawn = new ColossalMagmaCube(ctx.level);
            var livingEntity = mobToSpawn.getBukkitLivingEntity();
            mobToSpawn.addFreshEntity(ctx.location);
            SpawnUtils.setMobCap(livingEntity, 5, 2, 1);
            if (ctx.day >= 21) {
                MobUtils.multiplyAttribute(livingEntity, Attribute.MOVEMENT_SPEED, 2);
                MobUtils.multiplyAttribute(livingEntity, Attribute.ATTACK_DAMAGE, 2);
            }

            return true;
        }
        var ragnarok = Cataclysm.getRagnarok();
        int probability = ctx.random.nextInt(100);

        if (ragnarok != null && ragnarok.getData().getLevel() >= 5) probability+=20;

        if (ctx.day >= 14 && ctx.biomeName.contains("BASALT") && probability > 90) {

            new GoldenMagmaCube(ctx.level).addFreshEntity(ctx.location);
            return true;
        }

        return false;
    }

    private boolean transformDrowned(SpawnContext ctx) {
        if (ctx.day >= 14) {
            if (ctx.entity.getEquipment() != null) {
                ctx.entity.getEquipment().setItemInMainHand(CataclysmItems.ARCANE_TRIDENT.build());
            }
        }
        return false;
    }

    private boolean transformRabbit(SpawnContext ctx) {
        if (ctx.day >= 14) {
            ((Rabbit) ctx.entity).setRabbitType(Rabbit.Type.THE_KILLER_BUNNY);
            MobUtils.damageBoost(ctx.entity, 2);
        }
        return false;
    }

    private boolean transformEnderman(SpawnContext ctx) {
        if (ctx.day >= 14) {
            if (ctx.location.getWorld() == Dimensions.NETHER.getWorld() || ctx.day < 21) {
                ctx.entity.remove();
                AggressiveEnderMan aggressiveEnderman = new AggressiveEnderMan(ctx.level);
                aggressiveEnderman.addFreshEntity(ctx.location);
                SpawnUtils.setMobCap(aggressiveEnderman.getBukkitLivingEntity(), 20, 2, 1);
                ctx.entity = aggressiveEnderman.getBukkitLivingEntity();
            }
        }
        return false;
    }

    private boolean transformSpiders(SpawnContext ctx) {
        ctx.entity.remove();
        BlackSpider blackSpider = new BlackSpider(ctx.level);
        blackSpider.addFreshEntity(ctx.location);
        ctx.entity = blackSpider.getBukkitLivingEntity();
        SpawnUtils.setMobCap(blackSpider.getBukkitLivingEntity(), 10, 2, 1);
        return false;
    }

    private boolean transformIronGolem(SpawnContext ctx) {
        if (ctx.day < 14) return false;
        ctx.entity.remove();
        return true;
    }

    private boolean transformHoglin(SpawnContext ctx) {
        if (ctx.day >= 14) {
            MobUtils.multiplyAttribute(ctx.entity, Attribute.ATTACK_DAMAGE, 2);

            // 50% chance to add custom piglin jockey
            if (ctx.random.nextInt(100) < 50) {
                var piglinToSpawn = SpawnUtils.getRandomPiglin(ctx);

                if (piglinToSpawn != null) {
                    piglinToSpawn.addFreshEntity(ctx.location);
                    ctx.entity.addPassenger(piglinToSpawn.getBukkitLivingEntity());
                }
            }
        }

        if (ctx.day >= 21) {
            // 20% chance to replace with Zoglin
            if (ctx.random.nextInt(100) < 20) {
                ctx.entity.remove();
                var zoglin = ctx.location.getWorld().spawnEntity(ctx.location, EntityType.ZOGLIN, CreatureSpawnEvent.SpawnReason.CUSTOM);
                MobUtils.multiplyAttribute((LivingEntity) zoglin, Attribute.ATTACK_DAMAGE, 50); //oneshot
                MobUtils.multiplyAttribute((LivingEntity) zoglin, Attribute.SCALE, 2); //oneshot

                SpawnUtils.setMobCap(zoglin, 15, 2, 1);
            } else {
                // Triple damage if not replaced
                MobUtils.multiplyAttribute(ctx.entity, Attribute.ATTACK_DAMAGE, 3);
            }
        }

        return false;
    }

    private boolean transformSquid(SpawnContext ctx) {
        if (ctx.day >= 14) {
            ctx.entity.remove();
            ctx.location.getWorld().spawnEntity(ctx.location, EntityType.GUARDIAN, CreatureSpawnEvent.SpawnReason.DEFAULT);
        }

        return false;
    }

    private boolean transformGuardian(SpawnContext ctx) {
        if (ctx.day >= 14) {
            // 20% chance to replace with Elder Guardian
            if (ctx.random.nextInt(100) < 20) {
                CataclysmMob onlyPlayerElderGuardian = new CustomElderGuardian(ctx.level);
                onlyPlayerElderGuardian.addFreshEntity(ctx.location);
                onlyPlayerElderGuardian.getBukkitLivingEntity().setPersistent(false);
                SpawnUtils.setMobCap(onlyPlayerElderGuardian.getBukkitLivingEntity(), 5, 2, 1);
            } else {
                // Replace with CustomGuardian
                CataclysmMob onlyPlayerGuardian = new CustomGuardian(ctx.level);
                onlyPlayerGuardian.addFreshEntity(ctx.location);
                onlyPlayerGuardian.getBukkitLivingEntity().setPersistent(false);
                SpawnUtils.setMobCap(onlyPlayerGuardian.getBukkitLivingEntity(), 5, 2, 1);
            }
            ctx.entity.remove();
            return true;
        }

        return false;
    }

    private boolean transformElderGuardian(SpawnContext ctx) {
        if (ctx.day < 14) return false;
        ctx.entity.remove();
        CataclysmMob onlyPlayerElderGuardian = new CustomElderGuardian(ctx.level);
        onlyPlayerElderGuardian.addFreshEntity(ctx.location);
        onlyPlayerElderGuardian.getBukkitLivingEntity().setPersistent(false);
        SpawnUtils.setMobCap(onlyPlayerElderGuardian.getBukkitLivingEntity(), 5, 2, 1);
        return true;
    }

    private void spawnPufferfish(SpawnContext ctx) {
        ctx.entity.remove();
        CataclysmMob explosivePufferfish = new ExplosivePufferfish(ctx.level);
        explosivePufferfish.addFreshEntity(ctx.location);
        explosivePufferfish.getBukkitLivingEntity().setPersistent(false);
        SpawnUtils.setMobCap(explosivePufferfish.getBukkitLivingEntity(), 15, 2, 1);
    }

    private boolean transformCamel(SpawnContext ctx) {
        if (ctx.day < 21) return false;
        ctx.entity.remove();
        CataclysmMob aggressiveLlama = new AggressiveLlama(ctx.level);
        aggressiveLlama.addFreshEntity(ctx.location);
        SpawnUtils.setMobCap(aggressiveLlama.getBukkitLivingEntity(), 15, 2, 1);
        return true;
    }

}
