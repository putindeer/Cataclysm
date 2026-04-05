package org.cataclysm.game.events.raids.bosses.pale_king.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.events.raids.bosses.pale_king.PaleKing;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//TODO Decorar...
public class EmbraceTheVoidAbility extends PaleAbility {
    private final int delay = 4;

    public EmbraceTheVoidAbility(PaleKing king) {
        super(king, Material.NETHER_STAR, "Embrace The Void", 5, 10, true);
    }

    @Override
    public void channel() {
        Player controller = super.king.getController();
        controller.setGameMode(GameMode.SPECTATOR);

        ScheduledExecutorService service = super.king.getThread().getService();

        CataclysmArea arena = super.king.getArena();
        Location center = arena.center().clone().add(0, 15, 0);
        ParticleHandler handler = new ParticleHandler(center);
        World world = center.getWorld();

        arena.getLivingEntitiesInArena().forEach(livingEntity -> {
            world.playSound(livingEntity, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3F, 0.55F);
            world.playSound(livingEntity, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3F, 0.65F);
            world.playSound(livingEntity, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 3F, 0.75F);
        });

        for (int i = 0; i < this.channelTime; i++) {
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    world.playSound(center, Sound.ITEM_TRIDENT_RETURN, 8F, 1.15F);
                    world.playSound(center, Sound.ITEM_TRIDENT_RETURN, 8F, 0.55F);
                    handler.sphere(Particle.END_ROD, 10, 10);
                    handler.sphere(Particle.WHITE_SMOKE, 10, 10);
                    this.summonVoidSphere(center, 20);
                    arena.getLivingEntitiesInArena().forEach(livingEntity -> {
                        if (livingEntity.hasPotionEffect(DisperEffect.EFFECT_TYPE)) return;
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 15, 0));
                        world.playSound(livingEntity, Sound.ENTITY_GUARDIAN_DEATH, 1F, 0.5F);
                    });
                });
            }, i, TimeUnit.SECONDS);
        }

        List<Location> locations = super.king.getArena().getRandomLocations(7);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            for (int i = 0; i < locations.size(); i++) {
                Location location = locations.get(i);
                service.schedule(() -> {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.spawnSphere(location, 4.0, this.channelTime + this.delay));
                }, 250L * i, TimeUnit.MILLISECONDS);
            }
        }, 30);

        service.schedule(() -> {
            for (int j = 0; j < 3; j++) {
                service.schedule(() -> {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                        handler.sphere(Particle.EXPLOSION_EMITTER, arena.radius(), (double) arena.radius() / 4);
                        world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 8F, 1.F);
                        world.playSound(center, Sound.ITEM_TRIDENT_THUNDER, 8F, 0.5F);
                        world.playSound(center, Sound.ITEM_TRIDENT_THUNDER, 8F, 1.55F);
                        arena.getLivingEntitiesInArena().forEach(livingEntity -> {
                            if (livingEntity.hasPotionEffect(DisperEffect.EFFECT_TYPE) || livingEntity.equals(super.king.getController())) return;
                            super.king.damage(livingEntity, 120);
                        });
                    });
                }, j * 600, TimeUnit.MILLISECONDS);
            }
        }, this.channelTime, TimeUnit.SECONDS);

        service.schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                Location location = super.king.getArena().getRandomLocations(1).getFirst();
                controller.teleport(location);
                controller.setGameMode(GameMode.ADVENTURE);
                super.king.castPaleExplosion(controller.getLocation(), 6);
            });
        }, this.channelTime + this.delay + 6, TimeUnit.SECONDS);
    }

    private void spawnSphere(Location location, double radius, int explosionDelay) {
        Location particleCenter = location.clone().add(0, 1, 0);
        ParticleHandler handler = new ParticleHandler(particleCenter);
        ScheduledExecutorService service = super.king.getThread().getService();
        World world = location.getWorld();

        for (int i = 0; i < explosionDelay * 2; i++) {
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    location.getNearbyLivingEntities(radius).forEach(livingEntity -> {
                        livingEntity.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 80, 0, false, false));
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 80, 0, false, false));
                    });

                    handler.sphere(Particle.WHITE_SMOKE, radius, radius * 3);
                    handler.sphere(Particle.END_ROD, radius, radius * 3);
                    world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 4F, 0.55F);
                });
            }, i * 500L, TimeUnit.MILLISECONDS);
        }

        service.schedule(() -> {
            for (int i = 0; i < this.delay * 2; i++) {
                service.schedule(() -> {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.summonVoidSphere(particleCenter, radius));
                }, i * 500, TimeUnit.MILLISECONDS);
            }
        }, this.channelTime + 1, TimeUnit.SECONDS);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> this.summonExplosion(location, radius * 5), explosionDelay * 20L);
    }

    private void summonVoidSphere(Location location, double radius) {
        ParticleHandler handler = new ParticleHandler(location);
        handler.sphere(Particle.SMOKE, radius / 2, radius * 3);
        handler.sphere(Particle.SQUID_INK, radius / 3, radius * 3);
        handler.sphere(Particle.SMOKE, radius, radius);
        location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 5, .5F);
        location.getWorld().playSound(location, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS, 5, .5F);
        location.getNearbyLivingEntities(radius).forEach(livingEntity -> livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 40, 4)));
    }

    private void summonExplosion(Location location, double radius) {
        ParticleHandler handler = new ParticleHandler(location);
        ScheduledExecutorService service = super.king.getThread().getService();
        World world = location.getWorld();

        for (int i = 0; i < 3; i++) {
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    location.getNearbyLivingEntities(radius).forEach(livingEntity -> super.king.damage(livingEntity, 120));

                    world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 8F, 1.15F);
                    world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 8F, 0.55F);
                    world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, 8F, 1.95F);

                    handler.sphere(Particle.END_ROD, radius, radius);
                    handler.sphere(Particle.EXPLOSION_EMITTER, radius, radius / 3);
                });
            }, i * 750, TimeUnit.MILLISECONDS);
        }
    }
}
