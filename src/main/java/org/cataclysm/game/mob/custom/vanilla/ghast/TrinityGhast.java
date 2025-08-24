package org.cataclysm.game.mob.custom.vanilla.ghast;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Fireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.global.utils.security.CataclysmToken;
import org.jetbrains.annotations.NotNull;

public class TrinityGhast extends CataclysmMob {

    public TrinityGhast(Level level) {
        super(new TrinityGhastEntity(level), "Trinity Ghast", level);
        super.setHealth(Cataclysm.getDay() < 14 ? 50 : 100);
        super.setAttribute(Attributes.SCALE, 0.85);
        super.setListener(new TrinityGhastListener(this));
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(ItemStack.of(Material.GHAST_TEAR), 1, 1, 1))));
    }

    public void shootFireball() {
        TrinityGhastEntity ghast = (TrinityGhastEntity) super.getEntity();
        Level level = ghast.level();
        LivingEntity target = ghast.getTarget();
        if (target == null) return;

        Vec3 viewVector = ghast.getViewVector(1.0F);
        double baseX = target.getX() - (ghast.getX() + viewVector.x * 4.0);
        double baseY = target.getY(0.5) - (0.5 + ghast.getY(0.5));
        double baseZ = target.getZ() - (ghast.getZ() + viewVector.z * 4.0);
        Vec3 baseDirection = new Vec3(baseX, baseY, baseZ).normalize();

        if (!ghast.isSilent()) level.levelEvent(null, 1016, ghast.blockPosition(), 0);

        Vec3 ghastVelocity = ghast.getDeltaMovement();

        double spawnX = ghast.getX() + viewVector.x * 4.0 + ghastVelocity.x * 2.0;
        double spawnY = ghast.getY(0.5) + 0.5 + ghastVelocity.y * 2.0;
        double spawnZ = ghast.getZ() + viewVector.z * 4.0 + ghastVelocity.z * 2.0;


        double[] angleOffsets = {0.0, -0.5, 0.5};
        if (Cataclysm.getDay() < 21) angleOffsets = new double[]{0.0};
        for (double angleOffset : angleOffsets) {
            Vec3 spreadDirection = rotateVectorAroundY(baseDirection, angleOffset);

            double forwardOffset = Math.abs(angleOffset) > 0.1 ? 1.5 : 0.0;
            double offsetX = spawnX + spreadDirection.x * forwardOffset;
            double offsetZ = spawnZ + spreadDirection.z * forwardOffset;

            LargeFireball largeFireball = new LargeFireball(level, ghast, spreadDirection, ghast.getExplosionPower());
            largeFireball.bukkitYield = (float) (largeFireball.explosionPower = ghast.getExplosionPower());
            largeFireball.setPos(offsetX, spawnY, offsetZ);

            PersistentData.set(largeFireball.getBukkitEntity(), "TRINITY_EXTRA_FIREBALL", PersistentDataType.BOOLEAN, true);
            level.addFreshEntity(largeFireball);
        }
    }

    private Vec3 rotateVectorAroundY(Vec3 vector, double angle) {
        if (angle == 0.0) return vector;

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);

        double newX = vector.x * cos - vector.z * sin;
        double newZ = vector.x * sin + vector.z * cos;

        return new Vec3(newX, vector.y, newZ);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new TrinityGhast(super.getLevel());
    }

    static class TrinityGhastEntity extends Ghast {
        public TrinityGhastEntity(Level level) {
            super(EntityType.GHAST, level);
            super.setExplosionPower(Cataclysm.getDay() < 14 ? 3 : 5);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }
    
    static class TrinityGhastListener implements Listener {

        private final TrinityGhast trinityGhast;

        public TrinityGhastListener(TrinityGhast trinityGhast) {
            this.trinityGhast = trinityGhast;
        }
        
        @EventHandler
        public void onShootFireball(ProjectileLaunchEvent event) {
            if (!(event.getEntity().getShooter() instanceof org.bukkit.entity.LivingEntity shooter)) return;
            if (PersistentData.has(event.getEntity(), "TRINITY_EXTRA_FIREBALL", PersistentDataType.BOOLEAN)) return;

            CataclysmToken token = CataclysmMob.getToken(shooter);
            if (token == null || !token.key().equals(this.trinityGhast.getMobToken().key())) return;
            event.setCancelled(true);
            for (int i = 0; i < 3; i++) {
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), trinityGhast::shootFireball, 16 * i);
            }
        }

        @EventHandler
        public void onFireballDamageFireball(EntityDamageByEntityEvent event) {
            if (!(event.getDamager() instanceof Fireball damagerFireball)) return;
            if (!(event.getEntity() instanceof Fireball targetFireball)) return;
            event.setCancelled(true);
        }

        @EventHandler
        public void onFireballDamageFireball(ProjectileHitEvent event) {
            if (!(event.getHitEntity() instanceof Fireball damagerFireball)) return;
            if (!(event.getEntity() instanceof Fireball targetFireball)) return;
            event.setCancelled(true);
        }

        @EventHandler
        public void onDeath(EntityDeathEvent event) {
            org.bukkit.entity.LivingEntity livingEntity = event.getEntity();

            CataclysmToken token = CataclysmMob.getToken(livingEntity);
            if (token == null || !token.key().equals(this.trinityGhast.getMobToken().key())) return;

            HandlerList.unregisterAll(this);
        }
    }
}
