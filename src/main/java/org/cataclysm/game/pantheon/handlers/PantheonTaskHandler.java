package org.cataclysm.game.pantheon.handlers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.pantheon.world.PantheonLocations;
import org.cataclysm.global.utils.text.font.TinyCaps;

public class PantheonTaskHandler {
    public static void tickPlayerTask() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
            for (var player : Bukkit.getOnlinePlayers()) {
                applyReadyEffects(player);
            }
        });
    }
    public static void tickEntrance() {
        Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> castEntranceEffects(PantheonLocations.PANTHEON_ENTRANCE.getCoreLocation()));
    }

    private static void castEntranceEffects(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        world.spawnParticle(Particle.SQUID_INK, location, 80, 0.15F, 2.5F, 0.15F, 0.01F, null, true);
        world.spawnParticle(Particle.SMOKE, location, 90, 0.35F, 2.5F, 0.35F, 0.01F, null, true);

        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.5F, 0.5F);
    }
    private static void applyReadyEffects(Player player) {
        if (!PantheonPlayerHandler.isReady(player)) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false));

        String display = TinyCaps.tinyCaps(getActionBarDisplay());
        player.sendActionBar(MiniMessage.miniMessage().deserialize("<gradient:#FC8C03:#B5A16B>" + display + "</gradient>"));
    }
    private static String getActionBarDisplay() {
        int ready = PantheonPlayerHandler.getReadyCount();
        int size = PantheonPlayerHandler.getParticipants().size();
        return  "estás listo para entrar al panteón [" + ready + "/" + size + "]";
    }
}
