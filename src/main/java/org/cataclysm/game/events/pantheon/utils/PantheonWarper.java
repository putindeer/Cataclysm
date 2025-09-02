package org.cataclysm.game.events.pantheon.utils;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.effect.ImmunityEffect;
import org.cataclysm.game.events.pantheon.PantheonZones;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public class PantheonWarper {
    /**
     * Applies slow falling and immunity effects to all online players,
     * then teleports them to the specified Pantheon zone location.
     *
     * @param zone the PantheonZones destination to warp players to
     */
    public static void warp(PantheonZones zone) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 100, 0, false, false));
            player.addPotionEffect(new PotionEffect(ImmunityEffect.EFFECT_TYPE, 100, 0, false, false));
            teleport(player, zone);
        }
    }

    /**
     * Teleports the given player to the specified location with audiovisual effects.
     * <p>
     * The method plays a teleport sound, shows a whitescreen effect with blindness,
     * and then teleports the player after a delay matching the whitescreen duration.
     *
     * @param player   the player to teleport (must not be null)
     * @param zone the PantheonZone destination
     */
    public static void teleport(@NotNull Player player, PantheonZones zone) {
        playTeleportSound(player);
        showWhitescreen(player, 1000, 4500, 1500);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () ->
                player.teleport(zone.getLocation().clone().add(0, 10, 0)), 4500 / 50);
    }

    private static void playTeleportSound(Player player) {
        World world = player.getWorld();
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 1f, .75f);
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 1f, .55f);
        world.playSound(player, "cataclysm.pantheon.teleport", .85F, 0.985F);
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
