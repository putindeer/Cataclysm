package org.cataclysm.game.mob.listener.types;

import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.projectiles.ProjectileSource;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;
import org.cataclysm.game.mob.utils.TeleportUtils;

import java.util.concurrent.ThreadLocalRandom;

@Registrable
public class MirageMobsListener implements Listener {

    @EventHandler
    private void onSculptureDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();

        String mobId = CataclysmMob.getID(entity);
        if (mobId == null) return;
        if (!mobId.equalsIgnoreCase("MirageSculpture") && !mobId.equalsIgnoreCase("ShulkerSculpture")) return;

        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        killer.getWorld().playSound(killer.getLocation(), Sound.ITEM_SHIELD_BREAK, 1, 0.75F);
        killer.getWorld().playSound(killer.getLocation(), Sound.ITEM_TRIDENT_THUNDER, 1, 0.75F);
        killer.clearActivePotionEffects();
    }

    @EventHandler
    private void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();

        if (!(shooter instanceof LivingEntity entity)) return;
        String mobId = CataclysmMob.getID(entity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("MIRAGE")) return;

        switch (entity.getType()) {
            case SKELETON, GHAST -> entity.teleport(projectile.getLocation());
        }
    }

    @EventHandler
    private void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (entity instanceof Player) return;
        String mobId = CataclysmMob.getID(entity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("MIRAGE")) return;

        if (entity.getType() == EntityType.ENDERMAN) {
            AttributeInstance attribute = entity.getAttribute(Attribute.SCALE);
            if (attribute == null) return;
            if (attribute.getValue() > 0.5) attribute.setBaseValue(attribute.getValue() - 0.25);
        }
    }

    @EventHandler
    public void onDamagedByArrow(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof LivingEntity entity)) return;
        if (!(event.getDamager() instanceof Projectile)) return;
        String mobId = CataclysmMob.getID(entity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("MIRAGE")) return;

        if (entity instanceof Ghast ghast) {
            var location = entity.getLocation();
            boolean foundTeleport = TeleportUtils.teleportEntityRandomly(ghast, 20);

            if (!foundTeleport) {
                var random = ThreadLocalRandom.current();
                var newLocation = location.add(random.nextInt(-10, 10), random.nextInt(-3, 3), random.nextInt(-10, 10));
                ghast.teleport(newLocation);
                ghast.setNoDamageTicks(5);
            }
        }
    }

    @EventHandler
    private void onEntityDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
        if (!(entity instanceof LivingEntity livingEntity)) return;
        String mobId = CataclysmMob.getID(livingEntity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("MIRAGE")) return;
        var cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
            var location = livingEntity.getLocation();
            boolean foundTeleport = TeleportUtils.teleportEntityRandomly(livingEntity, 20);
            if (!foundTeleport) {
                var random = ThreadLocalRandom.current();
                var newLocation = location.add(random.nextInt(-10, 10), random.nextInt(-3, 3), random.nextInt(-10, 10));
                livingEntity.teleport(newLocation);
                livingEntity.setNoDamageTicks(5);
            }
        }
    }

    @EventHandler
    private void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof Enderman) event.setCancelled(true);
    }

    @EventHandler
    public void onTarget(EntityTargetEvent event) {
        if (!(event.getEntity() instanceof LivingEntity livingEntity)) return;

        String mobId = CataclysmMob.getID(livingEntity);
        if (mobId == null) return;
        if (!mobId.toUpperCase().contains("MIRAGE")) return;
        if (!(event.getTarget() instanceof Player || event.getTarget() == null)) event.setCancelled(true);
    }

    @EventHandler
    public void onTargetWither(EntityTargetEvent event) {
        if (!event.getReason().equals(EntityTargetEvent.TargetReason.CLOSEST_ENTITY)) return;
        if (event.getEntity() instanceof Wither && event.getTarget() instanceof Player) event.setCancelled(true);
    }

}
