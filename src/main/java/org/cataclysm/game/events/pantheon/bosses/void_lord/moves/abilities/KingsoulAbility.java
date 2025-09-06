package org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAbility;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class KingsoulAbility extends HeartAbility {
    private final VoidLord lord;

    private static final double BASE_EXPLOSION_RADIUS = 12.5;
    private static final int BASE_DAMAGE_FACTOR = 10;
    private static final int BASE_RANDOM_LOCATIONS = 25;

    public KingsoulAbility(VoidLord lord) {
        super(Material.WHITE_DYE, "Kingsoul", 2);
        this.lord = lord;
    }

    // ----------- Escalado dinámico -----------

    private int scaleValue(int base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return (int) Math.round(base * factor);
    }

    private double scaleDouble(double base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return base * factor;
    }

    private long scaleDelay(long baseMillis) {
        double factor = isVoidLord() ? 0.5 : 0.8; // más rápido si es VoidLord
        return Math.max(50, (long) (baseMillis * factor));
    }

    // ----------- Canalización -----------

    @Override
    public void channel() {
        Player controller = lord.getController();
        Location loc = controller.getLocation();
        World world = loc.getWorld();

        int duration = (channelTime + 1) * 20;
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 9, false, false));

        playChannelStartSounds(world, loc);

        ScheduledExecutorService service = lord.getThread().getService();
        ParticleHandler handler = new ParticleHandler(loc);

        for (int i = 0; i < channelTime * 10; i++) {
            double pitchAmplifier = i * 0.05;
            long delay = scaleDelay(i * 100L);

            service.schedule(() -> runSync(() -> {
                handler.sphere(Particle.END_ROD, scaleDouble(12.5), 30);
                handler.sphere(Particle.WHITE_SMOKE, scaleDouble(11), 30);

                world.spawnParticle(Particle.END_ROD, loc, scaleValue(7), .25, 2, .25, 0, null, true);
                world.spawnParticle(Particle.WHITE_SMOKE, loc, scaleValue(10), .75, 3, .75, 0, null, true);

                playChannelLoopSounds(world, loc, pitchAmplifier);
            }), delay, TimeUnit.MILLISECONDS);
        }
    }

    // ----------- Ejecución -----------

    @Override
    public void cast() {
        ScheduledExecutorService service = lord.getThread().getService();
        Player controller = lord.getController();

        Location controllerLoc = controller.getLocation();
        World world = controllerLoc.getWorld();

        summonExplosion(controllerLoc, scaleDouble(BASE_EXPLOSION_RADIUS));
        world.playSound(controllerLoc, Sound.ITEM_TRIDENT_THUNDER, 12F, .625F);

        ThreadLocalRandom randomizer = ThreadLocalRandom.current();
        int locationsCount = scaleValue(BASE_RANDOM_LOCATIONS);
        List<Location> locations = lord.getArena().getRandomLocations(locationsCount);

        for (int i = 0; i < locations.size(); i++) {
            Location loc = locations.get(i).clone().add(0, 2, 0);
            if (randomizer.nextBoolean()) loc.add(0, randomizer.nextDouble(0, scaleDouble(30)), 0);

            long delay = scaleDelay(i * 55L);
            service.schedule(() -> runSync(() -> {
                ParticleHandler handler = new ParticleHandler(loc);
                handler.sphere(Particle.END_ROD, scaleDouble(4), scaleValue(10));

                world.playSound(loc, Sound.ITEM_TRIDENT_RETURN, 12F, 1.895F);
                world.playSound(loc, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, .6F, 1.295F);

                service.schedule(() -> runSync(() -> summonExplosion(loc, scaleDouble(5))),
                        scaleDelay(1500), TimeUnit.MILLISECONDS);

            }), delay, TimeUnit.MILLISECONDS);
        }
    }

    // ----------- Explosiones -----------

    private void summonExplosion(Location loc, double radius) {
        World world = loc.getWorld();
        Collection<LivingEntity> entities = loc.getNearbyLivingEntities(radius + 1.5);

        entities.forEach(entity -> {
            if (!entity.equals(lord.getController())) {
                lord.damage(entity, (int) (BASE_DAMAGE_FACTOR * scaleDouble(radius)));
            }
        });

        ParticleHandler handler = new ParticleHandler(loc);
        handler.sphere(Particle.EXPLOSION_EMITTER, radius - 2, radius / 2);

        playExplosionSounds(world, loc, radius);
    }

    // ----------- Sonidos -----------

    private void playChannelStartSounds(World world, Location loc) {
        world.playSound(loc, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 10F, .65F);
        world.playSound(loc, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 10F, .75F);
        world.playSound(loc, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 10F, .85F);
    }

    private void playChannelLoopSounds(World world, Location loc, double pitch) {
        world.playSound(loc, Sound.ITEM_TRIDENT_RETURN, 0.6F, (float) (1.35 + pitch));
        world.playSound(loc, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1F, (float) (1.05 + pitch));
        world.playSound(loc, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1F, (float) (1.45 + pitch));

        if (isVoidLord()) {
            world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 5F, .8F);
        }
    }

    private void playExplosionSounds(World world, Location loc, double radius) {
        world.playSound(loc, Sound.ITEM_TRIDENT_RETURN, (float) (radius * 2), .6F);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, (float) radius, 1.2F);

        if (isVoidLord()) {
            world.playSound(loc, Sound.ENTITY_WITHER_SPAWN, 8F, .7F);
        }
    }

    // ----------- Utilidad -----------

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task);
    }
}