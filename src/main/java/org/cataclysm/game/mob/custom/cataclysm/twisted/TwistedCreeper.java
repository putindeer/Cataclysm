package org.cataclysm.game.mob.custom.cataclysm.twisted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bukkit.craftbukkit.entity.CraftCreeper;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class TwistedCreeper extends CataclysmMob {

    public TwistedCreeper(Level level) {
        super(new TwistedCreeperEntity(level), "Twisted Creeper", CataclysmColor.TWISTED, level);
        super.setHealth(5);
        super.setAttribute(Attributes.SCALE, 1.3);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.36);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TWISTED_POWDER.build(), 1, 1, 1))));
    }

    static class TwistedCreeperEntity extends Creeper {
        public TwistedCreeperEntity(Level level) {
            super(EntityType.CREEPER, level);
            var creeper = ((CraftCreeper) this.getBukkitLivingEntity());
            creeper.setExplosionRadius(7);
            creeper.setMaxFuseTicks(20);
            creeper.setFuseTicks(20);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        protected void registerGoals() {
            if (Cataclysm.getDay() >= 21) {
                this.goalSelector.addGoal(1, new FloatGoal(this));
                this.goalSelector.addGoal(2, new SwellGoal(this));
                this.goalSelector.addGoal(4, new MeleeAttackGoal(this, 1.0, false));
                this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
                this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
                this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
                this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true));
                this.targetSelector.addGoal(2, new HurtByTargetGoal(this));
            } else super.registerGoals();
        }

    }

    @Override
    protected CataclysmMob createInstance() {
        return new TwistedCreeper(super.getLevel());
    }
}
