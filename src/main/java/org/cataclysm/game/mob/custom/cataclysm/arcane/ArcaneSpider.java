package org.cataclysm.game.mob.custom.cataclysm.arcane;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class ArcaneSpider extends CataclysmMob {

    public ArcaneSpider(Level level) {
        super(new ArcaneSpiderEntity(level), "Arcane Spider", CataclysmColor.ARCANE, level);
        super.setAttribute(Attributes.SCALE, 0.9);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 30);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.33);
        super.setAttribute(Attributes.MAX_HEALTH, 16.0);
    }

    public static class ArcaneSpiderEntity extends Spider {
        public ArcaneSpiderEntity(Level level) {
            super(EntityType.SPIDER, level);
            this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArcaneSpider(super.getLevel());
    }
}
