package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities;

import org.bukkit.*;
import org.bukkit.util.Vector;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.attacks.CalamityExplosion;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.rage.RagePantheonAbility;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AtomicBreathPantheonAbility extends RagePantheonAbility {
    public AtomicBreathPantheonAbility(PantheonHydra hydra) {
        super(hydra, Material.BLAZE_POWDER, "Atomic Breath", 10);
    }

    @Override
    public void tick() {
        double maxDistance = 80.0;
        double step = .2;
        double power = 6;

        ScheduledExecutorService service = getHydra().getThread().getService();
        service.schedule(() -> {
            for (int i = 0; i < 8; i++) {
                service.schedule(() -> {
                    Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                        Location loc = hydra.getController().getEyeLocation();
                        Vector dir = loc.getDirection().normalize();
                        this.shoot(loc, dir, maxDistance, step, power);
                    });
                }, 200 * i, TimeUnit.MILLISECONDS);
            }
        }, 3, TimeUnit.SECONDS);
    }

    private void shoot(Location loc, Vector dir, double maxDistance, double step, double power) {
        hydra.playSound(Sound.ITEM_TRIDENT_RETURN, 4F, .6F);
        hydra.playSound(Sound.ENTITY_ENDER_DRAGON_SHOOT, 4F, .5F);
        hydra.playSound(Sound.BLOCK_END_PORTAL_FRAME_FILL, 4F, .7F);
        for (double d = 2; d < maxDistance; d += step) {
            Location point = loc.clone().add(dir.clone().multiply(d));
            Material type = point.getBlock().getType();
            hydra.getThread().getService().schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    if (type.isSolid()) hydra.createHydraExplosion(point, power, CalamityExplosion.Type.MAGIC);
                    loc.getWorld().spawnParticle(Particle.FLAME, point, 1, 0.1, 0.1, 0.1, 0);
                });
            }, (long) ((d - 1) * 30), TimeUnit.MILLISECONDS);
            if (type.isSolid()) break;
        }
    }
}
