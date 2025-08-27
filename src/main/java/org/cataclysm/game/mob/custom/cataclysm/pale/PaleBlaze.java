package org.cataclysm.game.mob.custom.cataclysm.pale;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class PaleBlaze extends CataclysmMob {
    public PaleBlaze(Level level) {
        super(new CalamityBlazeEntity(level), "Calamity Blaze", CataclysmColor.CALAMITY, level);
        this.setHealth(50);
        super.setAttribute(Attributes.SCALE, 1.45F);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new PaleBlaze(super.getLevel());
    }

    static class CalamityBlazeEntity extends Blaze {
        public CalamityBlazeEntity(Level level) {
            super(EntityType.BLAZE, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
}
