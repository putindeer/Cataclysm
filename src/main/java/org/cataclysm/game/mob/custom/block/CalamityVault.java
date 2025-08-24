package org.cataclysm.game.mob.custom.block;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.Material;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class CalamityVault extends CataclysmMob {
    public CalamityVault(Level level) {
        super(new CalamityVaultEntity(level), "Calamity Vault", CataclysmColor.CALAMITY, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setPersistentDataString("CUSTOM", "calamity_vault");
        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setPregenerationMaterial(Material.VAULT);
    }

    static class CalamityVaultEntity extends ArmorStand {
        public CalamityVaultEntity(@NotNull Level level) {
            super(EntityType.ARMOR_STAND, level);
            super.setInvulnerable(true);
            super.setSilent(true);
        }

        @Override
        public void knockback(double strength, double x, double z) {
            super.knockback(0, x, z);
        }

        @Override
        protected float getKnockback(@NotNull Entity attacker, @NotNull DamageSource damageSource) {
            return 0;
        }

        @Override
        public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource damageSource, float amount) {
            return damageSource.isCreativePlayer();
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new CalamityVault(super.getLevel());
    }
}
