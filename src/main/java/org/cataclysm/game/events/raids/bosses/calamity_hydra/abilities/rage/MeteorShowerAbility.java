package org.cataclysm.game.events.raids.bosses.calamity_hydra.abilities.rage;

import org.bukkit.*;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.CataclysmArea;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.CalamityHydra;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.attacks.CalamityCharge;
import org.cataclysm.game.events.raids.bosses.calamity_hydra.rage.RageAbility;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MeteorShowerAbility extends RageAbility {
    private final CalamityHydra hydra;

    public MeteorShowerAbility(CalamityHydra hydra) {
        super(hydra, Material.FIRE_CHARGE, "Lluvia de Meteoritos", 5, 50, true);
        this.hydra = hydra;
    }

    @Override
    public void tick() {
        ScheduledExecutorService service = this.hydra.getThread().getService();
        CataclysmArea arena = this.hydra.getArena();

        int amount = 40;
        int power = 15;
        int loops = 4;

        for (int i = 0; i < loops; i++) {
            service.schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    this.hydra.playSound(Sound.ITEM_TRIDENT_THUNDER, power, 0.5F);
                    this.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, power, 0.55F);
                    this.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, power, 0.75F);

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
                                CalamityCharge charge = new CalamityCharge(this.hydra, power, 3, 7);
                                charge.drop(dropLoc, power);
                            });
                        }, (long) j * 100, TimeUnit.MILLISECONDS);
                    }
                });
            }, i * 4, TimeUnit.SECONDS);
        }
    }
}
