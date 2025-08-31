package org.cataclysm.game.pantheon.bosses.twisted_warden;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.api.data.PersistentData;
import org.cataclysm.game.pantheon.bosses.twisted_warden.keys.TwistedWardenKeys;
import org.jetbrains.annotations.NotNull;

public class TwistedWardenUtils {

    public static void setNightmare(@NotNull Player player, boolean nightmare) {
        if (nightmare) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DARKNESS, 160, 0));
            player.playSound(player, Sound.ENTITY_SKELETON_HORSE_DEATH, 1.2F, 0.75F);
        }
        PersistentData.set(player, TwistedWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN, nightmare);
    }

    public static boolean hasNightmare(@NotNull Player player) {
        return Boolean.TRUE.equals(PersistentData.get(player, TwistedWardenKeys.NIGHTMARE_KEY.getKey(), PersistentDataType.BOOLEAN));
    }

}