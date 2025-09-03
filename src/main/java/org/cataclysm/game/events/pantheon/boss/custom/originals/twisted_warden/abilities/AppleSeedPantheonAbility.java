package org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.ParticleHandler;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.PantheonWarden;

import java.util.concurrent.TimeUnit;

public class AppleSeedPantheonAbility extends PantheonAbility {
    private final double explosionRadius = 30.0;
    private final PantheonWarden warden;

    public AppleSeedPantheonAbility(PantheonWarden warden) {
        super(Material.TNT_MINECART, "Apple Seed", 2);
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
                    new ParticleHandler(location).sphere(Particle.END_ROD, this.explosionRadius, this.explosionRadius * 2);
                    location.getWorld().strikeLightningEffect(location);
                    for (var livingEntity : this.warden.getNearbyLivingEntities(location, this.explosionRadius)) {
                        livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 15, 0, true, true));
                    }
                });
                }, i, TimeUnit.SECONDS);
        }
    }

    @Override
    public void cast() {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        new ParticleHandler(location).sphere(Particle.EXPLOSION_EMITTER, this.explosionRadius, this.explosionRadius * 2);
        location.getWorld().playSound(location, "cataclysm.pantheon.radiance", 10F, 0.78F);

        this.warden.getNearbyLivingEntities(location, this.explosionRadius).forEach(livingEntity -> {
            warden.damage(livingEntity, 200);
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 200, 0));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 200, 2));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 200, 0));
            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 0));
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.65F);
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2F, 0.65F);
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 2F, 0.55F);
            livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1F, 0.55F);
        });
    }
}
