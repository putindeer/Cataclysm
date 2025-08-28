package org.cataclysm.game.mob.custom.cataclysm.twisted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Blaze;
import net.minecraft.world.level.Level;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class TwistedBlaze extends CataclysmMob {
    public TwistedBlaze(Level level) {
        super(new TwistedBlazeEntity(level), "Twisted Blaze", CataclysmColor.TWISTED, level);
        super.setHealth(60);
        super.setAttribute(Attributes.SCALE, 2.25);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TWISTED_ROD.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new TwistedBlaze(super.getLevel());
    }

    static class TwistedBlazeEntity extends Blaze {
        public TwistedBlazeEntity(Level level) {
            super(EntityType.BLAZE, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
}
