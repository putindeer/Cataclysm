package org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.abilities;

import org.bukkit.*;
import org.bukkit.craftbukkit.CraftWorld;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.TheRagnarok;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class CataclysmAbility extends RagnarokAbility {
    public CataclysmAbility(TheRagnarok ragnarok) {
        super(ragnarok, Material.SKELETON_SKULL, "CATACLYSM", 3);
    }

    @Override
    public void channel() {
        var controller = ragnarok.getController();
        var location = controller.getLocation();

        for (var fighter : ragnarok.getArena().getPlayersInArena()) {
            fighter.playSound(location, Sound.ENTITY_WARDEN_ROAR, 10F, 0.5F);
            fighter.playSound(location, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.5F);
            fighter.playSound(location, Sound.ITEM_GOAT_HORN_SOUND_6, 10F, 0.76F);
        }

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            var arena = this.ragnarok.getArena();
            var world = arena.center().getWorld();
            var level = ((CraftWorld) world).getHandle();

            String[] mobs = {
                    "TwistedBrute", "Headsman", "CataclystSkeleton", "CataclystStray", "MirageEye", "PaleBlaze",
                    "PaleWhale", "PaleWhale", "PaleEnderman", "TwistedZombie", "ArcaneSpider", "AggressiveLlama"
            };

            var locations = arena.getRandomLocations(40);
            for (var loc : locations) {
                var random = ThreadLocalRandom.current().nextInt(0, mobs.length);
                var calamityMob = CataclysmMob.instantiateMob(mobs[random], level);
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

        this.ragnarok.getThread().getService().schedule(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                world.strikeLightning(location);
                world.playSound(location, Sound.BLOCK_TRIAL_SPAWNER_SPAWN_MOB, 1F, 0.7F);
                cataclysmMob.addFreshEntity(location);
            });
        }, 2, TimeUnit.SECONDS);
    }
}
