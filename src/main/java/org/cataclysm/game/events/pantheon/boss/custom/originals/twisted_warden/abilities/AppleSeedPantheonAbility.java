package org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.abilities;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.PantheonWarden;

import java.util.concurrent.TimeUnit;

public class AppleSeedPantheonAbility extends PantheonAbility {
    private static final @Getter double EXPLOSION_RADIUS = 40.0;

    private final PantheonWarden warden;

    public AppleSeedPantheonAbility(PantheonWarden warden) {
        super(Material.TNT_MINECART, "Apple Seed", 3);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = this.warden.getController();
        var duration = (this.channelTime * 20);

        controller.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, duration, 4, true, true, true));
        controller.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, duration, 0, true, true, true));

        for (int i = 0; i < this.channelTime; i++) {
            this.warden.getThread().getService().schedule(() -> {
                var location = controller.getLocation();

                this.warden.playAlarmSound(location, 10F);

                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    new ParticleHandler(location).sphere(Particle.END_ROD, EXPLOSION_RADIUS, EXPLOSION_RADIUS * 3);
                    location.getWorld().strikeLightningEffect(location);
                    for (var livingEntity : this.warden.getNearbyLivingEntities(location, EXPLOSION_RADIUS)) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 0, true, true));
                    }
                });
                }, i, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cast() {
        Location location = warden.getController().getLocation();
        World world = location.getWorld();

        world.playSound(location, "cataclysm.pantheon.radiance", 10F, 0.78F);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, location, (int) (EXPLOSION_RADIUS * 5), EXPLOSION_RADIUS, EXPLOSION_RADIUS, EXPLOSION_RADIUS, 0, null, true);
        this.warden.getNearbyLivingEntities(location, EXPLOSION_RADIUS).forEach(livingEntity -> {
            warden.damage(livingEntity, 200);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
            world.playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.65F);
            world.playSound(livingEntity.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2F, 0.65F);
            world.playSound(livingEntity.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2F, 0.55F);
            world.playSound(livingEntity.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1F, 0.55F);
        });
    }
}
