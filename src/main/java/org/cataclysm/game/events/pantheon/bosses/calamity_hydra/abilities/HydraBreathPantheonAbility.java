package org.cataclysm.game.events.pantheon.bosses.calamity_hydra.abilities;

import org.bukkit.*;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.bosses.calamity_hydra.attacks.PantheonCharge;

import java.util.concurrent.TimeUnit;

public class HydraBreathPantheonAbility extends PantheonHydraAbility {
    private final PantheonHydra hydra;

    public HydraBreathPantheonAbility(PantheonHydra hydra) {
        super(Material.DRAGON_BREATH, "Hydra Breath", 2);
        this.hydra = hydra;
    }

    @Override
    public void channel() {
        Location location = this.hydra.getController().getLocation();
        World world = location.getWorld();

        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_GROWL, 4F, .8F);
        world.playSound(location, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 4F, .8F);
    }

    @Override
    public void cast() {
        PantheonCharge charge = new PantheonCharge(this.hydra, 12, 3, 1.5);
        Location location = this.hydra.getController().getLocation();
        World world = location.getWorld();

        for (int i = 0; i < this.hydra.getHeads(); i++) {
            this.hydra.getThread().getService().schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    world.playSound(location, Sound.ENTITY_ENDER_DRAGON_SHOOT, 2F, 1.28F);
                    world.playSound(location, Sound.ENTITY_ENDER_DRAGON_SHOOT, 4F, .8F);
                    world.playSound(location, Sound.ENTITY_ENDER_DRAGON_SHOOT, 1F, .6F);
                    charge.shoot(5);
                });
            }, i * 500L, TimeUnit.MILLISECONDS);
        }
    }
}
