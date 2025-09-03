package org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.abilities;

import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.PantheonWarden;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class ArmageddonPantheonAbility extends PantheonAbility {
    private final PantheonWarden warden;

    public ArmageddonPantheonAbility(PantheonWarden warden) {
        super(Material.SKULL_BANNER_PATTERN, "Armageddon", 1);
        this.warden = warden;
    }

    @Override
    public void channel() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 40, 0));
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 100, 0));
            player.playSound(player, Sound.ENTITY_WARDEN_ROAR, 10F, 0.5F);
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.5F);
            player.playSound(player, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.76F);
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

        var locations = arena.getRandomLocations(40);
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
        new ParticleHandler(location).sphere(Particle.SOUL_FIRE_FLAME, 3, 6);

        for (int i = 0; i < 20; i++) {
            var loc = location.clone().add(0, i, 0);
            ParticleHandler handler = new ParticleHandler(loc);
            handler.circle(1, Particle.SOUL_FIRE_FLAME);
            handler.circle(2, Particle.SCULK_SOUL);
        }

        this.warden.getThread().getService().schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1F, 0.7F);
                cataclysmMob.addFreshEntity(location);
            });
        }, 1, TimeUnit.SECONDS);
    }
}
