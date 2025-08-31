package org.cataclysm.game.events.pantheon.boss.ragnarok.abilities;

import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.events.pantheon.boss.ragnarok.TheRagnarok;

public abstract class RagnarokAbility extends Ability {
    public final TheRagnarok ragnarok;

    public RagnarokAbility(TheRagnarok ragnarok, Material triggerMaterial, String name, int channelTime) {
        super(triggerMaterial, name, channelTime, 3);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {
    }

    @Override
    public void cast() {
    }
}
