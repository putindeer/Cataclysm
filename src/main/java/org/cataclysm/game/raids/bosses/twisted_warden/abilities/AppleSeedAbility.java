package org.cataclysm.game.raids.bosses.twisted_warden.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.api.particle.ParticleHandler;
import org.cataclysm.game.raids.bosses.twisted_warden.TwistedWarden;

import java.util.concurrent.TimeUnit;

public class AppleSeedAbility extends Ability {
    private final double explosionRadius = 20.0;
    private final TwistedWarden warden;

    public AppleSeedAbility(TwistedWarden warden) {
        super(Material.TNT_MINECART, "Apple Seed", 5, 80);
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
                    new ParticleHandler(location).sphere(Particle.END_ROD, this.explosionRadius, 30);
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

        new ParticleHandler(location).sphere(Particle.EXPLOSION_EMITTER, this.explosionRadius, 10);
        location.getWorld().playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2F, 0.6F);

        final var baseDamage = 200.0;

        this.warden.getNearbyLivingEntities(location, this.explosionRadius).forEach(livingEntity -> {
            double distance = location.distance(livingEntity.getLocation());
            double damageMultiplier = 1.0 - (distance / this.explosionRadius);
            double finalDamage = baseDamage * damageMultiplier;

            livingEntity.damage(finalDamage, controller);
        });
    }
}
