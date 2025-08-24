package org.cataclysm.game.mob.listener;

import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.api.mob.CataclysmMob;

@Registrable
public class DamageListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
        var cause = event.getCause();
        if (entity instanceof Player) return;
        if (entity instanceof Item) return;
        if (!(entity instanceof LivingEntity)) return;
        if (cause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
        || cause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) event.setCancelled(true);
    }

    @EventHandler
    public void onSuffocateRemove(EntityDamageEvent event) {
        var entity = event.getEntity();
        var cause = event.getCause();

        if (entity instanceof Enemy || entity instanceof Mob) {
            if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
                if (entity.getTicksLived() < 120) entity.remove();
            }
        }
    }

    @EventHandler
    public void entityDamagePlayer(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        Entity damager = event.getDamager();
        int day = Cataclysm.getDay();

        //? Vanilla Entities
        switch (damager.getType()) {

            case WITHER_SKULL -> {
                WitherSkull witherSkull = (WitherSkull) damager;
                if (witherSkull.getShooter() != null && witherSkull.getShooter() instanceof Wither wither) {
                    String mobId = CataclysmMob.getID(wither);
                    if (mobId == null) return;
                    if (mobId.contains("Wandering")) {
                        event.setDamage(event.getDamage() * 5);
                    }
                }
            }

            case GOAT -> {
                if (day >= 7) {
                    player.knockback(200, damager.getLocation().getDirection().multiply(-1).getX(), damager.getLocation().getDirection().multiply(-1).getZ());
                }

                if (day >= 14) {
                    if (!player.isBlocking()) player.setFreezeTicks(player.getFreezeTicks() + 20 * 10);
                }
            }

            case LLAMA_SPIT -> {
                if (day >= 7) {
                    if (((LlamaSpit) damager).getShooter() instanceof Llama llama) {
                        event.setCancelled(true);
                        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.damage(999, llama), 1L);
                    }
                }
            }

            case TRIDENT -> {
                if (day >= 14) event.setDamage(event.getDamage() * 1.75);
            }

            case FIREWORK_ROCKET -> {
                if (day >= 14) event.setDamage(event.getDamage() * 5);
            }

            case POLAR_BEAR, BREEZE_WIND_CHARGE -> {
                if (day >= 14) {
                    if (!player.isBlocking()) player.setFreezeTicks(player.getFreezeTicks() + 20 * 10);
                }
            }

            case SNOWBALL -> {
                if (day >= 14) {
                    if (damager instanceof Snowball snowball && snowball.getShooter() instanceof Snowman snowGolem) {
                        if (!player.isBlocking()){
                            player.damage(day < 21 ? 30D : 999D, snowGolem);
                            if (!player.isBlocking()) player.setFreezeTicks(player.getFreezeTicks() + 20 * 10);
                        }
                    }
                }
            }

            case ARROW -> {
                if (day >= 14) {
                    if (damager instanceof Arrow arrow && arrow.getShooter() instanceof Stray) {
                        if (!player.isBlocking()) player.setFreezeTicks(player.getFreezeTicks() + 20 * 10);
                    }
                }
            }

            case GUARDIAN -> {
                if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) return;
                if (day >= 14) event.setDamage(event.getDamage() * 3);
                if (day >= 21) event.setDamage(999);
            }
            case ELDER_GUARDIAN -> {
                if (event.getCause() == EntityDamageEvent.DamageCause.THORNS) return;
                if (day >= 14) event.setDamage(999);
            }
            case HOGLIN -> {
                if (day >= 21) {
                    player.knockback(200, damager.getLocation().getDirection().multiply(-1).getX(), damager.getLocation().getDirection().multiply(-1).getZ());
                }
            }
            case HUSK -> {
                if (day >= 21) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, 30 * 20, 9));
                }
            }
        }
    }
}
