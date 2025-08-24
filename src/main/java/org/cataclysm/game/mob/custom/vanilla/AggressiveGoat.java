package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class AggressiveGoat extends CataclysmMob {

    public AggressiveGoat(Level level) {
        super(new AggressiveGoatEntity(level), "Aggressive Goat", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new AggressiveGoat(super.getLevel());
    }

    public static class AggressiveGoatEntity extends Goat {
        public AggressiveGoatEntity(Level level) {
            super(EntityType.GOAT, level);
            this.goalSelector.addGoal(0, new GoatRamGoal(this));
            this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        static class GoatRamGoal extends Goal {
            private final Goat goat;

            public GoatRamGoal(Goat goat) {
                this.goat = goat;
            }

            @Override
            public boolean canUse() {
                return this.goat.getTarget() != null;
            }

            @Override
            public void tick() {
                if (this.goat.getBrain().isActive(Activity.RAM)) return;
                if (this.goat.getTarget() != null) this.goat.ram(goat.getTarget());
            }
        }
    }
}
