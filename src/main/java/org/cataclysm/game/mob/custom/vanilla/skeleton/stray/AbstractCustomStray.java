package org.cataclysm.game.mob.custom.vanilla.skeleton.stray;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCustomStray extends Stray {

    protected AbstractCustomStray(Level level) {
        super(EntityType.STRAY, level);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
        return super.hurtServer(level, damageSource, amount);
    }

}
