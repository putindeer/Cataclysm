package org.cataclysm.game.mob.custom.cataclysm.calamity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class CalamityEnderman extends CataclysmMob {

    public CalamityEnderman(Level level) {
        super(new CalamityEndermanEntity(level), "Calamity Enderman", CataclysmColor.CALAMITY, level);
        super.setHealth(60);
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, 4.25);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CalamityEnderman(super.getLevel());
    }

    static class CalamityEndermanEntity extends EnderMan {
        public CalamityEndermanEntity(Level level) {
            super(EntityType.ENDERMAN, level);
            this.setCarriedBlock(Block.byItem(Items.TNT).defaultBlockState());
        }

        @Override
        public boolean isAngryAt(@NotNull net.minecraft.world.entity.LivingEntity livingEntity, @NotNull ServerLevel level) {
            if (livingEntity.isInvulnerable()) return false;
            if (!this.hasLineOfSight(livingEntity)) return false;

            var bukkitEnderman = this.getBukkitEntity();
            var bukkitPlayer = livingEntity.getBukkitLivingEntity();

            if (bukkitPlayer instanceof Player player) {
                if (player.getGameMode() == GameMode.CREATIVE) return false;
                return bukkitEnderman.getNearbyEntities(12.5 ,10, 12.5).contains(player);
            }

            return false;
        }

        @Override
        public void registerGoals() {
            super.registerGoals();
            for (var availableGoal : this.goalSelector.getAvailableGoals()) {
                if (availableGoal == null) continue;
                var className = availableGoal.getGoal().getClass().getName();
                if (className.contains("BlockGoal")) this.goalSelector.removeGoal(availableGoal.getGoal());
            }
        }

        @Override
        public boolean canAttackType(@NotNull EntityType<?> type) {
            return type == EntityType.PLAYER;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
}
