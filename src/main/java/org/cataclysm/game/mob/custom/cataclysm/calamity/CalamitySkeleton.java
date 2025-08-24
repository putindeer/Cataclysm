
package org.cataclysm.game.mob.custom.cataclysm.calamity;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class CalamitySkeleton extends CataclysmMob {


    public CalamitySkeleton(Level level) {
        super(new CalamityWitherSkeletonEntity(level), "Calamity Skeleton", CataclysmColor.CALAMITY, level);
        super.setHealth(60);
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.NETHERITE_SWORD).addEnchant(Enchantment.SHARPNESS, 15).buildAsNMS());
        super.setAttribute(Attributes.ATTACK_DAMAGE, 20);
        super.setAttribute(Attributes.SCALE, 1.15F);
        MobUtils.speedBoost(this.getBukkitLivingEntity(), 0.25);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CalamitySkeleton(super.getLevel());
    }

    static class CalamityWitherSkeletonEntity extends WitherSkeleton {
        public CalamityWitherSkeletonEntity(Level level) {
            super(EntityType.WITHER_SKELETON, level);
        }

        @Override
        public boolean canAttackType(@NotNull EntityType<?> type) {
            return type == EntityType.PLAYER;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
}
