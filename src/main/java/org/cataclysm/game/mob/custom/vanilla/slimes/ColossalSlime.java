package org.cataclysm.game.mob.custom.vanilla.slimes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ThreadLocalRandom;

public class ColossalSlime extends CataclysmMob {
    public ColossalSlime(Level level) {
        super(new ColossalSlimeEntity(level), "Colossal Slime", "#599658", level);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 35);
    }

    static class ColossalSlimeEntity extends Slime {
        public ColossalSlimeEntity(Level level) {
            super(EntityType.SLIME, level);

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
