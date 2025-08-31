package org.cataclysm.game.events.raids.bosses.twisted_warden.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.events.raids.bosses.twisted_warden.TwistedWarden;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DevourerAbility extends Ability {
    private final TwistedWarden warden;
    private ScheduledFuture<?> future;

    public DevourerAbility(TwistedWarden warden) {
        super(Material.BLAZE_POWDER, "Devourer", 2, 15);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = this.warden.getController();
        var location = controller.getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_ANGRY, 5F, 0.77F);
    }

    @Override
    public void cast() {
        var service = this.warden.getThread().getService();
        this.future = service.scheduleAtFixedRate(this::tick, 0, 500, TimeUnit.MILLISECONDS);
        service.schedule(this::stop, 5, TimeUnit.SECONDS);
    }

    private void tick() {
        double radius = 5;
        this.drainSphere(radius, 50);
        this.absorbEffects(radius);
    }

    private void stop() {
        this.future.cancel(true);
        this.future = null;
    }

    private void drainSphere(double radius, int steps) {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        for (int i = 0; i <= steps; i++) {
            double phi = Math.PI * i / steps;
            double sinPhi = Math.sin(phi);
            double cosPhi = Math.cos(phi);

            for (int j = 0; j < steps; j++) {
                double theta = 2 * Math.PI * j / steps;
                double sinTheta = Math.sin(theta);
                double cosTheta = Math.cos(theta);

                double x = radius * sinPhi * cosTheta;
                double y = radius * cosPhi;
                double z = radius * sinPhi * sinTheta;

                var particleLoc = location.clone().add(x, y, z);
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () ->
                        location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 0, 0, 0, 0, 0, null, true)
                );
            }
        }
    }

    private void absorbEffects(double radius) {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            for (Player target : this.warden.getNearbyFighters(location, radius)) {
                if (target.getGameMode() == GameMode.SPECTATOR) continue;

                for (PotionEffect effect : target.getActivePotionEffects()) {
                    var type = effect.getType();
                    if (!type.getCategory().equals(PotionEffectTypeCategory.BENEFICIAL)) continue;

                    target.removePotionEffect(type);
                    controller.addPotionEffect(effect);
                }
            }
        });
    }
}
