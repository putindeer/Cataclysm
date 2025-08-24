package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.level.Level;
import org.bukkit.attribute.Attribute;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class ExplosivePufferfish extends CataclysmMob {

    public ExplosivePufferfish(Level level) {
        super(new ExplosivePufferfishEntity(level), "Explosive Pufferfish", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ExplosivePufferfish(super.getLevel());
    }


    public static class ExplosivePufferfishEntity extends Pufferfish {

        public ExplosivePufferfishEntity(Level level) {
            super(EntityType.PUFFERFISH, level);
            if (Cataclysm.getDay() >= 21) MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.MAX_HEALTH, 10);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void setPuffState(int puffState) {
            super.setPuffState(puffState);
            if (puffState > 0) {
                this.getBukkitLivingEntity().getWorld().createExplosion(this.getBukkitLivingEntity(), 15);
                this.remove(RemovalReason.KILLED);
            }
        }

    }
}
