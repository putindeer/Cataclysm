package org.cataclysm.game.raids.bosses.pale_king.abilities;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.raids.bosses.pale_king.PaleKing;
import org.cataclysm.game.raids.bosses.pale_king.PaleKingUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class BlasarosaAbility extends PaleAbility {
    public BlasarosaAbility(PaleKing king) {
        super(king, Material.OPEN_EYEBLOSSOM, "Blasarosa", 0, 8);
    }

    @Override
    public void channel() {
        ScheduledExecutorService service = super.king.getThread().getService();
        Player controller = super.king.getController();

        controller.setGameMode(GameMode.SPECTATOR);

        super.king.getArena().getPlayersInArena().forEach(player -> PaleKingUtils.breakElytras(player, 200));

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                Location location = super.king.getArena().getRandomLocations(1).getFirst();
                controller.teleport(location);
                controller.setGameMode(GameMode.ADVENTURE);
                super.king.castPaleExplosion(controller.getLocation(), 6);
            });
        }, 12000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void cast() {
        ScheduledExecutorService service = super.king.getThread().getService();
        CataclysmArea arena = super.king.getArena();

        List<Location> randomLocations = new ArrayList<>(arena.getRandomLocations(9));
        double searchRadius = (double) arena.radius() / 2;
        int totalDuration = 7000;
        int interval = totalDuration / randomLocations.size();

        for (int i = 0; i < 4; i++) {
            double operator = searchRadius;
            if (i == 1 || i == 3) operator *= -1;

            Location location = arena.center().clone();
            if (i < 2) location.add(0, 0, operator);
            else location.add(operator, 0, 0);

            randomLocations.add(this.getRandomLocation(location, searchRadius / 2));
        }

        for (int i = 0; i < randomLocations.size(); i++) {
            int finalI = i;
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    Location location = randomLocations.get(finalI);
                    this.spawnBlasarosaBeam(service, location);
                });
            }, (long) interval * i, TimeUnit.MILLISECONDS);
        }
    }

    private void spawnBlasarosaBeam(ScheduledExecutorService service, Location location) {
        World world = location.getWorld();
        int delay = 4;

        world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 6F, .65F);
        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 6F, .75F);
        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 6F, .8F);

        for (int i = 0; i < 5; i++) {
            service.schedule(() -> {
                for (int j = 0; j < 50; j++) {
                    int finalJ = j;
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                        Location offSet = location.clone().add(0, (finalJ * .5), 0);
                        ParticleHandler handler = new ParticleHandler(offSet);
                        handler.circle(2.5F, Particle.END_ROD);
                    });
                }
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 1F, .85F);
                });
            }, 500 * i, TimeUnit.MILLISECONDS);
        }

        this.castLine(location, super.king.getArena().radius() * 2, 3);
        for (int i = 0; i < delay; i++) {
            service.schedule((() -> this.castLine(location, super.king.getArena().radius() * 2, 2)), i, TimeUnit.SECONDS);
        }

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                this.castLine(location, super.king.getArena().radius(), 1);
                world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 3);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 6F, 0.95F);
                world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 6F, .55F);
                world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 6F, .65F);
                world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 6F, .75F);
            });
        }, delay, TimeUnit.SECONDS);
    }

    private void castLine(Location center, double radius, int mode) {
        ScheduledExecutorService service = super.king.getThread().getService();
        World world = center.getWorld();

        for (int i = 0; i < radius; i++) {
            int finalI = i;
            service.schedule(() -> {
                for (int j = 0; j < 4; j++) {
                    int operator = finalI;
                    if (j == 1 || j == 3) operator *= -1;

                    int finalJ = j;
                    int finalOperator = operator;
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                        Location location = center.clone().add(0, 1, 0);

                        if (finalJ < 2) location.add(0, 0, finalOperator);
                        else location.add(finalOperator, 0, 0);

                        if (mode == 1) this.castExplosion(location, 3);
                        if (mode == 2) world.spawnParticle(Particle.END_ROD, location, 2, 0, 0, 0, 0, null, true);
                        if (mode > 2) world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 0.85F, 1.55F);
                    });
                }
            }, (25L * i), TimeUnit.MILLISECONDS);
        }
    }

    private void castExplosion(Location location, double radius) {
        World world = location.getWorld();
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.75F, 1.55F);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 0.75F, 1.25F);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 2, 0, 0, 0, 0, null, true);

        location.getNearbyLivingEntities(radius).forEach(livingEntity -> {
            if (livingEntity.equals(super.king.getController())) return;
            super.king.damage(livingEntity, 180);
        });
    }

    private @NotNull Location getRandomLocation(Location center, double radius) {
        var world = center.getWorld();
        var random = ThreadLocalRandom.current();

        double theta = random.nextDouble() * 2 * Math.PI;
        double r = radius * Math.sqrt(random.nextDouble());

        double xOffset = r * Math.cos(theta);
        double zOffset = r * Math.sin(theta);

        double x = center.getBlockX() + Math.round(xOffset);
        double z = center.getBlockZ() + Math.round(zOffset);
        double y = center.getY();

        return new Location(world, x + 0.5, y, z + 0.5);
    }
}
