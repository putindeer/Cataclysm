package org.cataclysm.game.mob.custom.vanilla.piglin;

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
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class Knight extends CataclysmMob {

    public Knight(Level level) {
        super(new KnightEntity(level), "Piglin Knight", "#454545", level);
        super.setHealth(60);
        super.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.NETHERITE_HELMET).buildAsNMS());
        super.setItem(EquipmentSlot.CHEST, new ItemBuilder(Material.NETHERITE_CHESTPLATE).buildAsNMS());
        super.setItem(EquipmentSlot.LEGS, new ItemBuilder(Material.NETHERITE_LEGGINGS).buildAsNMS());
        super.setItem(EquipmentSlot.FEET, new ItemBuilder(Material.NETHERITE_BOOTS).buildAsNMS());
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.NETHERITE_SWORD).addEnchant(Enchantment.SHARPNESS, 5).buildAsNMS());
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, Cataclysm.getDay() < 21 ? 3 : 6);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Knight(super.getLevel());
    }

    static class KnightEntity extends Piglin {
        public KnightEntity(@NotNull Level level) {
            super(EntityType.PIGLIN, level);
            cannotHunt = true;
        }

        @Override
        public boolean wantsToPickUp(@NotNull ServerLevel level, @NotNull ItemStack stack) {
            return false;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void readAdditionalSaveData(@NotNull CompoundTag compound) {
            super.readAdditionalSaveData(compound);
            this.allowedBarterItems = new java.util.HashSet<>();
            this.interestItems = new java.util.HashSet<>();
        }
    }

}
