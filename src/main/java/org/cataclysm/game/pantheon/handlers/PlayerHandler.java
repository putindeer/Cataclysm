package org.cataclysm.game.pantheon.handlers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.api.data.PersistentData;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PlayerHandler {
    public static void setReady(Player player, boolean ready) {
        PersistentData.set(player, "PANTHEON_READY", PersistentDataType.BOOLEAN, ready);
    }

    public static boolean isReady(Player player) {
        return Boolean.TRUE.equals(PersistentData.get(player, "PANTHEON_READY", PersistentDataType.BOOLEAN));
    }

    public static void teleport(@NotNull Player player, Location location) {
        playTeleportSound(player);
        showWhitescreen(player, 1000, 6000, 1500);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.teleport(location), 6000 / 50);
    }


    private static void playTeleportSound(Player player) {
        World world = player.getWorld();
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 5f, .75f);
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 5f, .55f);
        world.playSound(player, "cataclysm.pantheon.teleport", 10F, 0.985F);
    }

    private static void showWhitescreen(@NotNull Player player, int fadeIn, int stay, int fadeOut) {
        int duration = fadeIn + stay + fadeOut;
        player.showTitle(buildTitle("\uE101", "", fadeIn, stay, fadeOut));
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, (duration/50), 0, false, false, false));
    }

    private static Title buildTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        return Title.title(
                MiniMessage.miniMessage().deserialize(title),
                MiniMessage.miniMessage().deserialize(subtitle),
                Title.Times.times(Duration.ofMillis(fadeIn), Duration.ofMillis(stay), Duration.ofMillis(fadeOut)));
    }
}
