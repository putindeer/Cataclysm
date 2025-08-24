package org.cataclysm.game.mob.custom.vanilla.slimes;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class PaleSlime extends CataclysmMob {

    public PaleSlime(Level level) {
        super(new PaleSlimeEntity(level), "Pale Slime", level);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.PALE_BALL.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new PaleSlime(super.getLevel());
    }

    static class PaleSlimeEntity extends Slime {
        public PaleSlimeEntity(Level level) {
            super(EntityType.SLIME, level);
            this.setSize(0, false);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

}