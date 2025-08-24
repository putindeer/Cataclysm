package org.cataclysm.game.mob.custom.cataclysm.twisted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import org.bukkit.GameMode;
import org.bukkit.SoundCategory;
import org.bukkit.entity.Player;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class TwistedEnderman extends CataclysmMob {

    public TwistedEnderman(Level level) {
        super(new TwistedEndermanEntity(level), "Twisted Enderman", CataclysmColor.TWISTED, level);
        super.setHealth(60);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 30);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.4);
        super.setAttribute(Attributes.SCALE, 1.8);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TWISTED_PEARL.build(), 1, 1, 1))));
    }

    public static class TwistedEndermanEntity extends EnderMan {
        public TwistedEndermanEntity(Level level) {
            super(EntityType.ENDERMAN, level);
            this.setRemainingPersistentAngerTime(Integer.MAX_VALUE);
        }

        @Override
        public boolean isSensitiveToWater() {
            return false;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean isAngryAt(@NotNull net.minecraft.world.entity.LivingEntity livingEntity, @NotNull ServerLevel level) {
            if (livingEntity.isInvulnerable()) return false;
            if (!this.hasLineOfSight(livingEntity)) return false;

            var bukkitEnderman = this.getBukkitEntity();
            var bukkitPlayer = livingEntity.getBukkitLivingEntity();

            if (bukkitPlayer instanceof Player player) {
                if (player.getGameMode() == GameMode.CREATIVE) return false;
                if (bukkitEnderman.getNearbyEntities(12.5 ,10, 12.5).contains(player)) {
                    var world = player.getWorld();
                    world.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_ENDERMAN_SCREAM, SoundCategory.HOSTILE, 1, 0.45F);
                    return true;
                }
            }

            return false;
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return null;
    }
}
