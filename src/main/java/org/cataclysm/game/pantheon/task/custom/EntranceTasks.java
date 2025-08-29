package org.cataclysm.game.pantheon.task.custom;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.handlers.PlayerHandler;
import org.cataclysm.game.pantheon.level.PantheonLevels;
import org.cataclysm.game.pantheon.task.PantheonScheduler;
import org.cataclysm.global.utils.text.font.TinyCaps;

import java.util.concurrent.TimeUnit;

public class EntranceTasks extends PantheonScheduler {
    public EntranceTasks(PantheonOfCataclysm pantheon) {
        super(pantheon);
    }

    public void registerTasks() {
        schRepeatingTask("VISUALS", this::castVisuals, 350, TimeUnit.MILLISECONDS);
    }

    private void castStatusEffect() {
        if (!PlayerHandler.isReady(player)) return;
        player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false));

        String display = TinyCaps.tinyCaps(getActionBarDisplay());
        player.sendActionBar(MiniMessage.miniMessage().deserialize("<gradient:#FC8C03:#B5A16B>" + display + "</gradient>"));
    }

    private void castVisuals() {
        Location location = PantheonLevels.PANTHEON_ENTRANCE.getCoreLocation();

        World world = location.getWorld();
        if (world == null) return;

        world.spawnParticle(Particle.SQUID_INK, location, 80, 0.15F, 2.5F, 0.15F, 0.01F, null, true);
        world.spawnParticle(Particle.SMOKE, location, 90, 0.35F, 2.5F, 0.35F, 0.01F, null, true);

        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.5F, 0.5F);
    }
}