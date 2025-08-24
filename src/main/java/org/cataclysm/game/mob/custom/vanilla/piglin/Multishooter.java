package org.cataclysm.game.mob.custom.vanilla.piglin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.jetbrains.annotations.NotNull;

public class Multishooter extends CataclysmMob {
    public Multishooter(Level level) {
        super(new MultishooterEntity(level), "Piglin Multishooter", "#ad9a68", level);
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.CROSSBOW).addEnchant(Enchantment.MULTISHOT, 1).addEnchant(Enchantment.PIERCING, 3).addEnchant(Enchantment.POWER, 30).buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(org.bukkit.inventory.ItemStack.of(Material.AIR), 1, 1, 1))));

    }

    @Override
    protected CataclysmMob createInstance() {
        return new Multishooter(super.getLevel());
    }

    static class MultishooterEntity extends Piglin {
        public MultishooterEntity(@NotNull Level level) {
            super(EntityType.PIGLIN, level);
            cannotHunt = true;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
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
    }

}
