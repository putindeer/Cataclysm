package org.cataclysm.game.mob.custom.cataclysm.arcane;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class ArcaneSculpture extends CataclysmMob {
    public ArcaneSculpture(Level level) {
        super(new ArcaneSculptureEntity(level), "Arcane Sculpture", CataclysmColor.ARCANE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setAttribute(Attributes.SCALE, 1.6);
        super.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.IRON_HELMET).buildAsNMS());
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.LIGHT).buildAsNMS());
        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setPregenerationMaterial(Material.LIGHT_GRAY_GLAZED_TERRACOTTA);
    }

    static class ArcaneSculptureEntity extends Vindicator {
        public ArcaneSculptureEntity(Level level) {
            super(EntityType.VINDICATOR, level);
            super.setNoAi(true);
            super.setSilent(true);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    @Override
    public CataclysmMob createInstance() {
        return new ArcaneSculpture(super.getLevel());
    }
}
