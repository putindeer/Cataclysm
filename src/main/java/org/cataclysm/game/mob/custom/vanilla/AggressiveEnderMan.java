package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class AggressiveEnderMan extends CataclysmMob {

    public AggressiveEnderMan(Level level) {
        super(new AggressiveEnderManEntity(level), "Aggressive Enderman", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new AggressiveEnderMan(super.getLevel());
    }

    static class AggressiveEnderManEntity extends EnderMan {

        public AggressiveEnderManEntity(Level level) {
            super(EntityType.ENDERMAN, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean canAttackType(@NotNull EntityType<?> type) {
            return type == EntityType.PLAYER;
        }

        @Override
        public boolean isAngryAt(@NotNull net.minecraft.world.entity.LivingEntity livingEntity, @NotNull ServerLevel level) {
            if (livingEntity.isInvulnerable()) return false;
            if (!this.hasLineOfSight(livingEntity)) return false;

            var bukkitEnderman = this.getBukkitEntity();
            var bukkitPlayer = livingEntity.getBukkitLivingEntity();

            if (bukkitPlayer instanceof Player player) {
                if (player.getGameMode() == GameMode.CREATIVE) return false;
                return bukkitEnderman.getNearbyEntities(12.5, 10, 12.5).contains(player);
            }

            return false;
        }
    }

}
