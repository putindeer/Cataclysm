package org.cataclysm.game.pantheon.bosses.the_ragnarok.abilities;

import lombok.Getter;
import org.bukkit.Material;
import org.cataclysm.api.boss.ability.Ability;
import org.cataclysm.game.pantheon.bosses.the_ragnarok.TheRagnarok;

public abstract class RagnarokAbility extends Ability {
    public final TheRagnarok ragnarok;

    public RagnarokAbility(TheRagnarok ragnarok, Material triggerMaterial, String name, int channelTime, double cooldown) {
        super(triggerMaterial, name, channelTime, cooldown);
        this.ragnarok = ragnarok;
    }

    @Override
    public void channel() {
    }

    @Override
    public void cast() {
    }
}
