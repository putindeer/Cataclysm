package org.cataclysm.game.mob.custom.dungeon.monolith;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.level.Level;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class Termite extends CataclysmMob {

    public Termite(Level level) {
        super(new TermiteEntity(level), "Termite", level);
        super.setHealth(1);
        super.setAttribute(Attributes.SCALE, 0.1);
        MobUtils.damageBoost(this.getBukkitLivingEntity(), 1);
        if (Cataclysm.getDay() >= 21) super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 2);
    }

    public static class TermiteEntity extends CaveSpider {
        public TermiteEntity(Level level) {
            super(EntityType.CAVE_SPIDER, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Termite(super.getLevel());
    }
}
