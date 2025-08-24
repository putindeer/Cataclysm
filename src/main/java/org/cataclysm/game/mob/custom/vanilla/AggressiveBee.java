package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Bee;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.attribute.Attribute;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

import java.util.SplittableRandom;

public class AggressiveBee extends CataclysmMob {

    public AggressiveBee(Level level) {
        super(new WaspEntity(level), "Wasp", level);
    }

    static class WaspEntity extends Bee {

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        public WaspEntity(Level level) {
            super(EntityType.BEE, level);
            this.setRemainingPersistentAngerTime(Integer.MAX_VALUE);
            SplittableRandom random = new SplittableRandom();
            AttributeInstance scale = this.getAttribute(Attributes.SCALE);

            if (scale != null) {
                scale.setBaseValue(random.nextDouble(3.75) + 0.25);

                MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.ATTACK_DAMAGE, scale.getBaseValue() * 8);
                MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.MAX_HEALTH, scale.getBaseValue());
            }

            this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }

        @Override
        public void setHasStung(boolean hasStung) {
            super.setHasStung(false);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new AggressiveBee(super.getLevel());
    }
}
