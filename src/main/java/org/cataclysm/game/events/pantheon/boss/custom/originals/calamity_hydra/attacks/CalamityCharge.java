package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.attacks;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CalamityCharge {
    private final PantheonHydra hydra;
    private ChargeEntity chargeEntity;
    private final float radius;
    private final int amplifier;
    private final double scale;

    public CalamityCharge(PantheonHydra hydra, float radius, int amplifier, double scale) {
        this.hydra = hydra;
        this.radius = radius;
        this.amplifier = amplifier;
        this.scale = scale;
    }

    public void drop(Location location, double power) {
        Player controller = this.hydra.getController();
        World world = controller.getWorld();

        for (double d = 3; d < 100.0; d += .25) {
            Location point = location.clone().add(0, -d, 0);
            Material type = point.getBlock().getType();

            if (this.chargeEntity == null) {
                this.chargeEntity = new ChargeEntity(point.clone().add(0.5, 0, 0.5), this.scale);
                ((CraftWorld) world).getHandle().addFreshEntity(this.chargeEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            }

            LivingEntity livingEntity = this.chargeEntity.getBukkitLivingEntity();
            ScheduledExecutorService service = this.hydra.getThread().getService();
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    if (type.isSolid()) {
                        this.hydra.createHydraExplosion(point, power, CalamityExplosion.Type.MACRO);
                        this.hydra.summonAreaEffectCloud(point, this.radius, this.amplifier);
                        livingEntity.remove();
                    }
                    livingEntity.teleport(point);
                });
            }, (long) ((d - 1) * 40), TimeUnit.MILLISECONDS);

            if (type.isSolid()) break;
        }
    }

    public void shoot(double power) {
        Player controller = this.hydra.getController();

        Location loc = controller.getEyeLocation();
        Vector dir = loc.getDirection().normalize();

        World world = controller.getWorld();
        for (double d = 3; d < 400.0; d += .25) {
            Location point = loc.clone().add(dir.clone().multiply(d));
            Material type = point.getBlock().getType();

            if (this.chargeEntity == null) {
                this.chargeEntity = new ChargeEntity(point.clone().add(0.5, 0, 0.5), this.scale);
                ((CraftWorld) world).getHandle().addFreshEntity(this.chargeEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);
            }

            LivingEntity livingEntity = this.chargeEntity.getBukkitLivingEntity();
            ScheduledExecutorService service = this.hydra.getThread().getService();
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    if (type.isSolid()) {
                        this.hydra.createHydraExplosion(point, power, CalamityExplosion.Type.MACRO);
                        this.hydra.summonAreaEffectCloud(point, this.radius, this.amplifier);
                        livingEntity.remove();
                    }
                    world.spawnParticle(Particle.FLAME, point, 2, 0.05, 0.05, 0.05, 0);
                    livingEntity.teleport(point);
                });
            }, (long) ((d - 1) * 40), TimeUnit.MILLISECONDS);

            if (type.isSolid()) break;
        }
    }

    static class ChargeEntity extends ArmorStand {
        public ChargeEntity(Location location, double scale) {
            super(EntityType.ARMOR_STAND, ((CraftWorld) location.getWorld()).getHandle());
            super.setCustomName(Component.literal("Calamity Charge"));
            super.setInvulnerable(true);
            this.setScale(scale);
            super.getBukkitLivingEntity().teleport(location);
        }

        private void setScale(double scale) {
            AttributeInstance instance = this.getBukkitLivingEntity().getAttribute(Attribute.SCALE);
            if (instance != null) instance.setBaseValue(scale);
        }
    }
}
