package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class MirageSkeleton extends CataclysmMob {
    public MirageSkeleton(Level level) {
        super(new MirageSkeletonEntity(level), "Mirage Skeleton", CataclysmColor.MIRAGE, level);
        super.setHealth(100);
        super.setAttribute(Attributes.SCALE, 1.5);
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).addEnchant(Enchantment.POWER, 50).buildAsNMS());
        super.setItem(EquipmentSlot.OFFHAND, Items.AIR);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.MIRAGE_BONE.build(), 1, 1, .5))));

    }

    @Override
    protected CataclysmMob createInstance() {
        return new MirageSkeleton(super.getLevel());
    }

    static class MirageSkeletonEntity extends Skeleton {

        public MirageSkeletonEntity(Level level) {
            super(EntityType.SKELETON, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void tick() {
            super.tick();
            if (!this.level().isClientSide && this.isAlive() && !this.isNoAi()) {
                LivingEntity livingEntity = this.getBukkitLivingEntity();
                boolean hasNearbyPlayers = livingEntity.getLocation().getNearbyPlayers(3.5).stream().anyMatch(player -> player.getGameMode() == GameMode.SURVIVAL);

                if (hasNearbyPlayers && this.getMainHandItem().is(Items.BOW)) {
                    ItemStack swordStack = new ItemBuilder(Material.NETHERITE_SWORD).addEnchant(Enchantment.SHARPNESS, 40).buildAsNMS();
                    this.setItemInHand(InteractionHand.MAIN_HAND, swordStack);
                }

                if (!hasNearbyPlayers && this.getMainHandItem().is(Items.NETHERITE_SWORD)) {
                    ItemStack bowStack = new ItemBuilder(Material.BOW).addEnchant(Enchantment.POWER, 50).buildAsNMS();
                    this.setItemInHand(InteractionHand.MAIN_HAND, bowStack);
                }
            }
        }
    }

}
