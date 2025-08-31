package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Endermite;
import net.minecraft.world.level.Level;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class MirageEndermite extends CataclysmMob {
    public MirageEndermite(Level level) {
        super(new MirageEndermiteEntity(level), "Mirage Endermite", CataclysmColor.MIRAGE, level);
        super.setHealth(10);
        MobUtils.damageBoost(this.getBukkitLivingEntity(), 7.5);
        MobUtils.speedBoost(this.getBukkitLivingEntity(), 1.5);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new MirageEndermite(super.getLevel());
    }

    static class MirageEndermiteEntity extends Endermite {

        public MirageEndermiteEntity(Level level) {
            super(EntityType.ENDERMITE, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

}
