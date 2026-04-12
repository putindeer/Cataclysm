package org.cataclysm.game.mob.custom.cataclysm.twisted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class TwistedZombie extends CataclysmMob {

    public TwistedZombie(Level level) {
        super(new TwistedZombieEntity(level), "Twisted Zombie", CataclysmColor.TWISTED, level);
        super.setHealth(75);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 36);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.325);
        super.setAttribute(Attributes.SCALE, 1.4);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TWISTED_FLESH.build(), 1, 1, 1))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new TwistedZombie(super.getLevel());
    }

    static class TwistedZombieEntity extends Husk {
        public TwistedZombieEntity(Level level) {
            super(EntityType.HUSK, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (Cataclysm.getDay() >= 21) if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }
}
