package org.cataclysm.game.mob.listener.types;

import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.effect.MortemEffect;
import org.cataclysm.game.effect.PaleCorrosionEffect;
import org.cataclysm.game.mob.utils.EffectUtils;

@Registrable
public class PaleMobsListener implements Listener {

    @EventHandler
    public void onEvokerAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof EvokerFangs fangs)) return;
        if (fangs.getOwner() == null) return;

        String mobId = CataclysmMob.getID(fangs.getOwner());
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("PALE")) return;

        if (!(event.getEntity() instanceof Player player)) return;
        event.setCancelled(true);
        player.getLocation().createExplosion(fangs.getOwner(), 7, false, false);
        player.getWorld().strikeLightningEffect(player.getLocation());
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 200, 0));
        }, 1);
    }

    @EventHandler
    private void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof LivingEntity entity)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        String mobId = CataclysmMob.getID(entity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("PALE")) return;

        switch (entity.getType()) {
            case ENDERMAN, VEX -> {
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    player.addPotionEffect(new PotionEffect(PaleCorrosionEffect.EFFECT_TYPE, 200, 0));
                }, 1);
            }
        }
    }

    @EventHandler
    public void onHitFireball(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        var world = projectile.getWorld();
        if (!(projectile.getShooter() instanceof LivingEntity shooter)) return;

        String mobId = CataclysmMob.getID(shooter);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("PALE")) return;

        var hitLocation = projectile.getLocation();
        world.playSound(hitLocation, Sound.ENTITY_GENERIC_EXPLODE, 1, 1);
        world.spawnParticle(Particle.EXPLOSION_EMITTER, hitLocation, 1);
        projectile.remove();

        switch (shooter.getType()) {
            case BLAZE -> {
                hitLocation.createExplosion(shooter, 4.5F, false, false);
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    for (Player player : hitLocation.getNearbyPlayers(3)) {
                        EffectUtils.removePossitiveEffects(player);
                        player.addPotionEffect(new PotionEffect(PaleCorrosionEffect.EFFECT_TYPE, 200, 0));
                    }
                }, 1L);
            }

            case GHAST -> {
                Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
                    for (Player player : hitLocation.getNearbyPlayers(5)) {
                        player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 200, 0));
                    }
                }, 1L);
            }
        }
    }

    @EventHandler
    public void onEndermanDeath(EntityDeathEvent event) {
        var livingEntity = event.getEntity();
        if (!(livingEntity instanceof Enderman enderman)) return;
        String mobId = CataclysmMob.getID(enderman);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("PALE")) return;

        var tntEntity = (TNTPrimed) livingEntity.getWorld().spawnEntity(livingEntity.getLocation(), org.bukkit.entity.EntityType.TNT);
        tntEntity.setFuseTicks((int) (tntEntity.getFuseTicks() / 1.5f));
        tntEntity.setYield(7.5f);
        tntEntity.setSource(livingEntity);
    }

}
