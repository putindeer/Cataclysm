package org.cataclysm.game.pantheon.bosses.pale_king.abilities;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.pantheon.bosses.pale_king.PaleKing;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class KingsoulAbility extends PaleAbility {
    public KingsoulAbility(PaleKing king) {
        super(king, Material.WHITE_DYE, "Kingsoul", 2, 10);
    }

    @Override
    public void channel() {
        Player controller = super.king.getController();

        Location location = controller.getLocation();
        World world = controller.getWorld();

        int duration = (this.channelTime + 1) * 20;
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 9, false, false));

        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 10F, .65F);
        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 10F, .75F);
        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 10F, .85F);

        ScheduledExecutorService service = super.king.getThread().getService();
        ParticleHandler handler = new ParticleHandler(location);
        for (int i = 0; i < this.channelTime * 10; i++) {
            double pitchAmplifier = i * 0.05;
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    handler.sphere(Particle.END_ROD, 12.5, 30);
                    handler.sphere(Particle.WHITE_SMOKE, 11, 30);

                    world.spawnParticle(Particle.END_ROD, location, 7, .25, 2, .25, 0, null, true);
                    world.spawnParticle(Particle.WHITE_SMOKE, location, 10, .75, 3, .75, 0, null, true);

                    world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 0.6F, (float) (1.35 + pitchAmplifier));
                    world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1F, (float) (1.05 + pitchAmplifier));
                    world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 1F, (float) (1.45 + pitchAmplifier));
                });
            }, i * 100L, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void cast() {
        ScheduledExecutorService service = super.king.getThread().getService();
        Player controller = super.king.getController();

        Location controllerLocation = controller.getLocation();
        World world = controllerLocation.getWorld();

        this.summonExplosion(controllerLocation, 12.5);
        world.playSound(controllerLocation, Sound.ITEM_TRIDENT_THUNDER, 12F, .625F);

        ThreadLocalRandom randomizer = ThreadLocalRandom.current();
        List<Location> locations = super.king.getArena().getRandomLocations(25);
        for (int i = 0; i < locations.size(); i++) {
            Location location = locations.get(i).clone().add(0, 2, 0);
            if (randomizer.nextBoolean()) location.add(0, randomizer.nextDouble(0, 30), 0);

            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    ParticleHandler handler = new ParticleHandler(location);
                    handler.sphere(Particle.END_ROD, 4, 10);

                    world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 12F, 1.895F);
                    world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, .6F, 1.295F);

                    service.schedule(() -> {
                        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.summonExplosion(location, 5));
                    }, 1500, TimeUnit.MILLISECONDS);
                });
            }, i * 55L, TimeUnit.MILLISECONDS);
        }
    }

    private void summonExplosion(Location location, double radius) {
        World world = location.getWorld();
        Collection<LivingEntity> livingEntities = location.getNearbyLivingEntities(radius + 1.5);

        livingEntities.forEach(livingEntity -> {
            if (livingEntity.equals(super.king.getController())) return;
            super.king.damage(livingEntity, 10 * radius);
        });

        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.EXPLOSION_EMITTER, radius - 2, radius / 2);

        world.playSound(location, Sound.ITEM_TRIDENT_RETURN, (float) (radius * 2), .6F);
        world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, (float) radius, 1.2F);
    }
}
