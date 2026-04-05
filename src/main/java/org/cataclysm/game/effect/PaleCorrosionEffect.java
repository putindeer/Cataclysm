package org.cataclysm.game.effect;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.api.listener.registrable.Registrable;
import org.cataclysm.game.items.ItemFamily;
import org.cataclysm.game.player.PlayerUtils;

@Registrable
public class PaleCorrosionEffect implements Listener {
    public static final PotionEffectType EFFECT_TYPE = PotionEffectType.WEAVING;

    @EventHandler
    private void onPlayerDamage(EntityDamageEvent event) {
        var entity = event.getEntity();
        if (entity instanceof Player player) {
            if (PlayerUtils.hasArmor(ItemFamily.PALE_ARMOR, player)) return;
            if (PlayerUtils.hasMirageHelmet(player)) return;
            if (player.hasPotionEffect(EFFECT_TYPE)) {
                var corrosionDebuff = PersistentData.get(player, "PALE_CORROSION_HEALTH_DEBUFF", PersistentDataType.DOUBLE);
                if (corrosionDebuff != null) PersistentData.set(player, "PALE_CORROSION_HEALTH_DEBUFF", PersistentDataType.DOUBLE, corrosionDebuff + 2.0);
            }
        }
    }

    @EventHandler
    public void playerPotionEffect(EntityPotionEffectEvent event) {
        if (Cataclysm.getDay() < 21) return;
        PotionEffect newEffect = event.getNewEffect();
        if (newEffect == null) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (PlayerUtils.hasArmor(ItemFamily.PALE_ARMOR, player)) return;
        if (PlayerUtils.hasMirageHelmet(player)) return;
        if (newEffect.getType().equals(EFFECT_TYPE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, newEffect.getDuration(), 0));
        }
    }
}
