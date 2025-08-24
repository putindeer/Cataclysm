package org.cataclysm.game.mob.custom.vanilla.slimes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.MagmaCube;
import net.minecraft.world.level.Level;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class GoldenMagmaCube extends CataclysmMob {
    public GoldenMagmaCube(Level level) {
        super(new GoldenMagmaCubeEntity(level), "Golden Magma Cube", "#dc8e32", level);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.GOLDEN_CREAM.build(), 1, 1, 1))));
    }

    static class GoldenMagmaCubeEntity extends MagmaCube {
        public GoldenMagmaCubeEntity(Level level) {
            super(EntityType.MAGMA_CUBE, level);
            super.setSize(1, true);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new GoldenMagmaCube(super.getLevel());
    }
}
