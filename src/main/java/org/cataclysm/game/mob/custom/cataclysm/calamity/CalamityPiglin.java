package org.cataclysm.game.mob.custom.cataclysm.calamity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

import java.util.SplittableRandom;

public class CalamityPiglin extends CataclysmMob {

    public CalamityPiglin(Level level) {
        super(new CalamityPiglinEntity(level), "Calamity Piglin", CataclysmColor.CALAMITY, level);
        super.setHealth(40);
        super.setAttribute(Attributes.SCALE, 1.25F);
        MobUtils.speedBoost(this.getBukkitLivingEntity(), 0.75);

        var item = new SplittableRandom().nextBoolean() ?
                new ItemBuilder(Material.GOLDEN_SWORD)
                        .addEnchant(Enchantment.SHARPNESS, 20)
                        .addEnchant(Enchantment.FIRE_ASPECT, 50)
                        .buildAsNMS() :
                new ItemBuilder(Material.CROSSBOW)
                        .addEnchant(Enchantment.QUICK_CHARGE, 3)
                        .addEnchant(Enchantment.PIERCING, 10)
                        .addEnchant(Enchantment.POWER, 40)
                        .buildAsNMS();
        super.setItem(EquipmentSlot.MAINHAND, item);

    }

    @Override
    protected CataclysmMob createInstance() {
        return new CalamityPiglin(super.getLevel());
    }

    static class CalamityPiglinEntity extends Piglin {
        public CalamityPiglinEntity(Level level) {
            super(EntityType.PIGLIN, level);
            cannotHunt = true;
        }

        @Override
        public boolean wantsToPickUp(@NotNull ServerLevel level, @NotNull ItemStack stack) {
            return false;
        }

        @Override
        public void readAdditionalSaveData(@NotNull CompoundTag compound) {
            super.readAdditionalSaveData(compound);
            this.allowedBarterItems = new java.util.HashSet<>();
            this.interestItems = new java.util.HashSet<>();
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }
}
