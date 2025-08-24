package org.cataclysm.game.mob.custom.cataclysm.arcane;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.breeze.Breeze;
import net.minecraft.world.level.Level;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class ArcaneBreeze extends CataclysmMob {

    public ArcaneBreeze(Level level) {
        super(new ArcaneBreezeEntity(level), "Arcane Breeze", CataclysmColor.ARCANE, level);
        super.setHealth(30);
        this.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.ARCANE_ROD.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArcaneBreeze(super.getLevel());
    }

    static class ArcaneBreezeEntity extends Breeze {
        public ArcaneBreezeEntity(Level level) {
            super(EntityType.BREEZE, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
}
