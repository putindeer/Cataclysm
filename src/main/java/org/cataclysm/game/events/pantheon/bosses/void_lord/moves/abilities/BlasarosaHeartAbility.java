package org.cataclysm.game.events.pantheon.bosses.void_lord.moves.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.bosses.void_lord.VoidLord;
import org.cataclysm.game.events.pantheon.bosses.void_lord.moves.HeartAbility;
import org.cataclysm.game.player.PlayerUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BlasarosaHeartAbility extends HeartAbility {
    private static final int BASE_CHANNEL_DELAY_MS = 12_000;
    private static final int BASE_CAST_DURATION_MS = 7_000;
    private static final int BASE_BEAM_REPETITIONS = 5;
    private static final int BASE_BEAM_HEIGHT_STEPS = 50;
    private static final int BASE_BEAM_INTERVAL_MS = 500;
    private static final int BASE_LINE_INTERVAL_MS = 25;
    private static final int BASE_EXPLOSION_DAMAGE = 180;

    private final VoidLord lord;

    public BlasarosaHeartAbility(VoidLord lord) {
        super(Material.OPEN_EYEBLOSSOM, "Blasarosa", 0);
        this.lord = lord;
    }

    // ---------------- Scaling dinámico ----------------

    private int scaleValue(int base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return (int) Math.round(base * factor);
    }

    private double scaleValue(double base) {
        double factor = isVoidLord() ? 2.5 : 1.25;
        return base * factor;
    }

    private int scaleDelay(int base) {
        // igual aplica la lógica de delay, pero invertida (menos delay = más rápido)
        double factor = isVoidLord() ? 0.4 : 0.8;
        return (int) Math.max(base * factor, 1);
    }

    // ---------------- Habilidades ----------------

    @Override
    public void channel() {
        ScheduledExecutorService service = lord.getThread().getService();
        Player controller = lord.getController();

        controller.setGameMode(GameMode.SPECTATOR);
        lord.getArena().getPlayersInArena().forEach(player -> PlayerUtils.breakElytras(player, 200));

        int delay = scaleDelay(BASE_CHANNEL_DELAY_MS);
        service.schedule(() -> runSync(() -> {
            Location location = lord.getArena().getRandomLocations(1).getFirst();
            controller.teleport(location);
            controller.setGameMode(GameMode.ADVENTURE);
            lord.createExplosion(controller.getLocation(), scaleValue(6));
        }), delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public void cast() {
        ScheduledExecutorService service = lord.getThread().getService();
        CataclysmArea arena = lord.getArena();

        List<Location> targets = new ArrayList<>(arena.getRandomLocations(scaleValue(9)));
        double searchRadius = scaleValue(arena.radius() / 2.0);
        int interval = scaleDelay(BASE_CAST_DURATION_MS) / targets.size();

        // Añadir ubicaciones cardinales extra
        for (int i = 0; i < 4; i++) {
            double offset = (i % 2 == 0 ? searchRadius : -searchRadius);
            Location base = arena.center().clone().add(i < 2 ? 0 : offset, 0, i < 2 ? offset : 0);
            targets.add(getRandomLocation(base, searchRadius / 2));
        }

        for (int i = 0; i < targets.size(); i++) {
            Location target = targets.get(i);
            service.schedule(() -> runSync(() -> spawnBlasarosaBeam(service, target)),
                    (long) interval * i, TimeUnit.MILLISECONDS);
        }
    }

    // ---------------- Internos ----------------

    private void spawnBlasarosaBeam(ScheduledExecutorService service, Location location) {
        World world = location.getWorld();
        playBeamSounds(world, location);

        int repetitions = scaleValue(BASE_BEAM_REPETITIONS);
        int steps = scaleValue(BASE_BEAM_HEIGHT_STEPS);
        int interval = scaleDelay(BASE_BEAM_INTERVAL_MS);

        // Partículas ascendentes
        for (int i = 0; i < repetitions; i++) {
            service.schedule(() -> {
                for (int step = 0; step < steps; step++) {
                    double yOffset = step * 0.5;
                    runSync(() -> {
                        Location offset = location.clone().add(0, yOffset, 0);
                        new ParticleHandler(offset).circle((float) scaleValue(2.5), Particle.END_ROD);
                    });
                }
                runSync(() -> world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 1F, .85F));
            }, (long) interval * i, TimeUnit.MILLISECONDS);
        }

        int radius = lord.getArena().radius();
        castLine(location, scaleValue(radius * 2), 3);

        // Líneas preliminares más rápidas
        for (int i = 0; i < (isVoidLord() ? 6 : 4); i++) {
            service.schedule(() -> castLine(location, scaleValue(radius * 2), 2),
                    i, TimeUnit.SECONDS);
        }

        // Explosión final
        service.schedule(() -> runSync(() -> {
            castLine(location, scaleValue(radius), 1);
            world.spawnParticle(Particle.EXPLOSION_EMITTER, location, scaleValue(3));
            playExplosionSounds(world, location);
        }), 4, TimeUnit.SECONDS);
    }

    private void castLine(Location center, double radius, int mode) {
        ScheduledExecutorService service = lord.getThread().getService();
        World world = center.getWorld();

        int interval = scaleDelay(BASE_LINE_INTERVAL_MS);

        for (int i = 0; i < radius; i++) {
            int distance = i;
            service.schedule(() -> {
                for (int dir = 0; dir < 4; dir++) {
                    int offset = (dir % 2 == 0 ? distance : -distance);

                    int finalDir = dir;
                    runSync(() -> {
                        Location loc = center.clone().add(0, 1, 0);
                        if (finalDir < 2) loc.add(0, 0, offset);
                        else loc.add(offset, 0, 0);

                        switch (mode) {
                            case 1 -> castExplosion(loc, scaleValue(3.0));
                            case 2 -> world.spawnParticle(Particle.END_ROD, loc, scaleValue(3), 0, 0, 0, 0, null, true);
                            default -> world.playSound(loc, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1.0F, 1.35F);
                        }
                    });
                }
            }, (long) interval * i, TimeUnit.MILLISECONDS);
        }
    }

    private void castExplosion(Location location, double radius) {
        World world = location.getWorld();

        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.85F, 1.55F);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, location, scaleValue(4), 0, 0, 0, 0, null, true);

        location.getNearbyLivingEntities(radius).forEach(entity -> {
            if (!entity.equals(lord.getController())) {
                lord.damage(entity, scaleValue(BASE_EXPLOSION_DAMAGE));
            }
        });
    }

    private @NotNull Location getRandomLocation(Location center, double radius) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double theta = random.nextDouble() * 2 * Math.PI;
        double r = radius * Math.sqrt(random.nextDouble());

        double x = center.getX() + Math.round(r * Math.cos(theta));
        double z = center.getZ() + Math.round(r * Math.sin(theta));

        return new Location(center.getWorld(), x + 0.5, center.getY(), z + 0.5);
    }

    // ---------------- Utilidades ----------------

    private void runSync(Runnable task) {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), task);
    }

    private void playBeamSounds(World world, Location location) {
        world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 6F, .65F);
        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 6F, .75F);
        if (isVoidLord()) {
            world.playSound(location, Sound.ENTITY_WITHER_SHOOT, 8F, .5F);
        }
    }

    private void playExplosionSounds(World world, Location location) {
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 6F, 0.95F);
        world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 6F, .55F);
        world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 6F, .65F);
        world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 6F, .75F);
        if (isVoidLord()) {
            world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 10F, .6F);
        }
    }
}