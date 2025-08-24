package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.ItemBuilder;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class MirageSculpture extends CataclysmMob {
    public MirageSculpture(Level level) {
        super(new MirageSculptureEntity(level), "Mirage Sculpture", CataclysmColor.MIRAGE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);

        super.setAttribute(Attributes.SCALE, 1.6);
        super.setItem(EquipmentSlot.HEAD, new ItemBuilder(Material.IRON_HELMET).buildAsNMS());
        super.setItem(EquipmentSlot.MAINHAND, new ItemBuilder(Material.LIGHT).buildAsNMS());
        super.setPregenerationMaterial(Material.WHITE_GLAZED_TERRACOTTA);

        super.setDrops(new CataclysmDrops(
                new LootContainer(new LootHolder(CataclysmItems.MIRAGE_QUARTZ.build(), 1, 1, 1))
        ));
    }

    static class MirageSculptureEntity extends Vindicator {
        public MirageSculptureEntity(Level level) {
            super(EntityType.VINDICATOR, level);
            super.setNoAi(true);
            super.setSilent(true);
            this.getBukkitLivingEntity().setRemoveWhenFarAway(false);
            this.getBukkitLivingEntity().setPersistent(true);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    @Override
    public CataclysmMob createInstance() {
        return new MirageSculpture(super.getLevel());
    }
}