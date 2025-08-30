package org.cataclysm.game.pantheon.bosses.the_ragnarok.abilities;

import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.TheRagnarok;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class YouSeeBIGGIRLAbility extends RagnarokAbility {
    private final double explosionRadius = 50;

    public YouSeeBIGGIRLAbility(TheRagnarok ragnarok) {
        super(ragnarok, Material.TNT_MINECART, "YouSeeBIGGIRL", 2, 5);
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
                    new ParticleHandler(location).sphere(Particle.END_ROD, this.explosionRadius, 30);
                    location.getWorld().strikeLightningEffect(location);
                    for (var livingEntity : this.ragnarok.getNearbyLivingEntities(location, this.explosionRadius)) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 0, true, true));
                    }
                });
            }, i, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cast() {
        var controller = this.ragnarok.getController();
        var location = controller.getLocation();

        location.getWorld().playSound(location, "cataclysm.pantheon.death", 20F, 0.95F);

        for (int i = 0; i < 5; i++) {
            Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                var locations = ragnarok.getArena().getRandomLocations((int) this.explosionRadius);
                for (Location r : locations) r.getWorld().strikeLightning(r);

                new ParticleHandler(location).sphere(Particle.EXPLOSION_EMITTER, this.explosionRadius, this.explosionRadius / 2);
                location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 20F, 0.95F);

                this.ragnarok.getNearbyLivingEntities(location, this.explosionRadius).forEach(livingEntity -> {
                    this.ragnarok.damage(livingEntity, 150.0);
                });
            }, 12 * i);
        }
    }
}
