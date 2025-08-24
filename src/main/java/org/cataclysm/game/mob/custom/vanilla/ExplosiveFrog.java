package org.cataclysm.game.mob.custom.vanilla;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.level.Level;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.MobUtils;
import org.jetbrains.annotations.NotNull;

public class ExplosiveFrog extends CataclysmMob {

    public ExplosiveFrog(Level level) {
        super(new ExplosiveFrogEntity(level), "Explosive Frog", level);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ExplosiveFrog(super.getLevel());
    }

    public static class ExplosiveFrogEntity extends Frog {

        public ExplosiveFrogEntity(Level level) {
            super(EntityType.FROG, level);
            MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.SCALE, 4);
            MobUtils.multiplyAttribute(this.getBukkitLivingEntity(), Attribute.MAX_HEALTH, 3);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }

        @Override
        public void setPose(@NotNull Pose pose) {
            super.setPose(pose);

            if (pose == Pose.CROAKING && !this.getBukkitLivingEntity().getLocation().getNearbyPlayers(5).isEmpty()) {
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> this.getBukkitLivingEntity().getWorld().createExplosion(this.getBukkitLivingEntity(), 5, false, false), 5L);
            }
        }
    }

}
