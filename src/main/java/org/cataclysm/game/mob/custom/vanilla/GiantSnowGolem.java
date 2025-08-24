package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.attribute.Attribute;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class GiantSnowGolem extends CataclysmMob {

    public GiantSnowGolem(Level level) {
        super(new GiantSnowGolemEntity(level), "Giant Snow Golem", level);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.GOLEM_HEAD.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new GiantSnowGolem(super.getLevel());
    }

    public static class GiantSnowGolemEntity extends SnowGolem {
        public GiantSnowGolemEntity(Level level) {
            super(EntityType.SNOW_GOLEM, level);
            MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.SCALE, 5);
            MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.MAX_HEALTH, 15);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean isSensitiveToWater() {
            return false;
        }

        @Override
        protected void registerGoals() {
            this.goalSelector.addGoal(0, new RangedAttackGoal(this, 1.25, 20 * 5, 40.0F));
            super.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
        }

    }
}
