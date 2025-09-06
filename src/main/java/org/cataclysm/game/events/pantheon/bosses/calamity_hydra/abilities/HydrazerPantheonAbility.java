package org.cataclysm.game.events.pantheon.bosses.calamity_hydra.abilities;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.attacks.PantheonExplosion;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Getter
public class HydrazerPantheonAbility extends PantheonHydraAbility {
    private static final int DURATION = 7;
    private static final int TICKS = 7;

    private ScheduledFuture<?> future;

    private final PantheonHydra hydra;

    public HydrazerPantheonAbility(PantheonHydra hydra) {
        super(Material.MUSIC_DISC_PIGSTEP, "Hidrazer", 1);
        this.hydra = hydra;
    }

    @Override
    public void channel() {
        Location location = this.hydra.getController().getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_ANGRY, 5F, 0.77F);
        this.throwEntities(30, -6.5);
    }

    @Override
    public void cast() {
        Player controller = this.hydra.getController();
        ScheduledExecutorService service = this.hydra.getThread().getService();

        controller.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, TICKS, 4));
        this.future = service.scheduleAtFixedRate(() -> {
            Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () ->
                    new PantheonExplosion(this.hydra).create(controller.getLocation(), 8, PantheonExplosion.Type.MACRO));
        }, 0, 250, TimeUnit.MILLISECONDS);
        service.schedule(() -> this.future.cancel(true), DURATION, TimeUnit.SECONDS);
    }

    private void throwEntities(double radius, double strength) {
        var controller = this.hydra.getController();
        var location = controller.getLocation();
        var nearby = this.hydra.getNearbyLivingEntities(location, radius);
        for (var target : nearby) {
            var targetLoc = target.getLocation();
            var direction = targetLoc.toVector().subtract(location.toVector()).normalize();
            var velocity = direction.multiply(-strength).setY(3.5);
            target.setVelocity(velocity);
        }
    }
}