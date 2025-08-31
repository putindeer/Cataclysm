package org.cataclysm.game.pantheon.bosses.calamity_hydra.rage;

import lombok.Getter;
import org.bukkit.Material;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.PantheonHydra;
import org.cataclysm.game.pantheon.bosses.calamity_hydra.abilities.HydraAbility;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class RageAbility extends HydraAbility {
    private final int costPerTick;
    private final boolean broadcast;

    public RageAbility(@NotNull PantheonHydra hydra, Material triggerMaterial, String name, double cooldownPerTick, int costPerTick, boolean broadcast) {
        super(hydra, triggerMaterial, "<#7829a3>" + name, 0, cooldownPerTick);
        this.costPerTick = costPerTick;
        this.broadcast = broadcast;
    }

    @Override
    public void channel() {}

    @Override
    public void cast() {
        HydraRage rage = this.hydra.rage;

        double currentRage = rage.current;
        if ((currentRage - this.costPerTick) <= 0) return;

        rage.getManager().reassure(this.costPerTick);
        this.tick();
    }

    public abstract void tick();
}
