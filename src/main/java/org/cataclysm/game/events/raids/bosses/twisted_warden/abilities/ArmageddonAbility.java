package org.cataclysm.game.events.raids.bosses.twisted_warden.abilities;

import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.events.raids.bosses.twisted_warden.TwistedWarden;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ArmageddonAbility extends Ability {
    private final TwistedWarden warden;

    public ArmageddonAbility(TwistedWarden warden) {
        super(Material.SKULL_BANNER_PATTERN, "Armageddon", 1, 30);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        for (var fighter : this.warden.getArena().getPlayersInArena()) {
            fighter.playSound(location, Sound.ENTITY_WARDEN_ROAR, 10F, 0.5F);
            fighter.playSound(location, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.5F);
            fighter.playSound(location, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.76F);
        }
    }

    @Override
    public void cast() {
        var arena = this.warden.getArena();
        var world = arena.center().getWorld();
        var level = ((CraftWorld) world).getHandle();

        String[] twistedMobTypes = {
                "Skeleton", "Zombie", "Enderman", "Creeper",
                "Spider", "Blaze",
        };

        var locations = arena.getRandomLocations(20);
        for (var loc : locations) {
            var random = ThreadLocalRandom.current().nextInt(0, twistedMobTypes.length);
            var name = "Twisted" + twistedMobTypes[random];

            var twistedMob = CataclysmMob.instantiateMob(name, level);
            spawnDelayedMob(loc, twistedMob);
        }
    }

    public void spawnDelayedMob(Location location, CataclysmMob cataclysmMob) {
        var world = location.getWorld();

        world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_ABOUT_TO_SPAWN_ITEM, 2.5F, 0.75F);

        for (int i = 0; i < 5; i++) {
            var loc = location.clone().add(0, i, 0);
            new ParticleHandler(loc).circle(2, Particle.SOUL_FIRE_FLAME);
        }

        this.warden.getThread().getService().schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1F, 0.7F);
                cataclysmMob.addFreshEntity(location);
            });
        }, 2, TimeUnit.SECONDS);
    }
}
