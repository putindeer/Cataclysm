package org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.abilities;

import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.ending.pantheon.boss.gods.ragnarok.TheRagnarok;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class YouSeeBIGGIRLAbility extends RagnarokAbility {
    public static final float EXPLOSION_RADIUS = 50.0F;

    public YouSeeBIGGIRLAbility(TheRagnarok ragnarok) {
        super(ragnarok, Material.TNT_MINECART, "YouSeeBIGGIRL", 2);
    }

    @Override
    public void channel() {
        var controller = ragnarok.getController();
        var duration = (this.channelTime * 20);

        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 4, true, true, true));
        controller.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, true, true));

        for (int i = 0; i < this.channelTime; i++) {
            this.ragnarok.getThread().getService().schedule(() -> {
                var location = controller.getLocation();

                Bukkit.getOnlinePlayers().forEach(player -> {
                    for (int j = 0; j < 10; j++) {
                        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                            float volume = new Random().nextFloat(2, 4);
                            float pitch = new Random().nextFloat(0.5F, 1.5F);
                            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ELDER_GUARDIAN_CURSE, volume, pitch);
                        }, j * 2L);
                    }
                });

                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    new ParticleHandler(location).sphere(Particle.END_ROD, EXPLOSION_RADIUS, 30);
                    location.getWorld().strikeLightningEffect(location);
                    for (var livingEntity : this.ragnarok.getNearbyLivingEntities(location, EXPLOSION_RADIUS)) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 0, true, true));
                    }
                });
            }, i, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cast() {
        Location location = ragnarok.getController().getLocation();
        World world = location.getWorld();

        location.getWorld().playSound(location, "cataclysm.pantheon.death", 20F, 0.95F);

        for (int i = 0; i < 5; i++) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                List<Location> randomLocations = ragnarok.getArena().getRandomLocations((int) EXPLOSION_RADIUS);
                for (Location randomLoc : randomLocations) world.strikeLightning(randomLoc);

                new ParticleHandler(location).sphere(Particle.EXPLOSION_EMITTER, EXPLOSION_RADIUS, EXPLOSION_RADIUS / 2);
                world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 20F, 0.95F);

                ragnarok.getNearbyLivingEntities(location, EXPLOSION_RADIUS).forEach(livingEntity -> {
                    ragnarok.damage(livingEntity, 150.0);
                });
            }, 12 * i);
        }
    }
}
