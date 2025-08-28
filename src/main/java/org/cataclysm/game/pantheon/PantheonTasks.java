package org.cataclysm.game.pantheon;

import org.bukkit.*;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.level.PantheonAreas;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PantheonTasks {
    public static void tickPlayerTask() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            for (var player : Bukkit.getOnlinePlayers()) applyGlowingEffect(player);
        });
    }

    public static void tickEntranceParticles() {
        if (Cataclysm.getPantheon().getPhase() != PantheonOfCataclysm.Phase.WAITING_FOR_PLAYERS) return;
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            Location location = PantheonAreas.PANTHEON_ENTRANCE.getCoreLocation();
            castEntranceEffects(location);
        });
    }

    private static void castEntranceEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        world.spawnParticle(Particle.SQUID_INK, location, 80, 0.35F, 3F, 0.35F, 0.01F, null, true);
        world.spawnParticle(Particle.SMOKE, location, 60, 0.25F, 3F, 0.25F, 0.01F, null, true);

        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 1.0F);
        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.5F, 0.85F);
    }

    private static void applyGlowingEffect(Player player) {
        if (!PantheonUtils.isReady(player)) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false));
    }
}
