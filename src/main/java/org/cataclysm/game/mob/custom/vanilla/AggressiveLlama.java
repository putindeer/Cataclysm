package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.horse.Llama;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AggressiveLlama extends CataclysmMob {

    public AggressiveLlama(Level level) {
        super(new AggressiveLlamaEntity(level), "Llama", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new AggressiveLlama(super.getLevel());
    }

    public static class AggressiveLlamaEntity extends Llama {

        public AggressiveLlamaEntity(Level level) {
            super(EntityType.LLAMA, level);
            MobUtils.speedBoost(this.getBukkitLivingEntity(), 3);
            org.bukkit.entity.Llama llama = (org.bukkit.entity.Llama) getBukkitLivingEntity();
            llama.getInventory().setDecor(new ItemStack(Material.BLACK_CARPET));
        }

        @Override
        protected void registerGoals() {
            this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));
            super.registerGoals();
        }

        @Override
        public boolean isAggressive() {
            return true;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        @Nullable
        public Llama getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
            Llama llama = this.makeNewLlama();
            if (llama != null) {
                this.setOffspringAttributes(otherParent, llama);
                Llama newLlama = (Llama) otherParent;
                llama.setVariant(super.random.nextBoolean() ? this.getVariant() : newLlama.getVariant());
            }

            return llama;
        }

    }
}
