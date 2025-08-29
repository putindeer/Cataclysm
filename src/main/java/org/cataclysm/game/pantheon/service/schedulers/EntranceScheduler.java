package org.cataclysm.game.pantheon.service.schedulers;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.cataclysm.game.pantheon.level.PantheonLevels;
import org.cataclysm.game.pantheon.service.PantheonScheduler;
import org.cataclysm.game.pantheon.service.PantheonService;
import org.cataclysm.game.pantheon.utils.PlayerStatusHandler;

import java.util.concurrent.TimeUnit;

public class EntranceScheduler extends PantheonScheduler {
    public EntranceScheduler(PantheonService service) {
        super(service);
    }

    @Override
    public void registerTasks() {
        super.getTasks().put("VISUALS", this::castVisuals);
        super.getTasks().put("EFFECTS", this::castStatusEffect);
    }

    private void castStatusEffect() {
        for (Player player : PlayerStatusHandler.getPlayers(PlayerStatusHandler.Status.PREPARED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false));
        }
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