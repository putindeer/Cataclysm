package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.abilities;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.cataclysm.Cataclysm;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.attacks.CalamityCharge;

import java.util.concurrent.TimeUnit;

public class HydraBreathPantheonAbility extends PantheonAbility {
    private final PantheonHydra hydra;

    public HydraBreathPantheonAbility(PantheonHydra hydra) {
        super(Material.DRAGON_BREATH, "Hydra Breath", 2);
        this.hydra = hydra;
    }

    @Override
    public void channel() {
        this.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_GROWL, 4F, .8F);
        this.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_AMBIENT, 4F, .8F);
    }

    @Override
    public void cast() {
        for (int i = 0; i < this.hydra.heads; i++) {
            this.hydra.getThread().getService().schedule(() -> {
                Bukkit.getScheduler().runTask(Cataclysm.getInstance(), () -> {
                    this.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_SHOOT, 2F, 1.28F);
                    this.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_SHOOT, 4F, .8F);
                    this.hydra.playSound(Sound.ENTITY_ENDER_DRAGON_SHOOT, 1F, .6F);
                    new CalamityCharge(this.hydra, 12, 3, 1.5).shoot(5);
                });
            }, i * 500L, TimeUnit.MILLISECONDS);
        }
    }
}
