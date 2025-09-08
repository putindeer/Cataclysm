package org.cataclysm.game.events.pantheon.bosses.the_cataclysm.abilities;

import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;

public class AnnihilationBeamAbility extends PantheonAbility {
    private static final int SPHERE_COUNT = 75; // menos esferas, pero más espaciadas
    private static final double SPHERE_INTERVAL_TICKS = 0.5; // ultra rápido
    private static final int SPHERE_RADIUS = 5; // radio ajustado
    private static final double DAMAGE = 400.0;
    private static final double BEAM_LENGTH = 150.0; // rango aumentado
    private final TheCataclysm cataclysm;

    public AnnihilationBeamAbility(TheCataclysm cataclysm) {
        super(Material.WIND_CHARGE, "ANNIHILATION", 0);
        this.cataclysm = cataclysm;
    }

    @Override
    public void channel() {
        var controller = cataclysm.getController();
        Location start = controller.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        for (int i = 0; i < SPHERE_COUNT; i++) {
            int finalI = i;
            scheduleSync(() -> {
                Vector offset = direction.clone().multiply((finalI + 3) * (BEAM_LENGTH / SPHERE_COUNT));
                Location location = start.clone().add(offset);

                // Menos partículas de advertencia
                ParticleHandler handler = new ParticleHandler(location);
                handler.sphere(Particle.CRIT, SPHERE_RADIUS, SPHERE_RADIUS);

                location.getWorld().playSound(location, Sound.BLOCK_NOTE_BLOCK_BASS, 2F, 0.6F);
            }, (long) (SPHERE_INTERVAL_TICKS * finalI));
        }
    }

    @Override
    public void cast() {
        var controller = cataclysm.getController();
        Location start = controller.getEyeLocation();
        Vector direction = start.getDirection().normalize();

        for (int i = 0; i < SPHERE_COUNT; i++) {
            int finalI = i;
            scheduleSync(() -> {
                Vector offset = direction.clone().multiply((finalI + 3) * (BEAM_LENGTH / SPHERE_COUNT));
                Location location = start.clone().add(offset);
                castDamageSphere(location, SPHERE_RADIUS, DAMAGE);
            }, (long) (SPHERE_INTERVAL_TICKS * finalI + 10)); // casi instantáneo tras la canalización
        }
    }

    private void castDamageSphere(Location location, int radius, double damage) {
        var world = location.getWorld();

        // Menos partículas
        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.EXPLOSION, radius, radius * 2);
        handler.sphere(Particle.SMOKE, (double) radius / 2, radius);

        // Sonidos demoledores
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 3F, 1.2F);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 3F, 1.0F);

        // Daño letal
        location.getNearbyLivingEntities(radius, radius, radius).forEach(entity -> {
            if (entity.equals(cataclysm.getController())) return;

            entity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
            entity.getWorld().playSound(entity.getLocation(), Sound.ENTITY_PLAYER_ATTACK_CRIT, 2F, 1.0F);

            cataclysm.damage(entity, damage);
        });
    }

    private void scheduleSync(Runnable task, double delayTicks) {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), task, (long) delayTicks);
    }
}