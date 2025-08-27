package org.cataclysm.game.raids.structures.pantheon.entrance;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.level.Level;
import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.color.CataclysmColor;
import org.cataclysm.api.mob.CataclysmMob;
import org.jetbrains.annotations.NotNull;

public class VoidEssenceMob extends CataclysmMob {
    public VoidEssenceMob(Level level) {
        super(new VoidEssenceEntity(level), "Void Essence", CataclysmColor.PALE, level);
        super.setSpawnTag(SpawnTag.PERSISTENT);
        super.setPersistentDataString("CUSTOM", "void_essence");

        super.getBukkitLivingEntity().setRemoveWhenFarAway(false);
        super.setPregenerationMaterial(Material.BEDROCK);
    }

    public void startTickTask() {
        LivingEntity livingEntity = super.getBukkitLivingEntity();

        World world = livingEntity.getWorld();
        Location location = livingEntity.getLocation().clone();

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
            world.spawnParticle(Particle.SQUID_INK, location, 80, 0.35F, 3F, 0.35F, 0.01F, null, true);
            world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 1.0F);
            world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        }, 0, 5L);

        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
            world.spawnParticle(Particle.SMOKE, location, 60, 0.25F, 3F, 0.25F, 0.01F, null, true);
            world.playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.5F, 0.75F);
        }, 0, 20L);
    }

    static class VoidEssenceEntity extends ArmorStand {
        public VoidEssenceEntity(@NotNull Level level) {
            super(EntityType.ARMOR_STAND, level);
            super.setInvulnerable(true);
            super.setSilent(true);
            super.setInvisible(true);
        }
    }

    @Override
    protected CataclysmMob createInstance() {
        return new VoidEssenceMob(super.getLevel());
    }
}