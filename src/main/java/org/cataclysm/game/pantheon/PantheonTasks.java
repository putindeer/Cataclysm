package org.cataclysm.game.pantheon;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PantheonTasks {
    public static void tickPlayerTask() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                applyGlowingEffect(player);
            }
        });
    }

    private static void applyGlowingEffect(Player player) {
        if (!PantheonUtils.isReady(player)) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false));
    }
}
