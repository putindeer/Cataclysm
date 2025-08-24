package org.cataclysm.game.effect;

import net.minecraft.world.entity.decoration.ArmorStand;
import org.bukkit.GameMode;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.listener.registrable.Registrable;

@Registrable
public class ImmunityEffect implements Listener {
    public static final PotionEffectType EFFECT_TYPE = PotionEffectType.LUCK;

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Player || entity instanceof ArmorStand) {
            var livingEntity = (LivingEntity) entity;
            if (livingEntity.hasPotionEffect(EFFECT_TYPE)) event.setCancelled(true);

            if (livingEntity instanceof Player player && player.getGameMode() == GameMode.SPECTATOR) event.setCancelled(true);
        }
    }
}
