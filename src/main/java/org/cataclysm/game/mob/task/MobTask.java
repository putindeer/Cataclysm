package org.cataclysm.game.mob.task;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Evoker;
import org.bukkit.entity.Monster;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.mob.custom.dungeon.temple.Enchanter;
import org.cataclysm.game.mob.utils.MobUtils;

public class MobTask {
    public void startTickTask(int tick) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Cataclysm.getInstance(), () -> {
            World world = Bukkit.getWorld("world");
            if (world == null) return;

            for (var livingEntity : world.getLivingEntities()) {
                // Enchanter's Cloud Boost
                if (livingEntity.getActivePotionEffects().isEmpty() && MobUtils.isEntityInCloudWithColor(livingEntity, Enchanter.BOOST_RADIUS, Enchanter.BOOST_CLOUD_COLOR)) {
                    if (!(livingEntity instanceof Monster) || livingEntity instanceof Evoker) continue;

                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, PotionEffect.INFINITE_DURATION, 1, true, true));
                    livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 0, true, true));

                    var scale = livingEntity.getAttribute(Attribute.SCALE);
                    if (scale != null) scale.setBaseValue(scale.getBaseValue() * 1.2);

                    livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.15F, 0.5F);
                    livingEntity.getWorld().playSound(livingEntity.getLocation(), Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1.15F, 0.75F);
                }
            }
        }, 0, tick);
    }

}
