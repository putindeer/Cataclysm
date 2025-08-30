package org.cataclysm.game.player.mechanics.upgrade;

import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionEffectTypeCategory;
import org.jetbrains.annotations.NotNull;

public record UpgradeDefinition(String key, int... levels) {

    public void addEffect(@NotNull Player player, int duration, int level) {
        PotionEffectType[] effectType = {null, null};
        var amplifier = this.levels[0];
        switch (this.key) {

            case "regeneration", "jump_boost", "speed", "strength", "dolphins_grace", "haste", "resistance", "hero_of_the_village", "fire_resistance", "conduit_power", "slow_falling", "invisibility" -> {
                effectType[0] = this.getPotionEffect(this.key);
            }

            case "health_blessing" -> {
                effectType[0] = PotionEffectType.HEALTH_BOOST;
                effectType[1] = PotionEffectType.INSTANT_HEALTH;
            }

            case "cleansing" -> {
                for (var effect : player.getActivePotionEffects()) {
                    if (!effect.getType().getCategory().equals(PotionEffectTypeCategory.HARMFUL)) continue;
                    player.removePotionEffect(effect.getType());
                }

                if (this.levels.length > 1) {
                    player.setFireTicks(0);
                    player.setFreezeTicks(0);
                    player.setRemainingAir(player.getMaximumAir());
                    player.setExhaustion(0);
                    player.setShieldBlockingDelay(0);
                    player.setNoDamageTicks(20);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 20 * 30, 0, false, false));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20 * 30, 10, false, false));
                }

                return;
            }
        }

        if (this.levels.length > 1) amplifier = this.levels[level - 1];
        if (effectType[0] != null) player.addPotionEffect(new PotionEffect(effectType[0], duration, amplifier));
        if (effectType[1] != null) player.addPotionEffect(new PotionEffect(effectType[1], 1, amplifier));
    }

    public PotionEffectType getPotionEffect(String effectName) {
        if (effectName == null) return null;

        NamespacedKey key = NamespacedKey.minecraft(effectName.toLowerCase());
        return Registry.POTION_EFFECT_TYPE.get(key);
    }
}


