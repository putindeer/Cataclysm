package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CustomWarden extends CataclysmMob {

    public CustomWarden(Level level) {
        super(new CustomWardenEntity(level), "Warden", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CustomWarden(super.getLevel());
    }


    public static class CustomWardenEntity extends Warden {

        public CustomWardenEntity(Level level) {
            super(EntityType.WARDEN, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean canTargetEntity(@Nullable Entity entity) {
            return entity instanceof Player player && this.level() == entity.level() && EntitySelector.NO_CREATIVE_OR_SPECTATOR.test(entity) && !this.isAlliedTo(entity) && !player.isDeadOrDying() && this.level().getWorldBorder().isWithinBounds(player.getBoundingBox());
        }

    }
}
