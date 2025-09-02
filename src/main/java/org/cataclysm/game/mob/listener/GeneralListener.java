package org.cataclysm.game.mob.listener;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.world.Dimensions;

import java.util.SplittableRandom;

@Registrable
public class GeneralListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onSpawn(CreatureSpawnEvent event) {
        World world = Dimensions.PALE_VOID.createWorld();
        if (world == null) return;

        Location spawnLocation = world.getSpawnLocation();
        Location mobLocation = event.getLocation();

        if (spawnLocation.getWorld().equals(mobLocation.getWorld())) {
            double distance = spawnLocation.distance(mobLocation);
            if (distance <= 300) event.getEntity().remove();
        }
    }

    @EventHandler
    public void projectileLaunch(ProjectileLaunchEvent event) {
        Projectile entity = event.getEntity();
        switch (entity.getType()) {
            case SPLASH_POTION -> {
                if (Cataclysm.getDay() < 14) return;
                if (entity.getShooter() instanceof Witch) {
                    SplashPotion potion = (SplashPotion) entity;
                    ItemStack potionItem = potion.getItem();
                    PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();
                    if (potionMeta == null) return;
                    for (PotionEffect effect : potionMeta.getAllEffects()) {
                        potionMeta.removeCustomEffect(effect.getType());
                        if (Cataclysm.getDay() < 21) potionMeta.addCustomEffect(new PotionEffect(effect.getType(), effect.getDuration() * 2, effect.getAmplifier() == 0 ? 1 : effect.getAmplifier() * 2, effect.isAmbient(), effect.hasParticles(), effect.hasIcon()), true);
                        else {
                            PotionEffect[] effects = {
                                    new PotionEffect(PotionEffectType.BLINDNESS, 30 * 20, 0, effect.isAmbient(), effect.hasParticles(), effect.hasIcon()),
                                    new PotionEffect(PotionEffectType.HUNGER, 20 * 20, 19, effect.isAmbient(), effect.hasParticles(), effect.hasIcon()),
                                    new PotionEffect(PotionEffectType.WITHER, 60 * 20, 2, effect.isAmbient(), effect.hasParticles(), effect.hasIcon()),
                                    new PotionEffect(PotionEffectType.INSTANT_DAMAGE, 1, 99, effect.isAmbient(), effect.hasParticles(), effect.hasIcon())
                            };
                            var randomEffect = effects[new SplittableRandom().nextInt(effects.length)];
                            potionMeta.addCustomEffect(randomEffect, true);
                        }
                    }
                    potionItem.setItemMeta(potionMeta);
                    potion.setItem(potionItem);
                }
            }
        }
    }


    @EventHandler
    public void projectileHit(ProjectileHitEvent event) {
        Projectile entity = event.getEntity();
        switch (entity.getType()) {
            case TRIDENT -> {
                if (Cataclysm.getDay() < 21) return;
                if (entity.getShooter() instanceof Drowned) entity.getLocation().createExplosion(entity, 4, false, false);
            }
        }
    }
}
