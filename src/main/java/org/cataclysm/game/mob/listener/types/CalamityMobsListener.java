package org.cataclysm.game.mob.listener.types;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.EffectUtils;

@Registrable
public class CalamityMobsListener implements Listener {

    @EventHandler
    public void onHitFireball(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        var world = projectile.getWorld();
        if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;

        String mobId = CataclysmMob.getID(shooter);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("CALAMITY")) return;

        var hitLocation = projectile.getLocation();
        world.playSound(hitLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, hitLocation, 1);
        projectile.remove();

        switch (shooter.getType()) {
            case BLAZE -> {
                hitLocation.createExplosion(shooter, 4.5F, false, false);
                for (Player player : hitLocation.getNearbyPlayers(3)) EffectUtils.removePossitiveEffects(player);
            }

            case GHAST -> hitLocation.createExplosion(shooter, 3.0F, false, false);
        }
    }

    @EventHandler
    public void onEndermanDeath(EntityDeathEvent event) {
        var livingEntity = event.getEntity();
        if (!(livingEntity instanceof Enderman enderman)) return;
        String mobId = CataclysmMob.getID(enderman);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("CALAMITY")) return;

        var tntEntity = (TNTPrimed) livingEntity.getWorld().spawnEntity(livingEntity.getLocation(), org.bukkit.entity.EntityType.TNT);
        tntEntity.setYield(6.0f);
        tntEntity.setSource(livingEntity);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        String mobId = CataclysmMob.getID(livingEntity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("CALAMITY")) return;

        if (!(event.getTarget() instanceof Player || event.getTarget() == null)) event.setCancelled(true);
    }

}
