package org.cataclysm.game.pantheon;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PantheonUtils {
    public static void setReady(Player player, boolean ready) {
        PersistentData.set(player, "PANTHEON_READY", PersistentDataType.BOOLEAN, ready);
    }
    public static boolean isReady(Player player) {
        Boolean ready = PersistentData.get(player, "PANTHEON_READY", PersistentDataType.BOOLEAN);
        return ready != null && ready;
    }

    public static void teleport(Player player, Location location) {
        showWhitescreen(player, 1000, 3000, 1500);

        World world = location.getWorld();
        world.playSound(player, Sound.BLOCK_BEACON_ACTIVATE, 1f, .75f);
        world.playSound(player, "cataclysm.pantheon.teleport", 2f, .95f);

        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.teleport(location), 40L);
    }

    //This is in MILLISECONDS.
    private static void showWhitescreen(@NotNull Player player, int fadeIn, int stay, int fadeOut) {
        player.showTitle(Title.title(
                MiniMessage.miniMessage().deserialize("\uE101"),
                MiniMessage.miniMessage().deserialize(""),
                Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut))));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ((fadeIn + stay + fadeOut) / 1000) * 20, 0, false, false, false));
    }
}
