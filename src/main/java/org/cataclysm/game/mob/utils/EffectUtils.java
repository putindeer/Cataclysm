package org.cataclysm.game.mob.utils;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class EffectUtils {
    private static final Set<PotionEffectType> POSITIVE_EFFECTS = Set.of(
            PotionEffectType.ABSORPTION,
            PotionEffectType.CONDUIT_POWER,
            PotionEffectType.DOLPHINS_GRACE,
            PotionEffectType.FIRE_RESISTANCE,
            PotionEffectType.HERO_OF_THE_VILLAGE,
            PotionEffectType.INVISIBILITY,
            PotionEffectType.JUMP_BOOST,
            PotionEffectType.NIGHT_VISION,
            PotionEffectType.REGENERATION,
            PotionEffectType.RESISTANCE,
            PotionEffectType.SATURATION,
            PotionEffectType.SLOW_FALLING,
            PotionEffectType.SPEED,
            PotionEffectType.STRENGTH,
            PotionEffectType.WATER_BREATHING
    );

    public static void removePossitiveEffects(@NotNull LivingEntity livingEntity) {
        Location location = livingEntity.getLocation();
        World world = livingEntity.getLocation().getWorld();

        for (final PotionEffect effect : livingEntity.getActivePotionEffects()) {
            if (POSITIVE_EFFECTS.contains(effect.getType())) {
                world.playSound(location, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 2.9F, 1.5F);
                world.playSound(location, Sound.ENTITY_ILLUSIONER_CAST_SPELL, 2.9F, 0.7F);
                livingEntity.removePotionEffect(effect.getType());
            }
        }
    }
}
