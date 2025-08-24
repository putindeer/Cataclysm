package org.cataclysm.game.mob.custom.vanilla.ghast;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.utils.TeleportUtils;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class Ur_Ghast extends CataclysmMob {
    public Ur_Ghast(Level level) {
        super(new TwilightGhast(level), "Ur-Ghast", level);
        super.setHealth(140);
        super.setAttribute(Attributes.SCALE, 3.5);
        super.setListener(new TwilightGhastListener(this));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Ur_Ghast(super.getLevel());
    }

    public void shootFireball() {
        TwilightGhast ghast = (TwilightGhast) super.getEntity();
        Level level = ghast.level();
        net.minecraft.world.entity.LivingEntity target = ghast.getTarget();
        if (target == null) return;

        //? From here to below is just a copy of mc code
        Vec3 viewVector = ghast.getViewVector(1.0F);
        double d2 = target.getX() - (ghast.getX() + viewVector.x * 4.0);
        double d3 = target.getY(0.5) - (0.5 + ghast.getY(0.5));
        double d4 = target.getZ() - (ghast.getZ() + viewVector.z * 4.0);
        Vec3 vec3 = new Vec3(d2, d3, d4);
        if (!ghast.isSilent()) level.levelEvent(null, 1016, ghast.blockPosition(), 0);

        LargeFireball largeFireball = new LargeFireball(level, ghast, vec3.normalize(), ghast.getExplosionPower());
        largeFireball.bukkitYield = (float) (largeFireball.explosionPower = ghast.getExplosionPower());
        largeFireball.setPos(ghast.getX() + viewVector.x * 4.0, ghast.getY(0.5) + 0.5, largeFireball.getZ() + viewVector.z * 4.0);

        PersistentData.set(largeFireball.getBukkitEntity(), "TRINITY_EXTRA_FIREBALL", PersistentDataType.BOOLEAN, true);
        level.addFreshEntity(largeFireball);
    }

    static class TwilightGhast extends Ghast {
        public TwilightGhast(Level level) {
            super(EntityType.GHAST, level);
            super.setExplosionPower(12);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    static class TwilightGhastListener implements Listener {
        private final Ur_Ghast ghast;

        public TwilightGhastListener(Ur_Ghast ghast) {
            this.ghast = ghast;
        }

        @EventHandler
        private void onDamage(EntityDamageEvent event) {
            var entity = event.getEntity();
            if (!(entity instanceof LivingEntity livingEntity)) return;

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.ghast.getMobToken().key())) return;

            var cause = event.getCause();
            if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                var location = livingEntity.getLocation();
                boolean foundTeleport = TeleportUtils.teleportEntityRandomly(livingEntity, 20);

                if (!foundTeleport) {
                    var random = ThreadLocalRandom.current();
                    var newLocation = location.add(random.nextInt(-10, 10), random.nextInt(-3, 3), random.nextInt(-10, 10));
                    livingEntity.teleport(newLocation);
                    livingEntity.setNoDamageTicks(5);
                }
            }
        }

        @EventHandler
        public void fireBallDamagePlayer(EntityDamageByEntityEvent event) {
            if (!(event.getEntity() instanceof Player player)) return;
            if (!(event.getDamager() instanceof Fireball fireball)) return;
            if (!(fireball.getShooter() instanceof org.bukkit.entity.LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.ghast.getMobToken().key())) return;

            PotionEffectType[] types = {PotionEffectType.WITHER, PotionEffectType.BLINDNESS, PotionEffectType.SLOWNESS, PotionEffectType.MINING_FATIGUE, PotionEffectType.NAUSEA};
            for (PotionEffectType type : types) {
                player.addPotionEffect(new PotionEffect(type, 600, ((type == PotionEffectType.WITHER || type == PotionEffectType.SLOWNESS) ? 2 : 0)));
            }
            if (Cataclysm.getDay() >= 21) player.addPotionEffect(new PotionEffect(PotionEffectType.UNLUCK, 600, 0));
        }

        @EventHandler
        public void onHitFireball(ProjectileHitEvent event) {
            if (!(event.getEntity().getShooter() instanceof LivingEntity shooter)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.ghast.getMobToken().key())) return;

            Location hitLocation = event.getEntity().getLocation();
            AreaEffectCloud effectCloud = (AreaEffectCloud) hitLocation.getWorld().spawnEntity(hitLocation, org.bukkit.entity.EntityType.AREA_EFFECT_CLOUD);
            effectCloud.setSource(shooter);
            effectCloud.setRadius(effectCloud.getRadius() * 4);
            effectCloud.addCustomEffect(new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 200, 3), true);
            effectCloud.setDuration(200);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.ghast.getMobToken().key())) return;
            livingEntity.getWorld().dropItemNaturally(livingEntity.getLocation(), CataclysmItems.UR_TEAR.build());
            HandlerList.unregisterAll(this);
        }

        @EventHandler
        public void onShootFireball(ProjectileLaunchEvent event) {
            if (!(event.getEntity().getShooter() instanceof org.bukkit.entity.LivingEntity shooter)) return;
            if (PersistentData.has(event.getEntity(), "TRINITY_EXTRA_FIREBALL", PersistentDataType.BOOLEAN)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.ghast.getMobToken().key())) return;

            for (int i = 0; i < 2; i++) {
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), ghast::shootFireball, 8 * (i + 1));
            }
        }

    }


}
