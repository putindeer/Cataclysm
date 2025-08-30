package org.cataclysm.game.pantheon.level.levels.entrance;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.cataclysm.game.pantheon.PantheonOfCataclysm;
import org.cataclysm.game.pantheon.level.levels.PantheonThread;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class EntranceThread extends PantheonThread {
    private PantheonEntrance entrance;

    public EntranceThread(PantheonOfCataclysm pantheon) {
        super(pantheon);
    }

    @Override
    public void handle() {
        futures.addAll(List.of(
                pantheon.scheduleLoop(this::castStartCount, 1, TimeUnit.SECONDS),
                pantheon.scheduleLoop(this::castVisuals, 500, TimeUnit.MILLISECONDS)
        ));
    }

    private void castStartCount() {
        if (this.entrance == null) this.entrance = (PantheonEntrance) pantheon.getLevel();
        entrance.getStartCount().tick();
    }

    private void castVisuals() {
        Location location = super.pantheon.getLevel().location();
        World world = location.getWorld();

        if (world == null) return;

        world.spawnParticle(Particle.SQUID_INK, location, 80, 0.15F, 2.5F, 0.15F, 0.01F, null, true);
        world.spawnParticle(Particle.SMOKE, location, 90, 0.35F, 2.5F, 0.35F, 0.01F, null, true);

        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_BEACON_AMBIENT, 3.25F, 0.5F);
        world.playSound(location, Sound.BLOCK_PORTAL_AMBIENT, 0.5F, 0.5F);
    }
}