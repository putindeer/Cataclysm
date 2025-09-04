package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities;

import org.bukkit.*;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.attacks.PantheonExplosion;

import java.util.concurrent.TimeUnit;

public class AtomicBreathPantheonAbility extends PantheonAbility {
    private final PantheonHydra hydra;

    public AtomicBreathPantheonAbility(PantheonHydra hydra) {
        super(Material.BLAZE_POWDER, "Atomic Breath", 3);
        this.hydra = hydra;
    }

    @Override
    public void channel() {
    }

    @Override
    public void cast() {
        double maxDistance = 80.0;
        double step = .2;
        double power = 6;
        for (int i = 0; i < 8; i++) {
            this.hydra.getPantheon().getService().schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    Location loc = hydra.getController().getEyeLocation();
                    Vector dir = loc.getDirection().normalize();
                    this.shoot(loc, dir, maxDistance, step, power);
                });
            }, 200 * i, TimeUnit.MILLISECONDS);
        }
    }

    private void shoot(Location loc, Vector dir, double maxDistance, double step, double power) {
        Location location = this.hydra.getController().getLocation();
        World world = location.getWorld();

        world.playSound(location, Sound.ITEM_TRIDENT_RETURN, 4F, .6F);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_SHOOT, 4F, .5F);
        world.playSound(location, Sound.BLOCK_END_PORTAL_FRAME_FILL, 4F, .7F);

        for (double d = 2; d < maxDistance; d += step) {
            Location point = loc.clone().add(dir.clone().multiply(d));
            Material type = point.getBlock().getType();
            this.hydra.getThread().getService().schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    if (type.isSolid()) new PantheonExplosion(this.hydra).create(point, power, PantheonExplosion.Type.MAGIC);
                    loc.getWorld().spawnParticle(Particle.FLAME, point, 1, 0.1, 0.1, 0.1, 0);
                });
            }, (long) ((d - 1) * 30), TimeUnit.MILLISECONDS);
            if (type.isSolid()) break;
        }
    }
}
