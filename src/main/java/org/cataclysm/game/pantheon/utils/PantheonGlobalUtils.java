package org.cataclysm.game.pantheon.utils;

import net.kyori.adventure.key.Key;
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
import java.util.ArrayList;
import java.util.List;

public class PantheonGlobalUtils {
    public static List<Player> getRaidParticipants() {
        List<Player> participants = new ArrayList<>();
        for (Player player : Bukkit.getOnlinePlayers()) {
            GameMode gameMode = player.getGameMode();
            if (gameMode == GameMode.CREATIVE || gameMode == GameMode.SPECTATOR) continue;
            participants.add(player);
        }
        return participants;
    }
    public static boolean areAllReady() {
        int ready = getReadyCount();
        int size = getRaidParticipants().size();
        return ready == size;
    }
    public static int getReadyCount() {
        int count = 0;
        for (Player player : Bukkit.getOnlinePlayers()) if (isReady(player)) count++;
        return count;
    }
    public static void setReady(Player player, boolean ready) {
        PersistentData.set(player, "PANTHEON_READY", PersistentDataType.BOOLEAN, ready);
    }
    public static boolean isReady(Player player) {
        Boolean ready = PersistentData.get(player, "PANTHEON_READY", PersistentDataType.BOOLEAN);
        return ready != null && ready;
    }

    public static void teleport(Player player, Location location) {
        World world = location.getWorld();
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 10f, .75f);
        world.playSound(player, Sound.ITEM_TRIDENT_RETURN, 10f, .55f);
        player.playSound(net.kyori.adventure.sound.Sound.sound(Key.key("cataclysm.pantheon.teleport"), SoundCategory.MASTER, 10F, 0.95F));

        showWhitescreen(player, 1000, 3000, 1500);
        Bukkit.getScheduler().runTaskLater(Cataclysm.getInstance(), () -> player.teleport(location), 50L);
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
