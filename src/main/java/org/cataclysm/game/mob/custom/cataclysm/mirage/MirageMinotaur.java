package org.cataclysm.game.mob.custom.cataclysm.mirage;

import lombok.Getter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftPiglinBrute;
import org.bukkit.event.entity.EntityTargetEvent;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;
import java.util.Objects;

public class MirageMinotaur extends CataclysmMob {
    public MirageMinotaur(Level level) {
        super(new MirageMinotaurEntity(level), "Mirage Minotaur", level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setHealth(2000);
        super.setAttribute(Attributes.SCALE, 1.5);
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 5);
        super.amplifyAttribute(Attributes.MOVEMENT_SPEED, 1.25);
        super.setPregenerationMaterial(Material.BLACK_GLAZED_TERRACOTTA);
    }

    @Override
    public CataclysmMob createInstance() {
        return new MirageMinotaur(super.getLevel());
    }

    static class MirageMinotaurEntity extends PiglinBrute {
        private boolean isCharging = false;
        private boolean isWindingUp = false;
        @Getter private int chargeCooldown = 0;
        @Getter private int windupTicks = 0;
        @Getter private int chargeTicks = 0;
        private Vec3 chargeDirection = Vec3.ZERO;
        private final double chargeSpeed = 4.0;
        private Player lastAttacker = null;

        private static final int WINDUP_DURATION = 30;
        private static final int CHARGE_COOLDOWN_TIME = 100;
        private static final int MAX_CHARGE_TICKS = 5;

        public MirageMinotaurEntity(Level level) {
            super(EntityType.PIGLIN_BRUTE, level);
            CraftPiglinBrute brute = (CraftPiglinBrute) this.getBukkitLivingEntity();
            brute.setImmuneToZombification(true);
            this.setPersistenceRequired(true);
            this.persist = true;
        }

        @Override
        protected void registerGoals() {
            super.registerGoals();
            this.goalSelector.addGoal(1, new ChargeGoal());
            this.targetSelector.addGoal(1, new TrackAttackerGoal());
        }

        @Override
        public @NotNull SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
            return SoundEvents.RAVAGER_HURT;
        }

        @Override
        public @NotNull SoundEvent getDeathSound() {
            return SoundEvents.RAVAGER_DEATH;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE) && (isCharging() || isRunning() || isWindingUp())) {
                return false;
            }

            if (damageSource.getEntity() instanceof Player) {
                this.lastAttacker = (Player) damageSource.getEntity();
                this.setTarget(this.lastAttacker, EntityTargetEvent.TargetReason.CUSTOM);
            }

            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void tick() {
            super.tick();

            if (chargeCooldown > 0) {
                chargeCooldown--;
            }

            if (isWindingUp) {
                windupTicks++;
                if (windupTicks >= WINDUP_DURATION) {
                    startCharge();
                }
            }

            if (isCharging) {
                chargeTicks++;

                if (chargeTicks >= MAX_CHARGE_TICKS) {
                    chargeTicks = 0;
                    endCharge();
                }

                Vec3 chargeVelocity = chargeDirection.multiply(chargeSpeed, this.getDeltaMovement().y, chargeSpeed);
                this.setDeltaMovement(chargeVelocity);

                LivingEntity target = this.getTarget();

                if (target == null) {
                    return;
                }

                double distance = this.distanceToSqr(target);
                boolean closeEnough = distance < 9.0;
                boolean boundingBoxCollision = this.getBoundingBox().inflate(0.5).intersects(target.getBoundingBox());

                if (closeEnough || boundingBoxCollision) {

                    org.bukkit.Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                        target.hurtServer((ServerLevel) target.level(),
                                this.damageSources().mobAttack(this), 80.0f);
                        Vec3 knockbackDirection = target.position().subtract(this.position()).normalize();
                        Vec3 knockback = knockbackDirection.multiply(1.25, 0.6, 1.25);
                        target.setDeltaMovement(target.getDeltaMovement().add(knockback));
                        target.hasImpulse = true;

                        if (target.getBukkitLivingEntity() instanceof org.bukkit.entity.Player player) {
                            if (player.isBlocking()) {
                                player.setCooldown(Material.SHIELD, 200);
                            }
                        }

                        if (target instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
                            serverPlayer.connection.send(new net.minecraft.network.protocol.game.ClientboundSetEntityMotionPacket(
                                    serverPlayer.getId(), target.getDeltaMovement()
                            ));
                        }
                    }, 1L);
                    endCharge();
                }
            }
        }

        private boolean isRunning() {
            return this.getDeltaMovement().horizontalDistanceSqr() > 0.1;
        }

        private void startWindup() {
            if (chargeCooldown > 0 || isWindingUp || isCharging) {
                return;
            }

            LivingEntity target = this.getTarget();
            if (target == null) {
                return;
            }

            isWindingUp = true;
            windupTicks = 0;
            this.getNavigation().stop();
            Vec3 targetPos = target.position();
            Vec3 myPos = this.position();
            chargeDirection = targetPos.subtract(myPos).normalize();
            this.playSound(SoundEvents.RAVAGER_ROAR, 1.0F, 1.0F);
        }

        private void startCharge() {
            isWindingUp = false;
            isCharging = true;
            windupTicks = 0;

            Vec3 chargeVelocity = chargeDirection.multiply(chargeSpeed, 0.0, chargeSpeed);
            this.setDeltaMovement(chargeVelocity);

            this.getNavigation().stop();
            this.setNoAi(false);
            this.playSound(SoundEvents.RAVAGER_ATTACK, 1.0F, 0.8F);
        }

        private void endCharge() {
            isCharging = false;
            chargeCooldown = CHARGE_COOLDOWN_TIME;
            chargeDirection = Vec3.ZERO;

            this.getNavigation().recomputePath();
            this.playSound(SoundEvents.ANVIL_LAND, 0.8F, 1.2F);
        }

        @Override
        public void addAdditionalSaveData(@NotNull CompoundTag compound) {
            super.addAdditionalSaveData(compound);
            compound.putBoolean("IsCharging", isCharging());
            compound.putBoolean("IsWindingUp", isWindingUp());
            compound.putInt("ChargeCooldown", getChargeCooldown());
            compound.putInt("WindupTicks", getWindupTicks());
            compound.putInt("ChargeTicks", getChargeTicks());
        }

        @Override
        public void readAdditionalSaveData(@NotNull CompoundTag compound) {
            super.readAdditionalSaveData(compound);
            this.isCharging = compound.getBooleanOr("IsCharging", false);
            this.isWindingUp = compound.getBooleanOr("IsWindingUp", false);
            this.chargeCooldown = compound.getIntOr("ChargeCooldown", 0);
            this.windupTicks = compound.getIntOr("WindupTicks", 0);
            this.chargeTicks = compound.getIntOr("ChargeTicks", 0);
        }

        public boolean isCharging() {
            return isCharging;
        }

        public boolean isWindingUp() {
            return isWindingUp;
        }

        private class ChargeGoal extends Goal {

            public ChargeGoal() {
                this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            }

            @Override
            public boolean canUse() {
                LivingEntity target = MirageMinotaurEntity.this.getTarget();
                if (target == null || !target.isAlive()) {
                    return false;
                }

                if (chargeCooldown > 0 || isCharging || isWindingUp) {
                    return false;
                }

                double distance = MirageMinotaurEntity.this.distanceToSqr(target);
                return distance < (256.0 * 2);
            }

            @Override
            public boolean canContinueToUse() {
                return isWindingUp || isCharging;
            }

            @Override
            public void start() {
                startWindup();
            }

            @Override
            public void stop() {
                if (isCharging) {
                    endCharge();
                }
                isWindingUp = false;
                windupTicks = 0;
            }

            @Override
            public void tick() {
                LivingEntity target = MirageMinotaurEntity.this.getTarget();
                if (target != null && !isCharging) {
                    MirageMinotaurEntity.this.getLookControl().setLookAt(target, 30.0F, 30.0F);
                }
            }
        }

        private class TrackAttackerGoal extends HurtByTargetGoal {
            public TrackAttackerGoal() {
                super(MirageMinotaurEntity.this);
            }

            @Override
            public boolean canUse() {
                if (lastAttacker != null && lastAttacker.isAlive()) {
                    LivingEntity currentTarget = MirageMinotaurEntity.this.getTarget();
                    if (currentTarget != lastAttacker) {
                        return true;
                    }
                }
                return super.canUse();
            }

            @Override
            public void start() {
                if (lastAttacker != null && lastAttacker.isAlive() && Objects.requireNonNull(lastAttacker.gameMode()).isSurvival()) {
                    MirageMinotaurEntity.this.setTarget(lastAttacker, EntityTargetEvent.TargetReason.CUSTOM);
                }
                super.start();
            }
        }
    }

}
