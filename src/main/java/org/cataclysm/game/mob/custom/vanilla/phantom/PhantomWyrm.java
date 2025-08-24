package org.cataclysm.game.mob.custom.vanilla.phantom;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.level.Level;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class PhantomWyrm extends CataclysmMob {
    public PhantomWyrm(Level level) {
        super(new PhantomWyrmEntity(level), "Phantom Wyrm", "#c4c4c4", level);
        super.setHealth(60);
        super.setAttribute(Attributes.SCALE, 4);
        super.setAttribute(Attributes.ATTACK_DAMAGE, 25);
    }

    public static class PhantomWyrmEntity extends Phantom {
        public PhantomWyrmEntity(Level level) {
            super(EntityType.PHANTOM, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public boolean doHurtTarget(@NotNull ServerLevel level, @NotNull Entity source) {
            var livingEntity = this.getTarget();
            if (livingEntity == null) return false;

            var cle = livingEntity.getBukkitLivingEntity();
            cle.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 60, 2));
            return super.doHurtTarget(level, source);
        }

    }

    @Override
    protected CataclysmMob createInstance() {
        return new PhantomWyrm(super.getLevel());
    }
}
