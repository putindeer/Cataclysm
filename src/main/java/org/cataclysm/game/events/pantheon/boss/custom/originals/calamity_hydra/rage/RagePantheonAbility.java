package org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.rage;

import lombok.Getter;
import org.bukkit.Material;
import org.cataclysm.game.events.pantheon.boss.PantheonAbility;
import org.cataclysm.game.events.pantheon.boss.custom.originals.calamity_hydra.PantheonHydra;
import org.jetbrains.annotations.NotNull;

@Getter
public abstract class RagePantheonAbility extends PantheonAbility {
    public final PantheonHydra hydra;
    public final int cost;

    public RagePantheonAbility(PantheonHydra hydra, Material triggerMaterial, String name, int cost) {
        super(triggerMaterial, name, 0);
        this.hydra = hydra;
        this.cost = cost;
    }

    public abstract void tick();

    @Override
    public void channel() {}

    @Override
    public void cast() {
        PantheonRage rage = this.hydra.rageManager;

        double currentRage = rage.current;
        if ((currentRage - this.cost) <= 0) return;

        rage.getManager().reassure(this.cost);
        this.tick();
    }
}
