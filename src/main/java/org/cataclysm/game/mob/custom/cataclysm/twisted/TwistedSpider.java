package org.cataclysm.game.mob.custom.cataclysm.twisted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.level.Level;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class TwistedSpider extends CataclysmMob {

    public TwistedSpider(Level level) {
        super(new TwistedSpiderEntity(level), "Twisted Spider", CataclysmColor.TWISTED, level);
        super.setHealth(75);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 24);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.4);
        super.setAttribute(Attributes.SCALE, 1.3);
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TWISTED_STRING.build(), 1, 1, 1))));
    }

    static class TwistedSpiderEntity extends Spider {
        public TwistedSpiderEntity(Level level) {
            super(EntityType.SPIDER, level);
            this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, net.minecraft.world.entity.player.Player.class, true));
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (Cataclysm.getDay() >= 21) if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

    @Override
    protected CataclysmMob createInstance() {
        return new TwistedSpider(super.getLevel());
    }
}
