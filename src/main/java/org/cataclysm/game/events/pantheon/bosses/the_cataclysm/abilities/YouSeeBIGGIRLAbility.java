package org.cataclysm.game.events.pantheon.bosses.the_cataclysm.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_cataclysm.TheCataclysm;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class YouSeeBIGGIRLAbility extends PantheonAbility {

    private static final float EXPLOSION_RADIUS = 50.0F;
    private static final int CHANNEL_SOUND_REPETITIONS = 10;
    private static final int CHANNEL_TICK_INTERVAL = 20; // 1 segundo por tick
    private static final int CAST_ITERATIONS = 5;

    private final TheCataclysm cataclysm;

    public YouSeeBIGGIRLAbility(TheCataclysm cataclysm) {
        super(Material.TNT_MINECART, "YouSeeBIGGIRL", 4);
        this.cataclysm = cataclysm;
    }

    @Override
    public void channel() {
        var controller = cataclysm.getController();
        int durationTicks = channelTime * 20;

        // aplicar efectos al controlador
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, durationTicks, 4, true, true, true));
        controller.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durationTicks, 0, true, true, true));

        // canalización periódica
        for (int i = 0; i < channelTime; i++) {
            scheduleSync(() -> {
                Location loc = controller.getLocation();
                // reproducir sonidos aleatorios
                Bukkit.getOnlinePlayers().forEach(player -> {
                    for (int j = 0; j < CHANNEL_SOUND_REPETITIONS; j++) {
                        scheduleSync(() -> {
                            float volume = ThreadLocalRandom.current().nextFloat() * 2 + 2; // 2-4
                            float pitch = ThreadLocalRandom.current().nextFloat() + 0.5F; // 0.5-1.5
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, volume, pitch);
                        }, j * 2L);
                    }
                });

                // partículas y rayos
                new ParticleHandler(loc).sphere(Particle.END_ROD, EXPLOSION_RADIUS, 30);
                loc.getWorld().strikeLightningEffect(loc);

                cataclysm.getNearbyLivingEntities(loc, EXPLOSION_RADIUS).forEach(entity ->
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 0, true, true))
                );
            }, (long) i * CHANNEL_TICK_INTERVAL);
        }
    }

    @Override
    public void cast() {
        Location center = cataclysm.getController().getLocation();
        World world = center.getWorld();

        // sonido inicial
        world.playSound(center, "cataclysm.pantheon.death", 20F, 0.95F);

        for (int i = 0; i < CAST_ITERATIONS; i++) {
            scheduleSync(() -> {
                // rayos en ubicaciones aleatorias
                List<Location> randomLocs = cataclysm.getArena().getRandomLocations((int) EXPLOSION_RADIUS);
                randomLocs.forEach(world::strikeLightning);

                // partículas y explosión
                new ParticleHandler(center).sphere(Particle.EXPLOSION_EMITTER, EXPLOSION_RADIUS, EXPLOSION_RADIUS / 2);
                world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 20F, 0.95F);

                // daño a entidades cercanas
                cataclysm.getNearbyLivingEntities(center, EXPLOSION_RADIUS).forEach(entity ->
                        cataclysm.damage(entity, 150.0)
                );
            }, 12 * i);
        }
    }

    /**
     * Ejecuta un Runnable en el hilo principal usando BukkitScheduler
     */
    private void scheduleSync(Runnable task, long delayTicks) {
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), task, delayTicks);
    }
}