package org.cataclysm.game.raids.bosses.calamity_hydra.abilities.rage;

import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.raids.bosses.calamity_hydra.CalamityHydra;
import org.cataclysm.game.raids.bosses.calamity_hydra.rage.RageAbility;
import org.cataclysm.game.raids.bosses.twisted_warden.TwistedWarden;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CalamityAbility extends RageAbility {
    public CalamityAbility(CalamityHydra hydra) {
        super(hydra, Material.SKULL_BANNER_PATTERN, "Calamity", 15, 30, true);
    }

    @Override
    public void tick() {
        var controller = super.hydra.getController();
        var location = controller.getLocation();

        for (var fighter : super.hydra.getArena().getPlayersInArena()) {
            fighter.playSound(location, Sound.ENTITY_WARDEN_ROAR, 10F, 0.5F);
            fighter.playSound(location, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.5F);
            fighter.playSound(location, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.76F);
        }

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            var arena = this.hydra.getArena();
            var world = arena.center().getWorld();
            var level = ((CraftWorld) world).getHandle();

            String[] calamityMobs = {"Blaze", "Enderman", "Ghast", "Piglin", "Skeleton"};

            var locations = arena.getRandomLocations(40);
            for (var loc : locations) {
                var random = ThreadLocalRandom.current().nextInt(0, calamityMobs.length);
                var name = "Calamity" + calamityMobs[random];

                var calamityMob = CataclysmMob.instantiateMob(name, level);
                spawnDelayedMob(loc, calamityMob);
            }
        }, 20);
    }

    public void spawnDelayedMob(Location location, CataclysmMob cataclysmMob) {
        var world = location.getWorld();

        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 2.5F, 0.75F);

        for (int i = 0; i < 5; i++) {
            var loc = location.clone().add(0, i, 0);
            new ParticleHandler(loc).circle(2, Particle.SOUL_FIRE_FLAME);
        }

        this.hydra.getThread().getService().schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                world.strikeLightning(location);
                world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1F, 0.7F);
                cataclysmMob.addFreshEntity(location);
            });
        }, 2, TimeUnit.SECONDS);
    }
}
