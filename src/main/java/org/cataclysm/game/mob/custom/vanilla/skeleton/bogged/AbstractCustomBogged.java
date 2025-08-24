package org.cataclysm.game.mob.custom.vanilla.skeleton.bogged;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.Bogged;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCustomBogged extends Bogged {

    protected AbstractCustomBogged(Level level) {
        super(EntityType.BOGGED, level);
    }

    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
        if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
        return super.hurtServer(level, damageSource, amount);
    }
}
