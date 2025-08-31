package org.cataclysm.game.pantheon.bosses.calamity_hydra.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.attacks.CalamityExplosion;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HydrazerAbility extends HydraAbility {
    private ScheduledFuture<?> future;

    public HydrazerAbility(PantheonHydra hydra) {
        super(hydra, Material.MUSIC_DISC_PIGSTEP, "Hidrazer", 1, 30);
    }

    @Override
    public void channel() {
        Player controller = super.hydra.getController();
        Location location = controller.getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_ANGRY, 5F, 0.77F);
        this.throwEntities(20, -6.5);
    }

    @Override
    public void cast() {
        final int duration = 7;

        Player controller = this.hydra.getController();
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration * 20, 1));

        ScheduledExecutorService service = this.hydra.getThread().getService();
        this.future = service.scheduleAtFixedRate(() -> {
            Location location = controller.getLocation();
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> this.hydra.createHydraExplosion(location, 6, CalamityExplosion.Type.MACRO));
        }, 0, 500, TimeUnit.MILLISECONDS);

        service.schedule(() -> this.future.cancel(true), duration, TimeUnit.SECONDS);
    }

    private void throwEntities(double radius, double strength) {
        var controller = this.hydra.getController();
        var location = controller.getLocation();

        var nearby = this.hydra.getNearbyLivingEntities(location, radius);
        for (var target : nearby) {
            var targetLoc = target.getLocation();
            var direction = targetLoc.toVector().subtract(location.toVector()).normalize();
            var velocity = direction.multiply(-strength).setY(2.5);
            target.setVelocity(velocity);
        }
    }
}