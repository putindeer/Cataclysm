package org.cataclysm.game.mob.custom.cataclysm.mirage;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.level.Level;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.entity.CraftShulker;
import org.cataclysm.api.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class MirageShulker extends CataclysmMob {
    public MirageShulker(Level level) {
        super(new MirageShulkerEntity(level), "Mirage Shulker", CataclysmColor.MIRAGE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setPregenerationMaterial(Material.LIGHT_BLUE_GLAZED_TERRACOTTA);
    }

    @Override
    public CataclysmMob createInstance() {
        return new MirageShulker(super.getLevel());
    }

    public static class MirageShulkerEntity extends Shulker {
        public MirageShulkerEntity(Level level) {
            super(EntityType.SHULKER, level);
            this.getBukkitLivingEntity().setRemoveWhenFarAway(false);
            this.getBukkitLivingEntity().setPersistent(true);
            CraftShulker shulker = (CraftShulker) this.getBukkitLivingEntity();
            var randomColor = DyeColor.values()[this.random.nextInt(DyeColor.values().length)];
            shulker.setColor(randomColor);
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) return false;
            return super.hurtServer(level, damageSource, amount);
        }
    }

}
