package org.cataclysm.game.mob.custom.cataclysm.calamity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

import java.util.EnumSet;

public class CalamityGhast extends CataclysmMob {

    public CalamityGhast(Level level) {
        super(new CataclysmGhastEntity(level), "Calamity Ghast", CataclysmColor.CALAMITY, level);
        this.setHealth(60);
        this.setAttribute(Attributes.SCALE, 0.25f);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CalamityGhast(super.getLevel());
    }

    static class CataclysmGhastEntity extends Ghast {
        public CataclysmGhastEntity(Level level) {
            super(EntityType.GHAST, level);
            super.xpReward = 5;
            super.moveControl = new CataclysmGhastEntity.GhastMoveControl(this);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public int getExplosionPower() {
            return 3;
        }

        protected void registerGoals() {
            super.goalSelector.addGoal(5, new CataclysmGhastEntity.RandomFloatAroundGoal(this));
            super.goalSelector.addGoal(7, new CataclysmGhastEntity.GhastLookGoal(this));
            super.goalSelector.addGoal(7, new CataclysmGhastEntity.GhastShootFireballGoal(this));
            super.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, (entity, level) -> Math.abs(entity.getY() - this.getY()) <= (double)4.0F));
        }

        static class GhastLookGoal extends Goal {
            private final Ghast ghast;

            public GhastLookGoal(Ghast ghast) {
                this.ghast = ghast;
                this.setFlags(EnumSet.of(Flag.LOOK));
            }

            public boolean canUse() {
                return true;
            }

            public boolean requiresUpdateEveryTick() {
                return true;
            }

            public void tick() {
                if (this.ghast.getTarget() == null) {
                    Vec3 deltaMovement = this.ghast.getDeltaMovement();
                    this.ghast.setYRot(-((float) Mth.atan2(deltaMovement.x, deltaMovement.z)) * (180F / (float)Math.PI));
                    this.ghast.yBodyRot = this.ghast.getYRot();
                } else {
                    LivingEntity target = this.ghast.getTarget();
                    if (target.distanceToSqr(this.ghast) < (double)4096.0F) {
                        double d1 = target.getX() - this.ghast.getX();
                        double d2 = target.getZ() - this.ghast.getZ();
                        this.ghast.setYRot(-((float)Mth.atan2(d1, d2)) * (180F / (float)Math.PI));
                        this.ghast.yBodyRot = this.ghast.getYRot();
                    }
                }

            }
        }

        static class GhastMoveControl extends MoveControl {
            private final Ghast ghast;
            private int floatDuration;

            public GhastMoveControl(Ghast mob) {
                super(mob);
                this.ghast = mob;
            }

            public void tick() {
                if (super.operation == Operation.MOVE_TO && this.floatDuration-- <= 0) {
                    this.floatDuration = this.floatDuration + this.ghast.getRandom().nextInt(5) + 2;
                    Vec3 vec3 = new Vec3(super.wantedX - this.ghast.getX(), super.wantedY - this.ghast.getY(), super.wantedZ - this.ghast.getZ());
                    double len = vec3.length();
                    vec3 = vec3.normalize();
                    if (this.canReach(vec3, Mth.ceil(len))) {
                        this.ghast.setDeltaMovement(this.ghast.getDeltaMovement().add(vec3.scale(0.1)));
                    } else {
                        super.operation = Operation.WAIT;
                    }
                }

            }

            private boolean canReach(Vec3 pos, int length) {
                AABB boundingBox = this.ghast.getBoundingBox();

                for(int i = 1; i < length; ++i) {
                    boundingBox = boundingBox.move(pos);
                    if (!this.ghast.level().noCollision(this.ghast, boundingBox)) {
                        return false;
                    }
                }

                return true;
            }
        }

        static class GhastShootFireballGoal extends Goal {
            private final Ghast ghast;
            public int chargeTime;
            private static final int FIREBALL_COOLDOWN = 25;

            public GhastShootFireballGoal(Ghast ghast) {
                this.ghast = ghast;
            }

            public boolean canUse() {
                return this.ghast.getTarget() != null;
            }

            public void start() {
                this.chargeTime = 0;
            }

            public void stop() {
                this.ghast.setCharging(false);
            }

            public boolean requiresUpdateEveryTick() {
                return true;
            }

            public void tick() {
                LivingEntity target = this.ghast.getTarget();
                if (target != null) {
                    if (target.distanceToSqr(this.ghast) < 4096.0 && this.ghast.hasLineOfSight(target)) {
                        Level level = this.ghast.level();
                        ++this.chargeTime;

                        if (this.chargeTime == 5 && !this.ghast.isSilent()) {
                            level.levelEvent(null, 1015, this.ghast.blockPosition(), 0);
                        }

                        if (this.chargeTime >= FIREBALL_COOLDOWN) {
                            Vec3 viewVector = this.ghast.getViewVector(1.0F);
                            double dx = target.getX() - (this.ghast.getX() + viewVector.x * 4.0);
                            double dy = target.getY(0.5) - (0.5 + this.ghast.getY(0.5));
                            double dz = target.getZ() - (this.ghast.getZ() + viewVector.z * 4.0);
                            Vec3 direction = new Vec3(dx, dy, dz).normalize();

                            if (!this.ghast.isSilent()) {
                                level.levelEvent(null, 1016, this.ghast.blockPosition(), 0);
                            }

                            SmallFireball fireball = new SmallFireball(level, this.ghast, direction);
                            fireball.setPos(
                                    this.ghast.getX() + viewVector.x * 4.0,
                                    this.ghast.getY(0.5) + 0.5,
                                    this.ghast.getZ() + viewVector.z * 4.0
                            );

                            level.addFreshEntity(fireball);

                            this.chargeTime = 0;
                        }
                    } else if (this.chargeTime > 0) {
                        --this.chargeTime;
                    }

                    this.ghast.setCharging(this.chargeTime > 5);
                }
            }
        }

        static class RandomFloatAroundGoal extends Goal {
            private final Ghast ghast;

            public RandomFloatAroundGoal(Ghast ghast) {
                this.ghast = ghast;
                this.setFlags(EnumSet.of(Flag.MOVE));
            }

            public boolean canUse() {
                MoveControl moveControl = this.ghast.getMoveControl();
                if (!moveControl.hasWanted()) {
                    return true;
                } else {
                    double d = moveControl.getWantedX() - this.ghast.getX();
                    double d1 = moveControl.getWantedY() - this.ghast.getY();
                    double d2 = moveControl.getWantedZ() - this.ghast.getZ();
                    double d3 = d * d + d1 * d1 + d2 * d2;
                    return d3 < (double) 1.0F || d3 > (double) 3600.0F;
                }
            }

            public boolean canContinueToUse() {
                return false;
            }

            public void start() {
                RandomSource random = this.ghast.getRandom();
                double d = this.ghast.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double d1 = this.ghast.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                double d2 = this.ghast.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
                this.ghast.getMoveControl().setWantedPosition(d, d1, d2, 1.0F);
            }
        }
    }
}
