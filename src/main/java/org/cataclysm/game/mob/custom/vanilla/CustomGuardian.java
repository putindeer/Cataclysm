package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Guardian;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.EnumSet;

public class CustomGuardian extends CataclysmMob {


    public CustomGuardian(Level level) {
        super(new CustomGuardianEntity(level), "", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CustomGuardian(super.getLevel());
    }

    static class CustomGuardianEntity extends Guardian {

        public CustomGuardianEntity(Level level) {
            super(EntityType.GUARDIAN, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void registerGoals() {
            MoveTowardsRestrictionGoal moveTowardsRestrictionGoal = new MoveTowardsRestrictionGoal(this, 1.0);
            this.randomStrollGoal = new RandomStrollGoal(this, 1.0, 80);
            super.goalSelector.addGoal(4, this.guardianAttackGoal = new GuardianAttackGoal(this));
            super.goalSelector.addGoal(5, moveTowardsRestrictionGoal);
            super.goalSelector.addGoal(7, this.randomStrollGoal);
            super.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
            super.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Guardian.class, 12.0F, 0.01F));
            super.goalSelector.addGoal(9, new RandomLookAroundGoal(this));
            this.randomStrollGoal.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            moveTowardsRestrictionGoal.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
            super.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, 10, true, false, new PlayerGuardianAttackSelector(this)));
        }

        static class PlayerGuardianAttackSelector implements TargetingConditions.Selector {
            private final Guardian guardian;

            public PlayerGuardianAttackSelector(Guardian guardian) {
                this.guardian = guardian;
            }

            public boolean test(@Nullable LivingEntity entity, @NotNull ServerLevel level) {
                return entity instanceof Player && entity.distanceToSqr(this.guardian) > 9.0;
            }
        }
    }
}
