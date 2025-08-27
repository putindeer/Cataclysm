package org.cataclysm.game.mob.custom.cataclysm.pale;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class PaleWhale extends CataclysmMob {
    public PaleWhale(Level level) {
        super(new MirageWhaleEntity(level), "Pale Whale", CataclysmColor.PALE, level);
        this.setHealth(65);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new PaleWhale(super.getLevel());
    }

    static class MirageWhaleEntity extends Ghast {
        public MirageWhaleEntity(Level level) {
            super(EntityType.GHAST, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public int getExplosionPower() {
            return ThreadLocalRandom.current().nextInt(7, 13);
        }
    }

}

