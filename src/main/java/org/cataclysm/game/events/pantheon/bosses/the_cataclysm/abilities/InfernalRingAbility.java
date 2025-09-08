package org.cataclysm.game.events.pantheon.bosses.the_cataclysm.abilities;

import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;

/**
 * Ataque final: dos anillos más grandes y rápidos de explosiones alrededor del jefe.
 */
public class InfernalRingAbility extends PantheonAbility {
    private static final int DAMAGE = 200;
    private static final int PARTICLE_COUNT = 36;
    private static final int SLOW_DURATION = 60;
    private static final int SLOW_AMPLIFIER = 2;
    private static final double[] RADII = {10.0, 20.0}; // radios más grandes y separados
    private static final int REPEAT = 4; // número de repeticiones
    private static final long INTERVAL_TICKS = 1L; // más rápido (antes era 2L)

    private final TheCataclysm cataclysm;

    public InfernalRingAbility(TheCataclysm cataclysm) {
        super(Material.BLAZE_POWDER, "Inferno", 0);
        this.cataclysm = cataclysm;
    }

    @Override
    public void channel() {
        Location center = cataclysm.getController().getLocation();
        for (double radius : RADII) {
            for (int i = 0; i < PARTICLE_COUNT; i++) {
                double angle = 2 * Math.PI * i / PARTICLE_COUNT;
                double x = center.getX() + radius * Math.cos(angle);
                double z = center.getZ() + radius * Math.sin(angle);
                Location loc = new Location(center.getWorld(), x, center.getY(), z);

                scheduleSync(() -> new ParticleHandler(loc).sphere(Particle.CRIT, 1, 2), i);
            }
        }
    }

    @Override
    public void cast() {
        Location center = cataclysm.getController().getLocation();
        World world = center.getWorld();

        for (int rep = 0; rep < REPEAT; rep++) {
            for (double radius : RADII) {
                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    double angle = 2 * Math.PI * i / PARTICLE_COUNT;
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);
                    Location explosionLoc = new Location(world, x, center.getY(), z);

                    long delay = rep * (PARTICLE_COUNT * INTERVAL_TICKS) + i * INTERVAL_TICKS;
                    scheduleSync(() -> createExplosion(explosionLoc), delay);
                }
            }
        }
    }

    private void createExplosion(Location location) {
        World world = location.getWorld();

        // Partículas y sonido
        world.spawnParticle(Particle.EXPLOSION, location, 1);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 4F, 1.0F);

        // Daño y efecto de lentitud a entidades cercanas
        location.getNearbyLivingEntities(3.5, 3.5, 3.5).forEach(entity -> {
            if (entity.equals(cataclysm.getController())) return;

            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, SLOW_DURATION, SLOW_AMPLIFIER));
            cataclysm.damage(entity, DAMAGE);

            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2F, 1.0F);
        });
    }

    private void scheduleSync(Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), task, delayTicks);
    }
}