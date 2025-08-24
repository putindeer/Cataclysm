package org.cataclysm.game.mob.custom.cataclysm.twisted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class TwistedSkeleton extends CataclysmMob {

    public TwistedSkeleton(Level level) {
        super(new TwistedSkeletonEntity(level), "Twisted Skeleton", CataclysmColor.TWISTED, level);
        super.setHealth(60);
        super.setAttribute(Attributes.SCALE, 1.4);
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.BOW).addEnchant(Enchantment.POWER, 25).buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TWISTED_BONE.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new TwistedSkeleton(super.getLevel());
    }

    public static class TwistedSkeletonEntity extends Skeleton {
        public TwistedSkeletonEntity(Level level) {
            super(EntityType.SKELETON, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }
}
