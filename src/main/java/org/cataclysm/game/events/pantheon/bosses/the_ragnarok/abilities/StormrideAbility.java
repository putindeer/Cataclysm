package org.cataclysm.game.events.pantheon.bosses.the_ragnarok.abilities;

import org.bukkit.*;
import org.bukkit.entity.LivingEntity;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.bosses.PantheonAbility;
import org.cataclysm.game.events.pantheon.bosses.the_ragnarok.TheRagnarok;

import java.util.Random;

public class StormrideAbility extends PantheonAbility {
    private final TheRagnarok ragnarok;
    private static final Random random = new Random();

    public StormrideAbility(TheRagnarok ragnarok) {
        super(Material.LIGHTNING_ROD, "Stormride", 3);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {
    }

    @Override
    public void cast() {
        Location center = ragnarok.getArena().center();
        World world = center.getWorld();

        for (int i = 0; i < 20; i++) {
            Location strike = center.clone().add(random.nextInt(60) - 30, 0, random.nextInt(60) - 30);
            world.strikeLightningEffect(strike);

            if (random.nextDouble() < 0.3) {
                world.createExplosion(strike, 8F, true, true);
            }

            if (random.nextDouble() < 0.1) {
                world.spawnParticle(Particle.PORTAL, strike, 300, 1, 1, 1, 0.1);
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    for (LivingEntity living : ragnarok.getNearbyLivingEntities(strike, 6)) {
                        living.damage(50);
                    }
                });
            }
        }
    }
}
