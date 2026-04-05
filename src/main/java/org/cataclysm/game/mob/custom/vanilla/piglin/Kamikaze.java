package org.cataclysm.game.mob.custom.vanilla.piglin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class Kamikaze extends CataclysmMob {
    public Kamikaze(Level level) {
        super(new KamikazeEntity(level), "Piglin Kamikaze", "#ed4f1f", level);
        MobUtils.speedBoost(this.getBukkitLivingEntity(), 0.75);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Kamikaze(super.getLevel());
    }

    static class KamikazeEntity extends Piglin {
        public KamikazeEntity(@NotNull Level level) {
            super(EntityType.PIGLIN, level);
            cannotHunt = true;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity source) {
            var livingEntity = this.getTarget();
            if (livingEntity == null) return false;
            var cle = livingEntity.getBukkitLivingEntity();
            cle.getWorld().createExplosion(this.getBukkitLivingEntity(), 6, false, false);
            this.kill(level);
            return super.doHurtTarget(level, source);
        }

        @Override
        public boolean wantsToPickUp(@NotNull ServerLevel level, @NotNull ItemStack stack) {
            return false;
        }

        @Override
        public void readAdditionalSaveData(@NotNull CompoundTag compound) {
            super.readAdditionalSaveData(compound);
            this.allowedBarterItems = new java.util.HashSet<>();
            this.interestItems = new java.util.HashSet<>();
        }
    }
}
