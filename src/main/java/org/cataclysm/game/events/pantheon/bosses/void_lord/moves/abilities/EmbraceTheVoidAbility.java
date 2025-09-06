package org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAbility;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class EmbraceTheVoidAbility extends HeartAbility {
    // ---------------- Constantes base ----------------
    private static final int BASE_DELAY = 4;
    private static final int BASE_DAMAGE = 120;
    private static final int BASE_EXPLOSION_MULTIPLIER = 5;

    private final VoidLord lord;

    public EmbraceTheVoidAbility(VoidLord lord) {
        super(Material.NETHER_STAR, "Embrace The Void", 5);
        this.lord = lord;
    }

    // ---------------- Escalado dinámico ----------------

    private int scaleValue(int base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return (int) Math.round(base * factor);
    }

    private double scaleValue(double base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return base * factor;
    }

    private int scaleDelay(int base) {
        double factor = isVoidLord() ? 0.4 : 0.8; // menos delay = más rápido
        return (int) Math.max(base * factor, 1);
    }

    // ---------------- Habilidades ----------------

    @Override
    public void channel() {
        Player controller = lord.getController();
        controller.setGameMode(GameMode.SPECTATOR);

        ScheduledExecutorService service = lord.getThread().getService();
        CataclysmArea arena = lord.getArena();
        Location center = arena.center().clone().add(0, 15, 0);
        ParticleHandler handler = new ParticleHandler(center);
        World world = center.getWorld();

        // Sonidos iniciales
        arena.getLivingEntitiesInArena().forEach(entity -> playStartSounds(world, entity));

        // Canalización con efectos y ceguera
        for (int i = 0; i < channelTime; i++) {
            service.schedule(() -> runSync(() -> {
                playChannelSounds(world, center);
                handler.sphere(Particle.END_ROD, 10, 10);
                handler.sphere(Particle.WHITE_SMOKE, 10, 10);
                summonVoidSphere(center, scaleValue(20));
                arena.getLivingEntitiesInArena().forEach(entity -> {
                    if (!entity.hasPotionEffect(DisperEffect.EFFECT_TYPE)) {
                        entity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 0));
                        world.playSound(entity, Sound.ENTITY_GUARDIAN_DEATH, 1F, 0.5F);
                    }
                });
            }), i, TimeUnit.SECONDS);
        }

        // Esferas adicionales
        List<Location> randomLocations = arena.getRandomLocations(scaleValue(7));
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            for (int i = 0; i < randomLocations.size(); i++) {
                Location loc = randomLocations.get(i);
                service.schedule(() -> runSync(() ->
                                spawnSphere(loc, scaleValue(4.0), channelTime + scaleValue(BASE_DELAY))),
                        250L * i, TimeUnit.MILLISECONDS);
            }
        }, 30);

        // Explosiones en oleada
        service.schedule(() -> {
            for (int j = 0; j < (isVoidLord() ? 5 : 3); j++) {
                service.schedule(() -> runSync(() -> {
                    handler.sphere(Particle.EXPLOSION_EMITTER, arena.radius(), arena.radius() / 4.0);
                    playExplosionWaveSounds(world, center);
                    arena.getLivingEntitiesInArena().forEach(entity -> {
                        if (!entity.hasPotionEffect(DisperEffect.EFFECT_TYPE) && !entity.equals(lord.getController())) {
                            lord.damage(entity, scaleValue(BASE_DAMAGE));
                        }
                    });
                }), j * 600, TimeUnit.MILLISECONDS);
            }
        }, channelTime, TimeUnit.SECONDS);

        // Fase final: retorno del controlador y explosión
        service.schedule(() -> runSync(() -> {
            Location random = arena.getRandomLocations(1).getFirst();
            controller.teleport(random);
            controller.setGameMode(GameMode.ADVENTURE);
            lord.createExplosion(controller.getLocation(), scaleValue(6));
        }), channelTime + BASE_DELAY + 6, TimeUnit.SECONDS);
    }

    @Override
    public void cast() {
        // Esta habilidad es puramente canalizada
    }

    // ---------------- Auxiliares internos ----------------

    private void spawnSphere(Location location, double radius, int explosionDelay) {
        Location particleCenter = location.clone().add(0, 1, 0);
        ParticleHandler handler = new ParticleHandler(particleCenter);
        ScheduledExecutorService service = lord.getThread().getService();
        World world = location.getWorld();

        for (int i = 0; i < explosionDelay * 2; i++) {
            service.schedule(() -> runSync(() -> {
                location.getNearbyLivingEntities(radius).forEach(entity -> {
                    entity.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 80, 0, false, false));
                    entity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 0, false, false));
                });
                handler.sphere(Particle.WHITE_SMOKE, radius, radius * 3);
                handler.sphere(Particle.END_ROD, radius, radius * 3);
                world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 4F, 0.55F);
            }), i * 500L, TimeUnit.MILLISECONDS);
        }

        // Esfera de vacío en bucle
        service.schedule(() -> {
            for (int i = 0; i < BASE_DELAY * 2; i++) {
                service.schedule(() -> runSync(() ->
                                summonVoidSphere(particleCenter, radius)),
                        i * 500, TimeUnit.MILLISECONDS);
            }
        }, channelTime + 1, TimeUnit.SECONDS);

        // Explosión final
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(),
                () -> summonExplosion(location, radius * BASE_EXPLOSION_MULTIPLIER),
                explosionDelay * 20L);
    }

    private void summonVoidSphere(Location location, double radius) {
        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.SMOKE, radius / 2, radius * 3);
        handler.sphere(Particle.SQUID_INK, radius / 3, radius * 3);
        handler.sphere(Particle.SMOKE, radius, radius);
        location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 5, .5F);
        location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 5, .5F);

        location.getNearbyLivingEntities(radius).forEach(entity ->
                entity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 4)));
    }

    private void summonExplosion(Location location, double radius) {
        ParticleHandler handler = new ParticleHandler(location);
        ScheduledExecutorService service = lord.getThread().getService();
        World world = location.getWorld();

        for (int i = 0; i < (isVoidLord() ? 5 : 3); i++) {
            service.schedule(() -> runSync(() -> {
                location.getNearbyLivingEntities(radius)
                        .forEach(entity -> lord.damage(entity, scaleValue(BASE_DAMAGE)));

                playExplosionFinalSounds(world, location);
                handler.sphere(Particle.END_ROD, radius, radius);
                handler.sphere(Particle.EXPLOSION_EMITTER, radius, radius / 3);
            }), i * 750, TimeUnit.MILLISECONDS);
        }
    }

    // ---------------- Sonidos ----------------

    private void playStartSounds(World world, org.bukkit.entity.Entity entity) {
        world.playSound(entity, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3F, 0.55F);
        world.playSound(entity, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3F, 0.65F);
        world.playSound(entity, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3F, 0.75F);
    }

    private void playChannelSounds(World world, Location center) {
        world.playSound(center, Sound.ITEM_TRIDENT_RETURN, 8F, 1.15F);
        world.playSound(center, Sound.ITEM_TRIDENT_RETURN, 8F, 0.55F);
    }

    private void playExplosionWaveSounds(World world, Location center) {
        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 8F, 1.0F);
        world.playSound(center, Sound.ITEM_TRIDENT_THUNDER, 8F, 0.5F);
        world.playSound(center, Sound.ITEM_TRIDENT_THUNDER, 8F, 1.55F);
        if (isVoidLord()) {
            world.playSound(center, Sound.ENTITY_WITHER_SPAWN, 10F, 0.6F);
        }
    }

    private void playExplosionFinalSounds(World world, Location loc) {
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 8F, 1.15F);
        world.playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 8F, 0.55F);
        world.playSound(loc, Sound.ITEM_TRIDENT_THUNDER, 8F, 1.95F);
        if (isVoidLord()) {
            world.playSound(loc, Sound.ENTITY_ENDER_DRAGON_GROWL, 10F, 0.7F);
        }
    }

    // ---------------- Utilidad ----------------

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task);
    }
}