package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.armadillo.Armadillo;
import net.minecraft.world.level.Level;
import org.bukkit.attribute.Attribute;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class ExplosiveArmadillo extends CataclysmMob {

    public ExplosiveArmadillo(Level level) {
        super(new ExplosiveArmadilloEntity(level), "Explosive Armadillo", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ExplosiveArmadillo(super.getLevel());
    }

    public static class ExplosiveArmadilloEntity extends Armadillo {

        public ExplosiveArmadilloEntity(Level level) {
            super(EntityType.ARMADILLO, level);
            MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.MAX_HEALTH, 10);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void switchToState(@NotNull ArmadilloState state) {
            if (state == ArmadilloState.SCARED) this.getBukkitLivingEntity().getWorld().createExplosion(this.getBukkitLivingEntity(), 20, false, true);
            super.switchToState(state);
        }

    }
}
