package org.cataclysm.game.mob.custom.vanilla.skeleton.wither;

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
import org.cataclysm.Cataclysm;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class Swordmaster extends CataclysmMob {

    public Swordmaster(Level level) {
        super(new SwordmasterEntity(level), "Swordmaster", level);
        super.setHealth(60);
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.NETHERITE_SWORD).addEnchant(Enchantment.SHARPNESS, 10).buildAsNMS());
        super.amplifyAttribute(Attributes.ATTACK_DAMAGE, Cataclysm.getDay() < 21 ? 2 : 8);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new Swordmaster(super.getLevel());
    }

    static class SwordmasterEntity extends WitherSkeleton {

        public SwordmasterEntity(Level level) {
            super(EntityType.WITHER_SKELETON, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
}
