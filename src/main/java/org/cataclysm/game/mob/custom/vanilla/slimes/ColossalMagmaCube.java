package org.cataclysm.game.mob.custom.vanilla.slimes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.Level;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class ColossalMagmaCube extends CataclysmMob {
    public ColossalMagmaCube(Level level) {
        super(new ColossalMagmaCubeEntity(level), "Colossal Magma Cube", "#dc8e32", level);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 35);
    }

    static class ColossalMagmaCubeEntity extends MagmaCube {
        public ColossalMagmaCubeEntity(Level level) {
            super(EntityType.MAGMA_CUBE, level);
            var random = ThreadLocalRandom.current();
            super.setSize(1 + random.nextInt(10, 15), true);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return null;
    }
}
