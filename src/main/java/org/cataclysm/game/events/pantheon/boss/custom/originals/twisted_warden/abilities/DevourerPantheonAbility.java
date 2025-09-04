package org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.abilities;

import lombok.Getter;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.PantheonWarden;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class DevourerPantheonAbility extends PantheonAbility {
    private static final @Getter int RADIUS = 7;

    private final PantheonWarden warden;
    private ScheduledFuture<?> future;

    public DevourerPantheonAbility(PantheonWarden warden) {
        super(Material.BLAZE_POWDER, "Devourer", 2);
        this.warden = warden;
    }

    @Override
    public void channel() {
        Player controller = this.warden.getController();
        controller.getWorld().playSound(controller.getLocation(), Sound.ENTITY_WARDEN_ANGRY, 5F, 0.77F);

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.playSound(player, Sound.ITEM_TRIDENT_THUNDER, 2F, 0.666F);
            player.playSound(player, Sound.ENTITY_ELDER_GUARDIAN_CURSE, 2F, 0.5F);
        }
    }

    @Override
    public void cast() {
        ScheduledExecutorService service = this.warden.getThread().getService();

        this.future = service.scheduleAtFixedRate(this::tick, 0, 250, TimeUnit.MILLISECONDS);

        service.schedule(this::stop, 15, TimeUnit.SECONDS);
    }

    private void tick() {
        this.drainSphere(RADIUS, RADIUS * 4);
        this.absorbEffects(RADIUS);
    }

    private void stop() {
        this.future.cancel(true);
        this.future = null;
    }

    private void drainSphere(double radius, int steps) {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            location.getWorld().playSound(location, Sound.BLOCK_FIRE_EXTINGUISH, .7F, 1.2F);
            location.getWorld().playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, .7F, 0.7F);
        });

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
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                            location.getWorld().spawnParticle(Particle.SCULK_SOUL, particleLoc, 0, 0, 0, 0, 0, null, true);
                            location.getWorld().spawnParticle(Particle.SOUL_FIRE_FLAME, particleLoc, 0, 0, 0, 0, 0, null, true);
                        }
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
