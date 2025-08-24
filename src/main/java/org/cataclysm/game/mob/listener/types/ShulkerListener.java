package org.cataclysm.game.mob.listener.types;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.effect.DisperEffect;
import org.cataclysm.game.effect.MortemEffect;

@Registrable
public class ShulkerListener implements Listener {

    @EventHandler
    public void onBulletHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof ShulkerBullet bullet && bullet.getShooter() instanceof Shulker shulker)) return;
        var color = shulker.getColor();
        if (color == null) return;
        Location location = bullet.getLocation();

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> {
            switch (color) {
                case YELLOW -> location.createExplosion(shulker, 3f);
                case ORANGE -> location.createExplosion(shulker, 5f);
                case RED -> location.createExplosion(shulker, 7f);
            }
        }, 1L);
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (Cataclysm.getDay() < 21) return;
        if (!(event.getDamager() instanceof ShulkerBullet bullet && bullet.getShooter() instanceof Shulker shulker)) return;
        if (!(event.getEntity() instanceof Player player)) return;
        var color = shulker.getColor();
        if (color == null) return;

        switch (color) {
            case PURPLE -> {
                event.setDamage(event.getDamage() * 3);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 0));
            }
            case MAGENTA -> {
                event.setDamage(event.getDamage() * 5);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 1));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 0));
            }
            case PINK -> {
                event.setDamage(event.getDamage() * 7);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 10, 2));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 20 * 10, 1));
            }
            case GRAY -> {
                event.setDamage(event.getDamage() * 3);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 0));
            }
            case BLACK -> {
                event.setDamage(event.getDamage() * 3);
                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 10, 0));
            }
            case GREEN -> {
                event.setDamage(event.getDamage() * 10);
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 5, 1));
            }
            case LIME -> {
                event.setDamage(event.getDamage() * 15);
                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20 * 10, 1));
            }
            case BROWN -> {
                event.setDamage(event.getDamage() * 3);
                player.addPotionEffect(new PotionEffect(DisperEffect.EFFECT_TYPE, 20 * 10, 0));
            }
            case BLUE -> {
                event.setDamage(event.getDamage() * 3);
                if (!player.isBlocking()) player.setFreezeTicks(player.getFreezeTicks() + 20 * 4);
            }

            case CYAN -> {
                event.setDamage(event.getDamage() * 3);
                if (!player.isBlocking()) player.setFreezeTicks(player.getFreezeTicks() + 20 * 5);

            }
            case LIGHT_BLUE -> {
                event.setDamage(event.getDamage() * 3);
                if (!player.isBlocking()) player.setFreezeTicks(player.getFreezeTicks() + 20 * 6);

            }
            case LIGHT_GRAY -> {
                event.setDamage(event.getDamage() * 3);
                player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 20 * 5, 0));
            }
            case WHITE -> {
                player.addPotionEffect(new PotionEffect(MortemEffect.EFFECT_TYPE, 20 * 10, 0));
            }
        }
    }
}
