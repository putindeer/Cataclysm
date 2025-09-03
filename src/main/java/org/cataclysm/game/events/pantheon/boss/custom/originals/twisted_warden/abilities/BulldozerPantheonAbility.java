package org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.abilities;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.twisted_warden.PantheonWarden;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class BulldozerPantheonAbility extends PantheonAbility {
    private final PantheonWarden warden;
    private ScheduledFuture<?> future;

    public BulldozerPantheonAbility(PantheonWarden warden) {
        super(Material.MUSIC_DISC_5, "Bulldozer", 1);
        this.warden = warden;
    }

    @Override
    public void channel() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20, 0));
            player.playSound(player, Sound.ENTITY_WARDEN_ROAR, 2F, 0.5F);
            player.playSound(player, Sound.ENTITY_WARDEN_ANGRY, 2F, 0.77F);
        }
        this.throwEntities(5, -4.5);
    }

    @Override
    public void cast() {
        ScheduledExecutorService service = this.warden.getThread().getService();

        this.future = service.scheduleAtFixedRate(() -> this.summonExplosion(5, 45), 0, 100, TimeUnit.MILLISECONDS);
        this.warden.getController().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 4));

        service.schedule(this::stop, 10, TimeUnit.SECONDS);
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
            if (target instanceof Player player) player.playSound(player, Sound.ITEM_TRIDENT_RIPTIDE_3, 3F, 2);
        }
    }
}