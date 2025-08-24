package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class AggressiveSnowGolem extends CataclysmMob {

    public AggressiveSnowGolem(Level level) {
        super(new AggressiveSnowGolemEntity(level), "Aggressive Snow Golem", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new AggressiveSnowGolem(super.getLevel());
    }

    public static class AggressiveSnowGolemEntity extends SnowGolem {

        public AggressiveSnowGolemEntity(Level level) {
            super(EntityType.SNOW_GOLEM, level);
        }

        @Override
        public boolean isSensitiveToWater() {
            return false;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(0, new RangedAttackGoal(this, 1.25, 20, 10.0F));
            super.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }

    }
}
