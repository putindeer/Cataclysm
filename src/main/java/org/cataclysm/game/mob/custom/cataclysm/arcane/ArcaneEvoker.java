package org.cataclysm.game.mob.custom.cataclysm.arcane;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Evoker;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class ArcaneEvoker extends CataclysmMob {

    public ArcaneEvoker(Level level) {
        super(new ArcaneEvokerEntity(level), "Arcane Evoker", CataclysmColor.ARCANE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setHealth(48);
        super.getBukkitLivingEntity().setPersistent(true);
        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setPregenerationMaterial(Material.RED_GLAZED_TERRACOTTA);
    }

    @Override
    protected CataclysmMob createInstance() {
        return new ArcaneEvoker(super.getLevel());
    }

    static class ArcaneEvokerEntity extends Evoker {
        public ArcaneEvokerEntity(Level level) {
            super(EntityType.EVOKER, level);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

}
