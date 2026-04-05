package org.cataclysm.game.mob.custom.vanilla.illager;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionType;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class Arbalist extends CataclysmMob {

    public Arbalist(Level level) {
        super(new ArbalistEntity(level), "Arbalist", "#b9b9b9", level);

        super.setItem(EquipmentSlot.MAINHAND,
                new ItemBuilder(Material.CROSSBOW)
                        .addEnchant(Enchantment.MULTISHOT, 3)
                        .addEnchant(Enchantment.POWER, 40)
                        .addEnchant(Enchantment.QUICK_CHARGE, 2)
                        .buildAsNMS());
        super.setItem(EquipmentSlot.OFFHAND, new ItemBuilder(Material.TIPPED_ARROW).setPotion(PotionType.HARMING).buildAsNMS());
    }

    public static class ArbalistEntity extends Pillager {
        public ArbalistEntity(Level level) {
            super(EntityType.PILLAGER, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return null;
    }
}
