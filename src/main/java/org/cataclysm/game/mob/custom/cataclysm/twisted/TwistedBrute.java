package org.cataclysm.game.mob.custom.cataclysm.twisted;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.level.Level;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.item.loot.LootContainer;
import org.cataclysm.api.item.loot.LootHolder;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.mob.drops.CataclysmDrops;
import org.cataclysm.game.items.CataclysmItems;
import org.jetbrains.annotations.NotNull;

public class TwistedBrute extends CataclysmMob {

    public TwistedBrute(Level level) {
        super(new TwistedBruteEntity(level), "Twisted Brute", CataclysmColor.TWISTED, level);
        super.setHealth(90);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 35);
        super.setAttribute(Attributes.MOVEMENT_SPEED, 0.4);
        super.setAttribute(Attributes.SCALE, 1.4);
        super.setItem(EquipmentSlot.MAINHAND, CataclysmItems.TWISTED_AXE.buildAsNMS());
        super.setDrops(new CataclysmDrops(new LootContainer(new LootHolder(CataclysmItems.TWISTED_INGOT.build(), 1, 1, 0.7))));
    }

    @Override
    protected CataclysmMob createInstance() {
        return new TwistedBrute(super.getLevel());
    }

    static class TwistedBruteEntity extends PiglinBrute {
        public TwistedBruteEntity(Level level) {
            super(EntityType.PIGLIN_BRUTE, level);
            ((org.bukkit.entity.PiglinBrute) this.getBukkitLivingEntity()).setImmuneToZombification(true);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            if (damageSource.is(DamageTypeTags.IS_PROJECTILE)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

    }

}
