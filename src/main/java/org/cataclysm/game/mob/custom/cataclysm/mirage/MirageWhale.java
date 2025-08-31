package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.level.Level;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class  MirageWhale extends CataclysmMob {
    public MirageWhale(Level level) {
        super(new MirageWhaleEntity(level), "Mirage Whale", CataclysmColor.MIRAGE, level);
        this.setHealth(65);
        super.setDrops(new CataclysmDrops(
                new LootContainer(
                        new LootHolder(CataclysmItems.MIRAGE_TEAR.build(), 1, 1, .5),
                        new LootHolder(CataclysmItems.WHALE_WING.build(), 1, 1, .25)
                )
        ));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new MirageWhale(super.getLevel());
    }

    static class MirageWhaleEntity extends Ghast {
        public MirageWhaleEntity(Level level) {
            super(EntityType.GHAST, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public int getExplosionPower() {
            return 7;
        }
    }

}

