package org.cataclysm.game.mob.listener.types;

import net.kyori.adventure.key.Key;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;

@Registrable
public class TwistedMobsListener implements Listener {

    @EventHandler
    public void onHitFireball(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        var world = projectile.getWorld();
        if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;

        String mobId = CataclysmMob.getID(shooter);
        if (mobId == null) return;
        if (!mobId.contains("Twisted")) return;

        switch (shooter.getType()) {
            case BLAZE -> {
                var hitLocation = projectile.getLocation();
                world.playSound(hitLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
                world.spawnParticle(Particle.EXPLOSION_EMITTER, hitLocation, 1);
                projectile.remove();
                hitLocation.createExplosion(shooter, 4.5F, false, false);
            }
        }
    }

    @EventHandler
    public void onMeleeAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity damager)) return;
        if (!(event.getEntity() instanceof Player player)) return;
        String mobId = CataclysmMob.getID(damager);
        if (mobId == null) return;
        if (!mobId.contains("Twisted")) return;

        switch (damager.getType()) {
            case ENDERMAN -> {
                player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key("entity.enderman.scream"), net.kyori.adventure.sound.Sound.Source.HOSTILE, 1.0F, 0.77F));
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 30, 0));
            }

            case SPIDER -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 15, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 15, 1));
            }

            case ZOMBIE -> player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 15, 1));

        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getHitEntity() instanceof Player player)) return;
        if (!(event.getEntity().getShooter() instanceof LivingEntity shooter)) return;
        String mobId = CataclysmMob.getID(shooter);
        if (mobId == null) return;
        if (!mobId.contains("Twisted")) return;
        player.setVelocity(shooter.getLocation().subtract(player.getLocation()).toVector());
    }

}
