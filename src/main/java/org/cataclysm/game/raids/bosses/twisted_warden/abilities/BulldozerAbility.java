package org.cataclysm.game.raids.bosses.twisted_warden.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.raids.bosses.twisted_warden.TwistedWarden;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BulldozerAbility extends Ability {
    private final TwistedWarden warden;
    private ScheduledFuture<?> future;

    public BulldozerAbility(TwistedWarden warden) {
        super(Material.MUSIC_DISC_5, "Bulldozer", 1, 15);
        this.warden = warden;
    }

    @Override
    public void channel() {
        var controller = this.warden.getController();
        var location = controller.getLocation();
        location.getWorld().playSound(location, Sound.ENTITY_WARDEN_ANGRY, 5F, 0.77F);
        this.throwEntities(5, -3.5);
    }

    @Override
    public void cast() {
        final var duration = 7;

        var controller = this.warden.getController();
        controller.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, duration*20, 3));

        var service = this.warden.getThread().getService();
        this.future = service.scheduleAtFixedRate(() -> this.summonExplosion(4, 20), 0, 250, TimeUnit.MILLISECONDS);
        service.schedule(this::stop, duration, TimeUnit.SECONDS);
    }

    private void stop() {
        this.future.cancel(true);
        this.future = null;
    }

    private void summonExplosion(int radius, float damage) {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            var world = location.getWorld();
            world.spawnParticle(Particle.EXPLOSION_EMITTER, location, 0, 0, 0, 0, 0, null, true);
            world.playSound(location, Sound.ENTITY_GENERIC_EXPLODE, 2F, 1.2F);

            var nearby = this.warden.getNearbyLivingEntities(location, radius);
            for (var livingEntity : nearby) livingEntity.damage(damage);
        });
    }

    private void throwEntities(double radius, double strength) {
        var controller = this.warden.getController();
        var location = controller.getLocation();

        var nearby = this.warden.getNearbyLivingEntities(location, radius);
        for (var target : nearby) {
            var targetLoc = target.getLocation();
            var direction = targetLoc.toVector().subtract(location.toVector()).normalize();
            var velocity = direction.multiply(-strength).setY(2.5);
            target.setVelocity(velocity);
        }
    }
}