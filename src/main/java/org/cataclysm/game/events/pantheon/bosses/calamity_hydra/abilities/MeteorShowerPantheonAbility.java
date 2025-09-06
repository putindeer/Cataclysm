package org.cataclysm.game.events.pantheon.bosses.calamity_hydra.abilities;

import org.bukkit.*;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.attacks.PantheonCharge;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MeteorShowerPantheonAbility extends PantheonHydraAbility {
    private final PantheonHydra hydra;

    public MeteorShowerPantheonAbility(PantheonHydra hydra) {
        super(Material.FIRE_CHARGE, "Draco Meteor", 0);
        this.hydra = hydra;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {
        Location location = this.hydra.getController().getLocation();
        World world = location.getWorld();

        ScheduledExecutorService service = this.hydra.getThread().getService();
        CataclysmArea arena = this.hydra.getArena();

        int amount = 40;
        int power = 15;
        int loops = 5;

        for (int i = 0; i < loops; i++) {
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    world.playSound(location, Sound.ITEM_TRIDENT_THUNDER, power, 0.5F);
                    world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, power, 0.55F);
                    world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, power, 0.75F);

                    List<Location> randomLocations = arena.getRandomLocations(amount);
                    for (int j = 0; j < randomLocations.size(); j++) {
                        Location spawnLoc = randomLocations.get(j);

                        Location dropLoc = spawnLoc.clone();
                        dropLoc.setY(arena.center().getY() + 60);
                        if (!dropLoc.getBlock().getType().isAir()) continue;

                        ParticleHandler handler = new ParticleHandler(spawnLoc.add(0, 1, 0));
                        handler.circle(15F, 150, 1, (float) spawnLoc.getY(), Particle.FLAME, Particle.SMOKE);

                        service.schedule(() -> {
                            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                                PantheonCharge charge = new PantheonCharge(this.hydra, power, 3, 7);
                                charge.drop(dropLoc, power);
                            });
                        }   , (long) j * 100, TimeUnit.MILLISECONDS);
                    }
                });
            }, i * 4, TimeUnit.SECONDS);
        }
    }
}
